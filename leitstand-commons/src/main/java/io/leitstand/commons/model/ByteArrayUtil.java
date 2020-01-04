/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static java.lang.Character.MAX_RADIX;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

import java.math.BigInteger;

/**
 * A bunch of byte array utilities.
 */
public final class ByteArrayUtil {

	/**
	 * Converts a byte array to a <a href="https://en.wikipedia.org/wiki/Base36">Base36</a> encoded string, a radix-36 representation of a byte array using arabic numerals 0-9
	 * and latin letters A-Z. Consequently the byte array is represented by an alphanumeric string.
	 * @param data - the bytes to be encoded 
	 * @return the Base36 encoded string
	 */
	public static String encodeBase36String(byte[] data) {
		return new BigInteger(data).abs().toString(MAX_RADIX);
	}
	
	/**
	 * Decodes a <a href="https://en.wikipedia.org/wiki/Base36">Base36</a> encoded string to a byte array.
	 * @param data - the Base36 string
	 * @return the decoded byte array
	 */
	public static byte[] decodeBase36String(String data) {
		return new BigInteger(data,MAX_RADIX).abs().toByteArray();
	}
	
	/**
	 * Converts a byte array to a <a href="https://en.wikipedia.org/wiki/Base64">Base64</a> encoded string.
	 * @param data - the bytes to be encoded 
	 * @return the Base64 encoded string
	 */
	public static String encodeBase64String(byte[] bytes) {
		return getEncoder().encodeToString(bytes);
	}

	/**
	 * Decodes a <a href="https://en.wikipedia.org/wiki/Base64">Base64</a> encoded string to a byte array.
	 * @param data - the Base64 string
	 * @return the decoded byte array
	 */
	public static byte[] decodeBase64String(String base64) {
		return getDecoder().decode(base64);
	}

	private ByteArrayUtil() {
		// No instances allowed
	}
}
