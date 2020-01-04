/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;

/**
 * A bunch of string utilities.
 */
public final class StringUtil {

	/**
	 * Checks whether the specified string is <code>null</code> or <i>empty</i> (<code>""</code>).
	 * @param s - the string to be tested
	 * @return <code>true</code> if the string is <code>null</code> or empty, otherwise <code>false</code>.
	 */
	public static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}

	public static <T> String asString(T o, 
									  Function<T,String> m,
									  String defaultString) {
		if(o == null) {
			return defaultString;
		}
		return asString(m.apply(o),
						defaultString);
		
	}

	
	
	public static String asString(Object o, 
								  String defaultString) {
		if(o == null) {
			return defaultString;
		}
		return o.toString();
	}
	
	
	
	/**
	 * Checks whether the specified string is neither <code>null</code> nor empty.
	 * @param s the string to check
	 * @return <code>true</code> if the string is neither <code>null</code> nor empty.
	 */
	public static boolean isNonEmptyString(String s) {
		return s != null && s.length() > 0;
	}
	
	/**
	 * Converts the specified string to UTF-8 bytes. This method expects that the passed string is not <code>null</code>.
	 * @param s the string to be converted into an UTF-8 encoded byte array
	 * @return the UTF-8 bytes.
	 */
	public static byte[] toUtf8Bytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Cannot occur since UTF-8 must be supported by all JVM implementations.
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Converts the given byte array to a string using UTF-8 character encoding.
	 * @param bytes  the UTF-8 bytes
	 * @return the string representation of the UTF-8 byte array
	 */
	public static String fromUtf8Bytes(byte[] bytes) {
		try {
			return new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Cannot occur since UTF-8 must be supported by all JVM implementations.
			throw new UncheckedIOException(e);
		}
	}
	
	
	
	private StringUtil() {
		// No instances allowed
	}
}
