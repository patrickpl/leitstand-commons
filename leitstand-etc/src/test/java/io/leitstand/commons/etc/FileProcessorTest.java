/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.FileProcessor.yaml;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FileProcessorTest {

	@Test
	public void generic_yaml_processor_returns_empty_map_if_file_url_is_null() throws IOException {
		assertTrue(yaml().process((Reader)null).isEmpty());
	}

	@Test
	public void typed_yaml_returns_empty_map_if_file_url_is_null() throws IOException {
		assertNull(yaml(DummyConfig.class).process((Reader)null));
	}
	
	@Test
	public void generic_yaml_converts_yaml_to_map() throws IOException{
		Map<String,Object> generic = yaml().process(FileProcessorTest.class.getResource("generic.yaml"));

		/* Verify that the map adheres to the structure of generic.yaml
		 * 
		 * string: STRING
		 * int: 123
		 * float: 1.5
		 * double: 1.5
		 * boolean-true: true
		 * boolean-false: false
		 * string-array:
		 * - a
		 * - b
		 * - c
		 * nested: 
		 *   name: nested
		 * nested-array:
		 * - name: item_1
		 *   value: FIRST
		 * - name: item_2
		 *   value: SECOND
		 */
		
		assertEquals("STRING",generic.get("string"));
		assertEquals(Integer.valueOf(123),generic.get("int"));
		assertEquals(Double.valueOf(1.5d),(Double)generic.get("float"),0.00001);
		assertEquals(Double.valueOf(1.5d),(Double)generic.get("float"),0.00001);
		assertTrue((Boolean) generic.get("boolean-true"));
		assertFalse((Boolean) generic.get("boolean-false"));
		assertArrayEquals(new String[] {"a","b","c"}, ((List<String>)generic.get("string-array")).toArray(new String[3]));
		Map<String,Object> nested = (Map<String, Object>) generic.get("nested");
		assertNotNull(nested);
		assertEquals("nested",nested.get("name"));
		List<Map<String,Object>> nestedObjects = (List<Map<String, Object>>) generic.get("nested-array");
		assertEquals(2,nestedObjects.size());
		assertEquals("item_1",nestedObjects.get(0).get("name"));
		assertEquals("FIRST",nestedObjects.get(0).get("value"));
		assertEquals("item_2",nestedObjects.get(1).get("name"));
		assertEquals("SECOND",nestedObjects.get(1).get("value"));
	}
	
	@Test
	public void typed_yaml_converty_yaml_to_java_object() throws IOException{
		DummyConfig config = yaml(DummyConfig.class).process(FileProcessorTest.class.getResource("dummy.yaml"));
		
		/* Verify that dummy.yaml has been mapped properly to DummyConfig
		 * name: junit
		 * value: test
		 */
		
		assertNotNull(config);
		assertEquals("junit",config.getName());
		assertEquals("test",config.getValue());
	}

	@Test(expected=IOException.class)
	public void typed_yaml_fails_if_yaml_contains_unknown_properties() throws IOException{
		yaml(DummyConfig.class).process(FileProcessorTest.class.getResource("generic.yaml"));
	}
	
}
