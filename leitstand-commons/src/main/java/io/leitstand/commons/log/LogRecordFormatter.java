/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import static io.leitstand.commons.model.StringUtil.isEmptyString;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.LogRecord;

/**
 * Formats a <code>LogRecord</code> by executiong a list of functions to extract information from the log record in a certain order.
 */
class LogRecordFormatter {

	private List<Function<LogRecord,String>> segments;
	
	/**
	 * Create a <code>LogRecordFormatter</code>.
	 */
	LogRecordFormatter(){
		this.segments = new LinkedList<>();
	}
	
	/**
	 * Adds a new extractor to the formatter.
	 * @param extractor - the funcation to extract information from the log record.
	 */
	void add(Function<LogRecord,String> extractor) {
		segments.add(extractor);
	}
	
	/**
	 * Adds a string that shall be used as delimiter between two log record extractions.
	 * @param s - the delimiter string.
	 */
	void add(String s) {
		if(isEmptyString(s)) {
			return;
		}
		segments.add(r -> s);
	}

	/**
	 * Formats the log record into a string.
	 * @param record - the log record to be formatted.
	 * @return the formatted log record string
	 */
	String format(LogRecord record) {
		StringBuilder buffer = new StringBuilder();
		for(Function<LogRecord,String> segment : segments) {
			buffer.append(segment.apply(record));
		}
		return buffer.toString();
	}
	
}
