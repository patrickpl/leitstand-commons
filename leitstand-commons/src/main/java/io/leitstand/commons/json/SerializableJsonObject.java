/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.json;

import static io.leitstand.commons.jpa.SerializableJsonObjectConverter.parseJson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * A simple wrapper to create a <i>serializable</i> <code>javax.json.JsonObject</code>.
 * <p>
 * The <code>javax.json.JsonObject</code> implementations does not have to be serializable which requires
 * to declare <code>transient JsonObject</code> fields in serializable objects and to customize the serialization by writing the 
 * JSON string of the JSON object to the object output stream and by restoring the object from JSON read from an 
 * object input stream respectively.
 * The downside of this approach is, that JPA does not write <code>transient</code> fields to the database and JSON-B ignores <code>transient</code> fields as well.
 * </p>
 * <p>
 * Since all existing implementations are serializable that <code>transient</code> decleration could be omitted, but static code analysis considers this as an error, 
 * because the code relies on an implementation that is not part of the interface contract which is a bad idea in most cases.
 * </p>
 * This wrapper forwards all method invocations to the wrapped <code>JsonObject</code> and implements serialization as outlined before.
 * By that, a <code>SerializableJsonObject</code> does not have to be transient and will not be ignored by JPA and JSON-B respectively.
 *
 */
public class SerializableJsonObject implements JsonObject, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static JsonObject unwrap(SerializableJsonObject object) {
		if(object == null) {
			return null;
		}
		return object.unwrap();
	}

	public static SerializableJsonObject serializable(JsonObject object) {
		if(object == null) {
			return null;
		}
		if(object instanceof SerializableJsonObject) {
			return (SerializableJsonObject) object;
		}
		return new SerializableJsonObject(object);
	}
	

	private transient JsonObject object;

	public SerializableJsonObject(JsonObject object) {
		this.object = object;
	}

	@Override
	public JsonArray getJsonArray(String name) {
		return object.getJsonArray(name);
	}

	@Override
	public JsonObject getJsonObject(String name) {
		return object.getJsonObject(name);
	}

	@Override
	public int size() {
		return object.size();
	}

	@Override
	public boolean isEmpty() {
		return object.isEmpty();
	}

	@Override
	public JsonNumber getJsonNumber(String name) {
		return object.getJsonNumber(name);
	}

	@Override
	public boolean containsKey(Object key) {
		return object.containsKey(key);
	}

	@Override
	public JsonString getJsonString(String name) {
		return object.getJsonString(name);
	}

	@Override
	public boolean containsValue(Object value) {
		return object.containsValue(value);
	}

	@Override
	public String getString(String name) {
		return object.getString(name);
	}

	@Override
	public ValueType getValueType() {
		return object.getValueType();
	}

	@Override
	public String toString() {
		return object.toString();
	}

	@Override
	public String getString(String name, String defaultValue) {
		return object.getString(name, defaultValue);
	}

	@Override
	public JsonValue get(Object key) {
		return object.get(key);
	}

	@Override
	public int getInt(String name) {
		return object.getInt(name);
	}

	@Override
	public int getInt(String name, int defaultValue) {
		return object.getInt(name, defaultValue);
	}

	@Override
	public boolean getBoolean(String name) {
		return object.getBoolean(name);
	}

	@Override
	public JsonValue put(String key, JsonValue value) {
		return object.put(key, value);
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		return object.getBoolean(name, defaultValue);
	}

	@Override
	public boolean isNull(String name) {
		return object.isNull(name);
	}

	@Override
	public JsonValue remove(Object key) {
		return object.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonValue> m) {
		object.putAll(m);
	}

	@Override
	public void clear() {
		object.clear();
	}

	@Override
	public Set<String> keySet() {
		return object.keySet();
	}

	@Override
	public Collection<JsonValue> values() {
		return object.values();
	}


	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		return object.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return object.equals(o);
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public JsonValue getOrDefault(Object key, JsonValue defaultValue) {
		return object.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super JsonValue> action) {
		object.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super String, ? super JsonValue, ? extends JsonValue> function) {
		object.replaceAll(function);
	}

	@Override
	public JsonValue putIfAbsent(String key, JsonValue value) {
		return object.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return object.remove(key, value);
	}

	@Override
	public boolean replace(String key, JsonValue oldValue, JsonValue newValue) {
		return object.replace(key, oldValue, newValue);
	}

	@Override
	public JsonValue replace(String key, JsonValue value) {
		return object.replace(key, value);
	}

	@Override
	public JsonValue computeIfAbsent(String key,
			Function<? super String, ? extends JsonValue> mappingFunction) {
		return object.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public JsonValue computeIfPresent(String key,
			BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
		return object.computeIfPresent(key, remappingFunction);
	}

	@Override
	public JsonValue compute(String key,
			BiFunction<? super String, ? super JsonValue, ? extends JsonValue> remappingFunction) {
		return object.compute(key, remappingFunction);
	}

	@Override
	public JsonValue merge(String key, JsonValue value,
			BiFunction<? super JsonValue, ? super JsonValue, ? extends JsonValue> remappingFunction) {
		return object.merge(key, value, remappingFunction);
	}
	
	public JsonObject unwrap() {
		return object;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.object = parseJson((String)in.readObject());
	}
	
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(object.toString());
	}

}
