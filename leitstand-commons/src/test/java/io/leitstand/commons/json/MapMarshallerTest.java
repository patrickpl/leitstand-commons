/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.json.MapMarshaller.marshal;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.Test;

public class MapMarshallerTest {

	@Test
	public void adaptToJson_basic_JSON_types_properly() {
		Map<String,Object> map = new HashMap<>();
		map.put("string","string");
		map.put("int", 0);
		map.put("Integer",Integer.valueOf(0));
		map.put("boolean", true);
		map.put("Boolean", TRUE);
		map.put("BigInteger", new BigInteger("1234567890"));
		map.put("BigDecimal", new BigDecimal("1234567890"));
		map.put("long",1L);
		map.put("Long",Long.valueOf(1));
		map.put("double",1.0d);
		map.put("Double",Double.valueOf(1.0));
		map.put("null", null);
		
		JsonObject json = marshal(map).toJson();
		assertEquals("string",json.getString("string"));
		assertEquals(0,json.getInt("int"));
		assertEquals(0,json.getInt("Integer"));
		assertTrue(json.getBoolean("boolean"));
		assertTrue(json.getBoolean("Boolean"));
		assertEquals( new BigInteger("1234567890"),json.getJsonNumber("BigInteger").bigIntegerValue());
		assertEquals( new BigDecimal("1234567890"),json.getJsonNumber("BigDecimal").bigDecimalValue());
		assertEquals(1L, json.getJsonNumber("long").longValue());
		assertEquals(1L, json.getJsonNumber("Long").longValue());
		assertEquals(1.0, json.getJsonNumber("double").doubleValue(),0.0001);
		assertEquals(1.0, json.getJsonNumber("Double").doubleValue(),0.0001);
		assertTrue(json.isNull("null"));
	}

	@Test
	public void empty_collection_is_not_mapped_to_null() {
		Map<String,Object> map = new HashMap<>();
		map.put("empty-list", emptyList());
		map.put("empty-set",emptySet());
		
		JsonObject json = marshal(map).toJson();
		assertTrue(json.getJsonArray("empty-list").isEmpty());
		assertTrue(json.getJsonArray("empty-set").isEmpty());
	}
	
	@Test
	public void adaptToJson_nested_objects_properly() {
		Map<String,Object> root = new HashMap<String,Object>();
		Map<String,Object> nested = new HashMap<String,Object>();
		nested.put("a","A");
		nested.put("b","B");
		root.put("nested", nested);
		
		JsonObject json = marshal(root).toJson();
		
		JsonObject nestedJson = json.getJsonObject("nested");
		assertEquals("A",nestedJson.getString("a"));
		assertEquals("B",nestedJson.getString("b"));
	}

	@Test
	public void adaptToJson_scalar_arrays_properly() {
		Map<String,Object> map = new HashMap<>();
		map.put("string-array",asList("a","b","c"));
		map.put("int-array", asList(1,2,3));
		map.put("boolean-array", asList(TRUE,FALSE));
		map.put("bigdecimal-array", asList(new BigDecimal("1"), new BigDecimal("2")));
		map.put("biginteger-array", asList(new BigInteger("1"), new BigInteger("2")));

		
		JsonObject json = marshal(map).toJson();
		
		assertEquals(3,json.getJsonArray("string-array").size());
		assertEquals("a",json.getJsonArray("string-array").getString(0));
		assertEquals("b",json.getJsonArray("string-array").getString(1));
		assertEquals("c",json.getJsonArray("string-array").getString(2));
	
		assertEquals(3,json.getJsonArray("int-array").size());
		assertEquals(1,json.getJsonArray("int-array").getInt(0));
		assertEquals(2,json.getJsonArray("int-array").getInt(1));
		assertEquals(3,json.getJsonArray("int-array").getInt(2));
		
		assertEquals(2,json.getJsonArray("boolean-array").size());
		assertTrue(json.getJsonArray("boolean-array").getBoolean(0));		
		assertFalse(json.getJsonArray("boolean-array").getBoolean(1));	
		
		assertEquals(2,json.getJsonArray("bigdecimal-array").size());
		assertEquals(new BigDecimal("1"),json.getJsonArray("bigdecimal-array").getJsonNumber(0).bigDecimalValue());
		assertEquals(new BigDecimal("2"),json.getJsonArray("bigdecimal-array").getJsonNumber(1).bigDecimalValue());

		assertEquals(2,json.getJsonArray("biginteger-array").size());
		assertEquals(new BigInteger("1"),json.getJsonArray("biginteger-array").getJsonNumber(0).bigIntegerValue());
		assertEquals(new BigInteger("2"),json.getJsonArray("biginteger-array").getJsonNumber(1).bigIntegerValue());
	}
	
	@Test
	public void adaptToJson_arrays_of_nested_objects_properly() {
		Map<String,Object> root = new HashMap<>();
		Map<String,Object> item1 = new HashMap<>();
		item1.put("name", "item 1");
		Map<String,Object> item2 = new HashMap<>();
		item2.put("name","item 2");
		root.put("items",asList(item1,item2));
		
		JsonObject json = marshal(root).toJson();
		
		JsonArray array = json.getJsonArray("items");
		assertEquals(2,array.size());
		assertEquals("item 1", array.getJsonObject(0).getString("name"));
		assertEquals("item 2", array.getJsonObject(1).getString("name"));
		
	}
	
}
