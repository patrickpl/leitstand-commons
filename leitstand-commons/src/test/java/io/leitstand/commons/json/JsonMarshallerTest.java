/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.json.JsonMarshaller.marshal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JsonMarshallerTest {

	@Test
	public void null_value_is_mapped_to_null() {
		assertNull(marshal(null));
	}
	
	
	@Test
	public void can_serialize_object_to_json() {
		assertEquals("{\"name\":\"name\",\"value\":\"value\"}",marshal(new FakeJsonObject()).toString());
	}

	@Test
	public void can_serialize_map_to_json() {
		Map<String,String> map = new HashMap<>();
		map.put("name", "value");
		
		assertEquals("{\"name\":\"value\"}",marshal(map).toString());
	}
	
	
	@Test
	public void can_serialize_map_with_nested_objects_to_json() {
		Map<String,Object> map = new HashMap<>();
		map.put("nested",new FakeJsonObject());
		
		assertEquals("{\"nested\":{\"name\":\"name\",\"value\":\"value\"}}",marshal(map).toString());
	}
	
}
