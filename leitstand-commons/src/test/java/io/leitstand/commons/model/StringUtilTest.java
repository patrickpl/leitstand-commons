/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.StringUtil.asString;
import static io.leitstand.commons.model.StringUtil.isEmptyString;
import static io.leitstand.commons.model.StringUtil.isNonEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import javax.security.enterprise.CallerPrincipal;

import org.junit.Test;

public class StringUtilTest {

	
	@Test
	public void null_is_empty_string() {
		assertFalse(isNonEmptyString(null));
		assertTrue(isEmptyString(null));
	}
	
	@Test
	public void empty_string_is_detected_properly() {
		assertTrue(isEmptyString(""));
		assertFalse(isNonEmptyString(""));
	}
	
	@Test
	public void blank_is_non_empty() {
		assertTrue(isNonEmptyString(" "));
		assertFalse(isEmptyString(" "));
	}
	
	@Test
	public void null_is_mapped_to_default_string() {
		assertEquals("default",asString(null, "default"));
		Principal principal = null;
		assertEquals("default",asString(principal,Principal::getName, "default"));
	}
	
	
	@Test
	public void object_is_mapped_to_string() {
		Principal principal = new CallerPrincipal("test");
		assertEquals(principal.toString(),asString(principal, "default"));
		assertEquals(principal.getName(),asString(principal,Principal::getName, "default"));
				
		
	}
	
	
}
