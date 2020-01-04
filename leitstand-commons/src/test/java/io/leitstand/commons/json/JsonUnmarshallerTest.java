/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.json.JsonUnmarshaller.unmarshal;
import static javax.json.Json.createObjectBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class JsonUnmarshallerTest {

	@Test
	public void null_value_is_mapped_to_null() {
		assertNull(unmarshal(FakeJsonObject.class,null));
	}
	
	
	@Test
	public void read_non_null_object() {
		FakeJsonObject object = unmarshal(FakeJsonObject.class,
										  createObjectBuilder()
										  .add("name", "foo")
										  .add("value", "bar")
										  .build() );
		assertEquals("foo",object.getName());
		assertEquals("bar",object.getValue());
	}
	
}
