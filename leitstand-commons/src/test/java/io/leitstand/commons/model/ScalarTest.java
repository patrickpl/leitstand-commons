/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class ScalarTest {
	
	public static final class UnitTestScalar extends Scalar<String>{

		private static final long serialVersionUID = 1L;

		private String value;
		
		public UnitTestScalar(String value) {
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
	}
	

	@Test
	public void empty_string_is_mapped_to_null() {
		assertNull(UnitTestScalar.fromString("", UnitTestScalar::new));
	}
	
	@Test
	public void null_string_is_mapped_to_null() {
		assertNull(UnitTestScalar.fromString(null, UnitTestScalar::new));
	}
	
	@Test
	public void nonnull_string_is_mapped_to_scalar(){
		assertEquals(new UnitTestScalar("unit"),UnitTestScalar.fromString("unit",UnitTestScalar::new));
	}
	
	@Test
	public void null_scalar_is_mapped_to_null() {
		assertNull(UnitTestScalar.toString(null));
	}
	
	@Test
	public void nonnull_scalar_is_mapped_to_string() {
		assertEquals("unit",UnitTestScalar.toString(new UnitTestScalar("unit")));
	}
	
	@Test
	public void scalar_equals_null_is_false() {
		UnitTestScalar scalar = new UnitTestScalar("foo");
		assertFalse(scalar.equals(null));
	}
	
	@Test
	public void scalar_equals_is_reflective() {
		UnitTestScalar scalar = new UnitTestScalar("foo");
		assertTrue(scalar.equals(scalar));
	}
	
	@Test
	public void scalar_equals_is_transitive() {
		UnitTestScalar a0 = new UnitTestScalar("a");
		UnitTestScalar a1 = new UnitTestScalar("a");
		UnitTestScalar a2 = new UnitTestScalar("a");
		
		assertTrue(a0.equals(a1) == a1.equals(a2) == a0.equals(a2));

	}
	
	@Test
	public void scalar_equals_is_symmetric() {
		UnitTestScalar a0 = new UnitTestScalar("a0");
		UnitTestScalar a1 = new UnitTestScalar("a1");
		UnitTestScalar b = new UnitTestScalar("b");
		
		assertTrue(a0.equals(a1) == a1.equals(a0));
		assertTrue(a0.equals(b) == b.equals(a0));
	}
	
	@Test
	public void scalar_does_not_equal_other_type() {
		UnitTestScalar a = new UnitTestScalar("a");
		assertFalse(a.equals(mock(Scalar.class)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannot_compare_scalar_of_different_types() {
		UnitTestScalar a = new UnitTestScalar("a");
		a.compareTo(mock(Scalar.class));
	}
	
	@Test
	public void can_compare_scalar_of_same_type() {
		UnitTestScalar a = new UnitTestScalar("a");
		UnitTestScalar b = new UnitTestScalar("b");
		
		assertTrue(a.compareTo(b) == "a".compareTo("b"));
		assertTrue(b.compareTo(a) == "b".compareTo("a"));
				
	}
	
}
