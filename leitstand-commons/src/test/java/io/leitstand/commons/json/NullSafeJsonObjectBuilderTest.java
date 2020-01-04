/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.junit.Before;
import org.junit.Test;

public class NullSafeJsonObjectBuilderTest {

	private NullSafeJsonObjectBuilder builder;
	private JsonObjectBuilder json;
	
	@Before
	public void initBuilder() {
		json = mock(JsonObjectBuilder.class);
		builder = new NullSafeJsonObjectBuilder(json);
	}
	
	@Test
	public void do_nothing_when_adding_null_string() {
		builder.add("property", (String) null);
		verifyZeroInteractions(json);
	}

	@Test
	public void can_add_empty_string() {
		builder.add("property", "");
		verify(json).add("property","");
	}
	
	@Test
	public void can_add_non_empty_string() {
		builder.add("property", "value");
		verify(json).add("property","value");
	}
	
	
	@Test
	public void do_nothing_when_adding_null_bigint() {
		builder.add("property", (BigInteger) null);
		verifyZeroInteractions(json);
	}

	@Test
	public void can_add_bigint() {
		builder.add("property", new BigInteger("1234"));
		verify(json).add("property",new BigInteger("1234"));
	}
	
	@Test
	public void do_nothing_when_adding_null_big_decimal() {
		builder.add("property", (BigDecimal) null);
		verifyZeroInteractions(json);
	}

	@Test
	public void can_add_big_decimal_value() {
		builder.add("property", new BigDecimal("1234"));
		verify(json).add("property",new BigDecimal("1234"));
	}
	
	@Test
	public void can_add_boolean_value() {
		builder.add("property", true);
		verify(json).add("property",true);
		builder.add("property", false);
		verify(json).add("property",false);
	}

	@Test
	public void can_add_int_value() {
		builder.add("property", 0);
		verify(json).add("property",0);
	}

	@Test
	public void can_add_double_value() {
		builder.add("property", 0d);
		verify(json).add("property",0d);
	}

	@Test
	public void can_add_long_value() {
		builder.add("property", 0l);
		verify(json).add("property",0l);
	}

	@Test
	public void do_nothing_when_adding_null_json_array() {
		builder.add("property", (JsonArrayBuilder) null);
		verifyZeroInteractions(json);
	}

	@Test
	public void can_add_json_array() {
		JsonArrayBuilder array = mock(JsonArrayBuilder.class);
		builder.add("property", array);
		verify(json).add("property", array);
	}

	
	@Test
	public void do_nothing_when_adding_null_json_object() {
		builder.add("property", (JsonObjectBuilder) null);
		verifyZeroInteractions(json);
	}
	
	@Test
	public void can_add_json_object() {
		JsonObjectBuilder object = mock(JsonObjectBuilder.class);
		builder.add("property", object);
		verify(json).add("property", object);
	}


	@Test
	public void do_nothing_when_adding_null_json_value() {
		builder.add("property", (JsonValue) null);
		verifyZeroInteractions(json);
	}
	
	@Test
	public void can_add_json_value() {
		JsonValue object = mock(JsonValue.class);
		builder.add("property", object);
		verify(json).add("property", object);
	}
	
	@Test
	public void can_add_null_value() {
		builder.addNull("property");
		verify(json).addNull("property");
	}
	
}
