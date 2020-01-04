/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanConverterTest {
	
	private BooleanConverter converter = new BooleanConverter();
	
	@Test
	public void null_string_is_mapped_to_false() throws Exception{
		assertFalse(converter.convertToEntityAttribute(null));
	}
	
	@Test
	public void null_boolean_is_mapped_to_false() throws Exception{
		assertEquals("N",converter.convertToDatabaseColumn(null));
	}
		
	@Test
	public void empty_string_is_mapped_to_false() throws Exception{
		assertFalse("N",converter.convertToEntityAttribute(""));
	}
	
	@Test
	public void boolean_is_mapped_to_string() throws Exception{
		assertEquals("N",converter.convertToDatabaseColumn(Boolean.FALSE));
		assertEquals("Y",converter.convertToDatabaseColumn(Boolean.TRUE));

	}
	
	@Test
	public void string_is_mapped_to_boolean() throws Exception{
		assertTrue(converter.convertToEntityAttribute("Y"));
		assertFalse(converter.convertToEntityAttribute("N"));

	}
	
	@Test
	public void malformed_url_string_raises_exception() throws Exception{
		assertFalse(converter.convertToEntityAttribute("foo"));
	}
}
