/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.json.MapUnmarshaller.unmarshal;
import static java.lang.Boolean.TRUE;
import static javax.json.Json.createObjectBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.Test;

public class MapUnmarshallerTest {

	@Test
	public void adaptToJson_basic_JSON_types_properly() {
		JsonObject object = createObjectBuilder()
							.add("string", "string")
							.add("int", 0)
							.add("Integer", Integer.valueOf(0))
							.add("boolean", true)
							.add("Boolean", TRUE)
							.add("BigInteger", new BigInteger("1234567890"))
							.add("BigDecimal", new BigDecimal("1.234567890"))
							.add("long",1L)
							.add("Long", Long.valueOf(1))
							.add("double",1.0d)
							.add("Double", Double.valueOf(1.0))
							.addNull("null")
							.build();
		
		

		Map<String,Object> map = unmarshal(object).toMap();
		assertEquals("string", map.get("string"));
		assertEquals(0, ((BigInteger)map.get("int")).intValue());
		assertEquals(0,  ((BigInteger)map.get("Integer")).intValue());
		assertTrue((boolean)map.get("boolean"));
		assertTrue((Boolean)map.get("Boolean"));
		assertEquals(new BigInteger("1234567890"),map.get("BigInteger"));
		assertEquals(new BigDecimal("1.234567890"),map.get("BigDecimal"));
		assertEquals(1L, ((BigInteger)map.get("long")).longValue());
		assertEquals(1L, ((BigInteger)map.get("long")).longValue());
		assertEquals(1.0d,((BigDecimal)map.get("double")).doubleValue(),0.005);
		assertEquals(1.0d,((BigDecimal)map.get("Double")).doubleValue(),0.005);
		assertNull(map.get("null"));
		assertTrue(map.containsKey("null"));
			
		
	}

	
	@Test
	public void adaptToJson_nested_objects_properly() {
		JsonObject root = createObjectBuilder()
						  .add("nested", createObjectBuilder()
								  		 .add("a", "A")
								  		 .add("b","B"))
						  .build();
		
		
		Map<String,Object> map = unmarshal(root).toMap();
		Map<String,Object> nestedMap = (Map<String,Object>) map.get("nested");
		
		assertEquals("A",nestedMap.get("a"));
		assertEquals("B",nestedMap.get("b"));
	}

}
