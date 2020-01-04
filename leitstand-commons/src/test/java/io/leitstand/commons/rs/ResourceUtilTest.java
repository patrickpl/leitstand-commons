/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.rs.ResourceUtil.tryParseInt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ResourceUtilTest {

	
	@Test
	public void null_string_is_translated_to_default_value() {
		assertEquals(10,tryParseInt(null, 10));
	}
	
	@Test
	public void empty_string_is_translated_to_default_value() {
		assertEquals(10,tryParseInt("", 10));
	}
	
	@Test
	public void integer_string_is_translated_to_int() {
		assertEquals(20,tryParseInt("20", 10));

	}
	
	@Test
	public void float_string_is_translated_to_default_value() {
		assertEquals(10,tryParseInt("20.5", 10));

	}
	
	@Test
	public void text_string_is_translated_to_default_value() {
		assertEquals(10,tryParseInt("abc", 10));
	}
}
