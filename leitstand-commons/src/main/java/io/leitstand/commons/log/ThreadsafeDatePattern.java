/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import static java.lang.ThreadLocal.withInitial;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class to cache a <code>SimpleDateFormat</code> per thread, since <code>SimpleDateFormat</code> is not thread-safe.
 */
class ThreadsafeDatePattern {

	private ThreadLocal<SimpleDateFormat> cache;

	/**
	 * Creates a <code>ThreadsafeDatePattern</code>.
	 * @param pattern the simple date format
	 */
	ThreadsafeDatePattern(String pattern){
		this.cache = withInitial(()->new SimpleDateFormat(pattern));
	}
	
	/**
	 * Formats a date according to the specified simple date format
	 * @param date the date to format
	 * @return the formatted date.
	 */
	String format(Date date) {
		return cache.get().format(date);
	}
	
}
