/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jsonb;

import static io.leitstand.commons.json.SerializableJsonObject.serializable;
import static io.leitstand.commons.json.SerializableJsonObject.unwrap;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;

import io.leitstand.commons.json.SerializableJsonObject;

public class SerializableJsonObjectTest {

	private SerializableJsonObject object;
	private JsonObject json;
	
	@Before
	public void initTestEnvironment() {
		this.json = mock(JsonObject.class);
		object = new SerializableJsonObject(json);
	}
	
	@Test
	public void unwrap_accepts_null_value() {
		assertNull(unwrap(null));
	}
	
	@Test
	public void serialize_accepts_null_value() {
		assertNull(serializable(null));
	}
	
	@Test
	public void do_not_serialize_already_serialized_objects() {
		assertSame(object, serializable(object));
		assertSame(json, serializable(serializable(json)).unwrap());
	}
	
}
