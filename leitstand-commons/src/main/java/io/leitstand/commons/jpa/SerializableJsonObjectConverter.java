/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jpa;

import static io.leitstand.commons.json.SerializableJsonObject.serializable;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;
import static javax.json.Json.createReader;

import java.io.StringReader;
import java.sql.SQLException;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.postgresql.util.PGobject;

import io.leitstand.commons.json.SerializableJsonObject;

@Converter(autoApply = true)
public class SerializableJsonObjectConverter implements AttributeConverter<SerializableJsonObject, Object>{

	/*
	 * Use PGObject for postgres but String for other databases. 
	 */
	static final boolean POSTGRES;
	
	static {
		String test = getProperty("POSTGRES","true");
		POSTGRES = parseBoolean(test);
	}
	
	@Override
	public Object convertToDatabaseColumn(SerializableJsonObject json) {
		if(POSTGRES) {
			try{
				PGobject object = new PGobject();
				object.setType("json");
				object.setValue(json != null ? json.toString() : null);
				return object;
			} catch (SQLException e){
				throw new IllegalStateException(e);
			}
		}
		if(json != null) {
			return json.toString();
		}
		return null;
	}

	@Override
	public SerializableJsonObject convertToEntityAttribute(Object value) {
		if(value == null){
			return null;
		}
		String json = value.toString();
		if(POSTGRES) {
			json = ((PGobject)value).getValue();
		}
		return serializable(parseJson(json));
	}

	public static JsonObject parseJson(String json) {
		if(json == null || json.isEmpty()) {
			return null;
		}
		try(JsonReader reader = createReader(new StringReader(json))){
			return reader.readObject();
		}
	}

}
