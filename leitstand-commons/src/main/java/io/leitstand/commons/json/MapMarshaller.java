/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import java.util.Map;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * A utility to convert a <code>java.util.Map</code> object to a <code>javax.json.JsonObject</code>.
 * <p>
 * Use {@link #marshal(Map)} to obtain a <code>MapMarshaller</code> instance to convert the passed map to a JSON object.
 * <pre><code>
 * import static net.rtbrick.json.MapMarshaller.adaptToJson;
 * ...
 * Map<String,Object> map = ...;
 * JsonObject json = adaptToJson(map).toJson();
 * </code></pre>
 * </p>
 * 
 */
public class MapMarshaller {

	/**
	 * Create a <code>MapMarshaller</code> to adaptToJson the specified map as JSON object.
	 * @param map - the map to be adaptToJsoned as JSON
	 * @return an initialized map adaptToJsonler
	 */
	public static MapMarshaller marshal(Map<String,Object> map) {
		return new MapMarshaller(map);
	}
	
	@FunctionalInterface
	private static interface Property {
		void in(JsonObjectBuilder builder);
	}

	@FunctionalInterface
	private static interface Item {
		void in(JsonArrayBuilder builder);
	}
	
	private Map<String,Object> map;
	
	protected MapMarshaller(Map<String,Object> map) {
		this.map = map;
	}
	
	/**
	 * Returns a JSON representation of the specified map.
	 * @return a JSON representation of the specified map.
	 */
	public JsonObject toJson() {
		return toJson(map);
	}
	
	private JsonObject toJson(Map<String,Object> map) {
		return JsonMarshaller.marshal(map);
	}
	
}
