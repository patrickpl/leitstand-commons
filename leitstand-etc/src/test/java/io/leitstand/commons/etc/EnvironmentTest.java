/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.Environment.getSystemProperty;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentTest {

	@Test
	public void system_property_overrides_default_value() {
		assertEquals("default",getSystemProperty("set-property","default"));
		System.setProperty("set-property","foobar");
		assertEquals("foobar",getSystemProperty("set-property","default"));
	}
	
}
