/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import static io.leitstand.commons.log.DiagnosticContext.getContext;
import static io.leitstand.commons.model.StringUtil.isEmptyString;
import static io.leitstand.commons.model.StringUtil.isNonEmptyString;
import static java.lang.Character.isDigit;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Arrays.fill;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.LogRecord;

/**
 * Parser to process the {@link PatternFormatter} format to create a {@link LogRecordFormatter} that formats all log records.
 */
class PatternParser {

	private static final char PARAMETER    = '%';
	private static final char DIAGNOSTIC   = 'x';
	private static final char LOGGER	   = 'c';
	private static final char MESSAGE	   = 's';
	private static final char NEWLINE	   = 'n';
	private static final char PRIORITY	   = 'p';
	private static final char THREAD       = 't';
	private static final char THROWN	   = 'e';
	private static final char TIMESTAMP    = 'd';
	private static final char START_FORMAT = '{';
	private static final char END_FORMAT   = '}';
	private static final char DOT 		   = '.';
	 
	
	/**
	 * Returns <code>true</code> if the character is one of the given chars.
	 * @param c - the character to test
	 * @param chars - the compare values.
	 * @return <code>true</code> if the character is one of the given chars.
	 */
	static boolean is(char c, char... chars) {
		for(char test : chars) {
			if(c == test) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Raises an exception if the given char is not the expected char.
	 * @param c - the character to test
	 * @param test - the expected character.
	 */
	static void assertThat(char c, char test) {
		if(c != test) {
			throw new IllegalStateException("Expected "+test+" but read "+c);
		}
	}
	
	/**
	 * Parses the pattern and creates a <code>LogRecordFormatter</code>.
	 * @param pattern - the log record pattern
	 * @return the <code>LogRecordFormatter</code> to format all log records.
	 */
	LogRecordFormatter parse(String pattern) {
		LogRecordFormatter formatter = new LogRecordFormatter();
		
		StringBuilder buffer = new StringBuilder();
		int i=0;
		while(i < pattern.length()) {
			char c = pattern.charAt(i);
			if(is(c,PARAMETER)) {
				formatter.add(buffer.toString());
				i++; // Consume PARAMETER
				i = scanParameter(formatter,
								  pattern,
								  i);
				// Create new buffer for next glue code
				i++; //Consume PARAMETER character
				buffer = new StringBuilder();
				continue;
			}
			buffer.append(c);
			i++; // Consume character
		}
		if(buffer.length() > 0) { 
			formatter.add(buffer.toString());
		}
		return formatter;
	}
	
	/**
	 * Scans a parameter an creates a lambda expression to extracts the parameter from the log record.
	 * @param formatter - the log record formatter under construction
	 * @param pattern - the pattern string
	 * @param offset - the current position being parsed.
	 * @return the offset after the parameter was processed.
	 */
	private int scanParameter(LogRecordFormatter formatter,
							  String pattern, 
							  int offset) {
		int i=offset;
		StringBuilder padding = new StringBuilder();
		boolean dot = false;
		for(; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			switch(c) {
				case DIAGNOSTIC: return createDiagnosticContextSegment(formatter, i, padding);
				case LOGGER:	 return dot ? createLoggerSegment(formatter, i, padding) : createLoggerSegment(formatter,pattern,i,padding);
				case MESSAGE:	 return createMessageSegment(formatter, i, padding);
				case PRIORITY: 	 return createLevelSegment(formatter, i, padding);
				case THREAD: 	 return createThreadSegment(formatter, i);
				case THROWN:	 return createThrownSegment(formatter, i, padding);
				case TIMESTAMP:	 return createTimestampSegment(formatter,pattern,offset);
				case NEWLINE: 	 return createNewLineSegment(formatter, i);
				case PARAMETER:	 return createParameterSegment(formatter, i);
				case DOT:{
					dot = true;
					continue; // Continue with next item.
				}
				default:{
					if(isDigit(pattern.charAt(i))) {
						padding.append(pattern.charAt(i));
					}
				}
			}
		}
		return i;
	}

	/**
	 * Adds a segment to extract the logger of a log record.
	 * The logger name is a full-qualified class name.
	 * This segment removes the first n common packages from the logger, where n is specified in the pattern
	 * (e.g. <code>%.3c</code> removes the first three packages and hence <code>net.rtbrick.rbms</code> would be removed from the logger name).
	 * @param formatter  - the formatter under construction
	 * @param offset - the current offset
	 * @param packages - the number of packages to be ignored.
	 * @return the current offset
	 */
	private int createLoggerSegment(LogRecordFormatter formatter, int offset, StringBuilder packages) {
		int pkgsToIgnore = parseInt(packages.toString()); 
		formatter.add(formattedString(record -> {
			String logger = record.getLoggerName();
			int i=-1;
			for(int c=0; c < pkgsToIgnore; c++) {
				i = logger.indexOf('.', i+1);
				if(i < 0) {
					break;
				}
			}
			return logger.substring(i+1);
		},""));
		return offset;
	}
	
	/**
	 * Adds a segment to extract the logger of a log record.
	 * The logger name is a full-qualified class name.
	 * This segment considers only the simple class name and the last packages from the logger, where n is specified in the pattern
	 * (e.g. <code>%.3c</code> extracts <code>inventory.model.DefaultElementSettingsService</code> from 
	 * <code>net.rtbrick.rbms.mod.inventory.model.DefaultElementSettingsService</code> would be removed from the logger name).
	 * @param formatter  - the formatter under construction
	 * @param offset - the current offset
	 * @param packages - the number of packages to be ignored.
	 * @return the current offset
	 */
	private int createLoggerSegment(LogRecordFormatter formatter, String pattern, int offset, StringBuilder padding) {
		int i = offset;
		if(lookahead(pattern, i, lookahead -> is(lookahead,DOT))) {
			i+=2; // Consume DOT
			StringBuilder buffer = new StringBuilder();
			for(;i<pattern.length() && isDigit(pattern.charAt(i)); i++){
				buffer.append(pattern.charAt(i));
			}
			int pkgsToInclude = buffer.length() > 0 ? parseInt(buffer.toString()) : 1;
			if(pkgsToInclude == 1) {
				formatter.add(formattedString(r -> r.getLoggerName().substring(r.getLoggerName().lastIndexOf('.')+1),
											  padding(padding)));
				return i+1;
			}
			formatter.add(formattedString(record -> {
				String logger = record.getLoggerName();
				int j = logger.length();
				for(int c=0; c < pkgsToInclude; c++) {
					j = logger.lastIndexOf('.', j-1);
					if(j < 0) {
						break;
					}
				}
				return logger.substring(j+1);
			},padding(padding)));
			return i+1;
		}
		formatter.add(formattedString(LogRecord::getLoggerName,
									  padding(padding)));
		return offset; // Consume LOGGER
	}

	/**
	 * Adds a segment to print the <code>%</code> character.
	 * @param formatter the formatter under construction.
	 * @param i the current offset
	 * @return the new offset
	 */
	private int createParameterSegment(LogRecordFormatter formatter, int i) {
		formatter.add("%");
		return i; // Consume escaped PARAMETER
	}

	/**
	 * Adds a new-line segment.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @return the new offset
	 */
	private int createNewLineSegment(LogRecordFormatter formatter, int i) {
		formatter.add("\n");
		return i; // Consume NEW LINE
	}

	/**
	 * Adds the log record timestamp as formatted date.
	 * @param formatter the formatter under construction
	 * @param pattern the log record pattern
	 * @param offset the current offset
	 * @return the new offset
	 */
	private int createTimestampSegment(LogRecordFormatter formatter, String pattern, int offset) {
		int i = offset;
		i++; // Consume TIMESTAMP
		if(i < pattern.length() && is(pattern.charAt(i),START_FORMAT)) {
			i++; // Consume START_FORMAT
			String datePattern = pattern.substring(i, pattern.indexOf(END_FORMAT, i));
			i+=datePattern.length();
			assertThat(pattern.charAt(i),END_FORMAT);
			formatter.add(formattedDate(r -> new Date(r.getMillis()), datePattern));
			return i;
		}
		formatter.add(r -> new Date(r.getMillis()).toString());
		return i;
	}

	/**
	 * Adds a segment to report the Throwable reported by this log record.
	 * Reports an empty string, if no Throwable has been reported.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @param padding padding to  be added if a throwable exists
	 * @return the new offset
	 */
	private int createThrownSegment(LogRecordFormatter formatter, int i, StringBuilder padding) {
		formatter.add(formattedThrown(LogRecord::getThrown,
									  padding(padding)));
		return i; // Consume THROWN
	}

	/**
	 * Adds a segment to report which thread created the log record.
	 * Reports the tread name, if resolvable, and the thread ID otherwise.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @return the new offset
	 */
	private int createThreadSegment(LogRecordFormatter formatter, int i) {
		formatter.add(formattedThread(LogRecord::getThreadID));
		return i; // Consume THREAD
	}

	/**
	 * Adds a segment to report the log record severity level.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @param padding the left padding if a level exists
	 * @return the new offset
	 */
	private int createLevelSegment(LogRecordFormatter formatter, int i, StringBuilder padding) {
		formatter.add(formattedString(r -> r.getLevel().getName(),
									  padding(padding)));
		return i; // Consume PRIORITY
	}

	/**
	 * Adds a segment to report the log message.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @param padding the left padding if a message exists
	 * @return the new offset
	 */
	private int createMessageSegment(LogRecordFormatter formatter, int i, StringBuilder padding) {
		formatter.add(formattedString(LogRecord::getMessage,
									  padding(padding)));
		return i; // Consume MESSAGE
	}

	/**
	 * Adds a segment to report the diagnostic context.
	 * @param formatter the formatter under construction
	 * @param i the current offset
	 * @param padding the left padding if a diagnostic context exists
	 * @return the new offset
	 */
	private int createDiagnosticContextSegment(LogRecordFormatter formatter, int i, StringBuilder padding) {
		formatter.add(formattedString(r -> getContext(),
									  padding(padding)));
		return i; // Consume DIAGNOSTIC
	}
	
	/**
	 * Tests whether the next character has the given predicate
	 * @param pattern the log record pattenr
	 * @param offset the current offset
	 * @param predicate the predicated to look for
	 * @return <code>true</code> if the predicate test is passed, <code>false</code> otherwise.
	 */
	private static boolean lookahead(String pattern, 
									 int offset, 
									 Predicate<Character> predicate) {
		int i = offset+1;
		return i < pattern.length() && predicate.test(pattern.charAt(i));
		
	}
	
	/**
	 * Computes the configured left padding.
	 * @param builder the configured padding
	 * @return the left padding or empty string if no padding was defined.
	 */
	private static String padding(StringBuilder builder) {
		if(builder.length() == 0) {
			return "";
		}
		char[] padding = new char[Integer.parseInt(builder.toString())];
		fill(padding, ' ');
		return new String(padding); 
	}
	
	/**
	 * Adds left padding to a string, if the string is not <code>null</code> or empty.
	 * @param extractor the lambda to read the string value from the record
	 * @param padding the left padding to add
	 * @return lambda to format the log string property
	 */
	private Function<LogRecord,String> formattedString(Function<LogRecord,String> extractor, String padding) {
		if(isEmptyString(padding)) {
			return extractor;
		}
		return record -> {
			String s = extractor.apply(record);
			if(isNonEmptyString(s)) {
				return padding+s;
			}
			return "";
		};
	}
	
	/**
	 * Adds left padding to a throwable, if the throwable is not <code>null</code>.
	 * @param extractor the lambda to read the string value from the record
	 * @param padding the left padding to add
	 * @return lambda to format the log record thrown-info
	 */
	private Function<LogRecord,String> formattedThrown(Function<LogRecord,Throwable> extractor, String padding) {
		return record -> {
			Throwable thrown = extractor.apply(record);
			if(thrown == null) {
				return "";
			}
			
			try(StringWriter buffer = new StringWriter();
				PrintWriter writer = new PrintWriter(buffer)){
				writer.write(padding);
				extractor.apply(record).printStackTrace(writer);
				return buffer.toString();
			} catch (IOException e) {
				return extractor.apply(record).toString();
			}
		};
	}

	
	/**
	 * Formats a thread by trying to resolve the thread name. Defaults to thread-ID if name is not resolvable.
	 * @param extractor the lambda to read the string value from the record
	 * @return lambda to format the log record thread info
	 */
	private Function<LogRecord,String> formattedThread(Function<LogRecord,Integer> extractor) {
		Thread thread = Thread.currentThread();
		return record -> extractor.apply(record) == thread.getId() ? thread.getName() : format("Thread: %d",record.getThreadID());
	}

	/**
	 * Formats a date by applying the simple date format.
	 * @param extractor the lambda to read the string value from the record
	 * @param format the date format
	 * @return lambda to format log record timestamp
	 */
	private Function<LogRecord,String> formattedDate(Function<LogRecord,Date> extractor, String format) {
		if(format.length() == 0) {
			return record -> extractor.apply(record).toString();
		}
		ThreadsafeDatePattern pattern = new ThreadsafeDatePattern(format);
		return record -> pattern.format(extractor.apply(record));
	}
	
}
