/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.jsonb.JsonbDefaults.jsonb;

import javax.json.JsonObject;

/**
 * A utility to convert a JSON object to a Java object.
 *
 */
public final class JsonUnmarshaller {

	/**
	 * Converts a JSON object to a Java object.
	 * Returns <code>null</code> if the JSON object is <code>null</code>
	 * @param type the Java type
	 * @param json the JSON object.
	 * @return the Java object
	 */
	public static <T> T unmarshal(Class<T> type, JsonObject json) {
		if(json == null) {
			return null;
		}
		return jsonb().fromJson(json.toString(), type);
	}
	
	private JsonUnmarshaller() {
		// No instances allowed
	}
}
