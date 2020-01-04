/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class NullSafeJsonObjectBuilder implements JsonObjectBuilder{


	public static JsonObjectBuilder createNullSafeJsonObjectBuilder(){
		return new NullSafeJsonObjectBuilder(Json.createObjectBuilder());
	}
	
	private JsonObjectBuilder builder;

	NullSafeJsonObjectBuilder(JsonObjectBuilder builder) {
		this.builder = builder;
	}

	
	@Override
	public JsonObjectBuilder add(String name, BigDecimal value) {
		if(value != null){
			builder.add(name, value);
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, BigInteger value) {
		if(value != null){
			builder.add(name, value);
		}
		return this;
				
	}

	@Override
	public JsonObjectBuilder add(String name, boolean value) {
		builder.add(name, value);
		return this;	}

	@Override
	public JsonObjectBuilder add(String name, double value) {
		builder.add(name, value);
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, int value) {
		builder.add(name, value);
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, JsonArrayBuilder value) {
		if(value != null) {
			builder.add(name, value);
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, JsonObjectBuilder value) {
		if(value != null) {
			builder.add(name, value);
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, JsonValue value) {
		if(value != null) {
			builder.add(name, value);
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(String name, long value) {
		builder.add(name, value);
		return this;
	}
	

	@Override
	public JsonObjectBuilder add(String name, String value) {
		if(value != null){
			builder.add(name, value);
		}
		return this;
	}

	@Override
	public JsonObjectBuilder addNull(String name) {
		builder.addNull(name);
		return this;
	}

	@Override
	public JsonObject build() {
		return builder.build();
	}
	
}
