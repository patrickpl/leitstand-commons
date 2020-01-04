/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.jsonb.JsonbDefaults.jsonb;
import static javax.json.Json.createReader;

import java.io.StringReader;

import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * A utility to convert a Java object to a JSON object.
 */
public final class JsonMarshaller {

	/**
	 * Converts a Java object to a JSON object.
	 * Returns <code>null</code> if the specified object is <code>null</code>.
	 * @param object the java object
	 * @return the JSON object
	 */
	public static JsonObject marshal(Object object) {
		if(object == null) {
			return null;
		}
		String json = jsonb().toJson(object);
		try(StringReader buffer = new StringReader(json);
			JsonReader reader = createReader(buffer)){
			return reader.readObject();
		}
	}
	
	private JsonMarshaller() {
		// No instances allowed
	}
}
