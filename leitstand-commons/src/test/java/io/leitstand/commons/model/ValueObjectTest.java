/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
public class ValueObjectTest {

	private static class BaseComposite extends ValueObject {
		
		private static final long serialVersionUID = 1L;

		private String a;
		private String b;
		
		public BaseComposite(String a, String b){
			this.a = a;
			this.b = b;
		}
		
		public String getA() {
			return a;
		}
		
		public String getB() {
			return b;
		}
	}
	
	private static class SubComposite extends BaseComposite {
		
		private static final long serialVersionUID = 1L;

		private String c;
		private String d;
			
		public SubComposite(String a, String b, String c, String d) {
			super(a,b);
			this.c = c;
			this.d = d;
		}
		
		public String getC() {
			return c;
		}
		
		public String getD() {
			return d;
		}
		
	}
	
	
	private BaseComposite ab;
	private BaseComposite copy_of_ab;
	private BaseComposite ac;
	private BaseComposite anull;
	private SubComposite abcd;
	
	@Before
	public void setup_test_data(){
		ab = new BaseComposite("A", "B");
		copy_of_ab = new BaseComposite("A", "B");
		ac = new BaseComposite("a","c");
		anull = new BaseComposite("a",null);
		abcd = new SubComposite("A", "B", "C", "D");
	}
	
	@Test
	public void same_object_has_same_hashcode(){
		int h1 = ab.hashCode();
		int h2 = ab.hashCode();
		assertTrue(h1 == h2);
	}
	
	@Test
	public void equal_objects_have_same_hashcode(){
		int h1 = ab.hashCode();
		int h2 = copy_of_ab.hashCode();
		assertTrue(h1 == h2);
	}

	@Test
	public void hash_code_considers_all_properties(){
		int h1 = ab.hashCode();
		int h2 = Objects.hash(abcd.getA(),abcd.getB(), ab.getClass());
		assertTrue(h1 == h2);
	}
	
	@Test
	public void hash_code_considers_all_properties_in_type_hierarchy(){
		int h1 = abcd.hashCode();
		int h2 = Objects.hash(abcd.getA(),abcd.getB(),abcd.getC(),abcd.getClass(), abcd.getD());
		assertTrue(h1 == h2);
	}
	
	@Test
	public void equal_is_reflective(){
		assertTrue(ab.equals(ab));
	}

	@Test
	public void equal_is_symmetric(){
		assertTrue(ab.equals(copy_of_ab) == copy_of_ab.equals(ab));
		assertTrue(ab.equals(ac) == ac.equals(ab));
		assertTrue(ab.equals(copy_of_ab));
		assertFalse(ab.equals(ac));
	}

	
	@Test
	public void equal_is_nullsafe(){
		assertFalse(ab.equals(null));
	}
	
	@Test
	public void equals_is_nullsafe_for_contained_properties() {
		assertTrue(anull.equals(anull));
		assertFalse(anull.equals(ab));
		assertFalse(ab.equals(anull));
		assertTrue(anull.equals(new BaseComposite(anull.getA(),anull.getB())));
	}
	
	@Test
	public void string_has_expected_value(){
		assertEquals("BaseComposite[a= A, b= B]",ab.toString());
	}
	
	@Test
	public void different_types_are_not_equal() {
		assertFalse(ab.equals(abcd));
		assertFalse(abcd.equals(ab));
	}
	
}
