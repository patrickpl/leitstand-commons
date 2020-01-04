/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.BuilderUtil.assertNotInvalidated;
import static io.leitstand.commons.model.BuilderUtil.requires;

import org.junit.Test;

public class BuilderUtilTest {

	
	@Test(expected=IllegalStateException.class)
	public void throw_exception_when_instance_under_construction_is_null() {
		assertNotInvalidated(BuilderUtilTest.class, null);
	}
	
	
	@Test
	public void do_nothing_when_instance_under_construction_exists() {
		assertNotInvalidated(BuilderUtilTest.class, new Object());
	}
	
	@Test(expected=IllegalStateException.class)
	public void throw_exception_when_required_string_is_null() {
		requires(BuilderUtilTest.class, "dummy", (String)null);
	}
	
	@Test(expected=IllegalStateException.class)
	public void throw_exception_when_required_string_is_empty() {
		requires(BuilderUtilTest.class, "dummy", "");
	}
	
	@Test
	public void do_nothing_when_string_is_non_empty() {
		requires(BuilderUtilTest.class, "dummy", "abc");
	}
	
	@Test(expected=IllegalStateException.class)
	public void throw_exception_when_required_property_is_null() {
		requires(BuilderUtilTest.class, "dummy", (Object) null);
	}
	
	@Test
	public void do_nothing_when_object_is_not_null() {
		requires(BuilderUtilTest.class, "dummy", new Object());
	}
	
}
