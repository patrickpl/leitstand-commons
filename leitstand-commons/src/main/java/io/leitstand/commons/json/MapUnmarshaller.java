/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static javax.json.JsonValue.ValueType.ARRAY;
import static javax.json.JsonValue.ValueType.FALSE;
import static javax.json.JsonValue.ValueType.NULL;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static javax.json.JsonValue.ValueType.STRING;
import static javax.json.JsonValue.ValueType.TRUE;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * A utility to convert a <code>javax.json.JsonObject</code> object to a <code>java.util.Map</code>.
 * <p>
 * Use {@link #unmarshal(JsonObject)} to obtain a <code>MapUnadaptToJsonler</code> instance to convert the passed JSON object to a map.
 * <pre><code>
 * import static net.rtbrick.json.MapUnadaptToJsonler.adaptFromJson;
 * ...
 * JsonObject json = ...;
 * Map<String,Object> map = adaptFromJson(json).toMap();
 * </code></pre>
 * </p>
 * 
 */
public class MapUnmarshaller {

	/**
	 * Create a <code>MapUnadaptToJsonler</code> to adaptFromJson the specified JSON object as map.
	 * @param json - the JSON object to be adaptFromJsonled to a map
	 * @return an initialized map adaptFromJsonler
	 */
	public static MapUnmarshaller unmarshal(JsonObject json) {
		return new MapUnmarshaller(json);
	}
	
	private JsonObject json;
	
	protected MapUnmarshaller(JsonObject json) {
		this.json = json;
	}
	
	/**
	 * Returns a map representation of the specified JSON object.
	 * @return a map representation of the specified JSON object.
	 */
	public Map<String,Object> toMap() {
		return toMap(json);
	}
	
	private Map<String,Object> toMap(JsonObject json) {
		Map<String,Object> map = new LinkedHashMap<>();
		for(Map.Entry<String, JsonValue> property : json.entrySet()) {
			map.put(property.getKey(),
					readValue(property.getValue()));

		}
		return map;
	}
	
	private Object readValue(JsonValue value) {
		if(value.getValueType() == NULL) {
			return null;
		}
		if(value.getValueType() == STRING) {
			return ((JsonString)value).getString();
		}
		if(value.getValueType() == OBJECT) {
			return toMap((JsonObject)value);
		}
		if(value.getValueType() == TRUE) {
			return Boolean.TRUE;
		}
		if(value.getValueType() == FALSE) {
			return Boolean.FALSE;
		}
		if(value.getValueType() == ARRAY) {
			List<Object> items = new LinkedList<>();
			JsonArray array = (JsonArray)value;
			for(int i=0; i < array.size(); i++) {
				items.add(readValue(array.get(i)));
			}
			return items;
		}
		if(value.getValueType() == NUMBER) {
			JsonNumber number = (JsonNumber) value;
			if(number.isIntegral()) {
				return number.bigIntegerValue();
			}
			return number.bigDecimalValue();
		}
		
		throw new IllegalStateException("Unknown JSON type: "+value.getValueType());
	}
	
}
