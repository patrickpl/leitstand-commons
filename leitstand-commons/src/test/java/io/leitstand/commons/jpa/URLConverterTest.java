/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class URLConverterTest {
	
	private URLConverter converter = new URLConverter();
	private URL ref;
	private String url;
	
	@Before
	public void initDates() throws Exception {
		url = "http://www.rtbrick.com";
		ref = new URL(url);
	}
	
	
	@Test
	public void null_string_is_mapped_to_null() throws Exception{
		assertNull(converter.convertToEntityAttribute(null));
	}
	
	@Test
	public void null_URL_is_mapped_to_null() throws Exception{
		assertNull(converter.convertToDatabaseColumn(null));
	}
	
	@Test
	public void empty_string_is_mapped_to_null() throws Exception{
		assertNull(converter.convertToEntityAttribute(""));
	}
	
	@Test
	public void url_is_mapped_to_string() throws Exception{
		assertEquals(url,converter.convertToDatabaseColumn(ref));
	}
	
	@Test
	public void url_string_is_mapped_to_URL() throws Exception{
		assertEquals(ref,converter.convertToEntityAttribute(url));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void malformed_url_string_raises_exception() throws Exception{
		converter.convertToEntityAttribute("foo");
	}
}
