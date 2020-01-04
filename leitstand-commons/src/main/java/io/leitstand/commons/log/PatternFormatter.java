/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A pattern base <code>LogRecord</code> formatter. 
 * The pattern is expressed as string with paramaters for differnt log record data:
 *  <table>
 *  	<thead>
 *  		<tr><th>Parameter</th><th>Description</th></tr>
 *  	</thead>
 *  	<tbody>
 *  		<tr><td><code>%d{<i>date-format</i>}</code></td><td>The log timestamp formatted in the specified date format. The date format must be expressed as {@link SimpleDateFormat}</td></tr>
 * 			<tr><td><code>%c</code></td><td>Prints the logger name</td></tr>
 *  		<tr><td><code>%t</code></td><td>Prints the thread name or thread-id if the name is unknown</td></tr>
 *  		<tr><td><code>%s</code></td><td>Prints the log message</td></tr>
 *  		<tr><td><code>%e</code></td><td>Prints the <code>Throwable</code>, if assigned to the log record.</td></tr>
 *  		<tr><td><code>%x</code></td><td>Prints the diagnostic context</td></tr>
 *  		<tr><td><code>%n</code></td><td>Prints a new line</td></tr>
 *  		<tr><td><code>%%</code></td><td>Prints the % character.</td></tr>
 *  	</tbody>
 *  </table>
 *  RBMS uses the full-qualified class name as logger name. Use <code>%c.</code> to print the simple class name as logger name only.
 *  Use <code>%c.2</code> to print the simple class name <i>and</i> the last package segment.
 *  Alternatively, <code>%.3c</code> ignores the first three packages of a full qualified class name.
 *  For optional data, such as <code>%x</code> or <code>%e</code> a padding, as number of blanks, can be specified as padding. 
 *  The padding is applied only when the respective attribute is neither <code>null</code> nor empty.
 *  For example, <code>%s%1e</code> renders adds a blank between the log message and the exception message, if an exception exists. If no exception exists <code>%1e</code> is rendered as empty string.
 *
 */
public class PatternFormatter extends Formatter {

	private LogRecordFormatter formatter;
	
	/**
	 * Creates a <code>PatternFormatter</code>.
	 * @param pattern the log record format
	 */
	public PatternFormatter(String pattern) {
		PatternParser parser = new PatternParser();
		formatter = parser.parse(pattern);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String format(LogRecord record) {
		return formatter.format(record);
	}
	
}
