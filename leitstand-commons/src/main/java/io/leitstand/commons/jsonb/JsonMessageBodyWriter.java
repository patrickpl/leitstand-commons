/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jsonb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Marshals a <code>javax.json.JsonObject</code> entity.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonMessageBodyWriter implements MessageBodyWriter<JsonStructure> {

	/**
	 * Returns always <code>0</code>.
	 * {@inheritDoc}
	 */
	@Override
	public long getSize(JsonStructure object, 
						Class<?> type,
						Type genericType, 
						Annotation[] annotations, 
						MediaType mediaType) {
		return 0;
	}

	/**
	 * Returns always <code>true</code>.
	 * {@inheritDoc}
	 */
	@Override
	public boolean isWriteable(Class<?> type, 
							   Type genericType, 
							   Annotation[] annotations, 
							   MediaType mediaType) {
		return true;
	}

	/**
	 * Marshals <code>javax.json.JsonObject</code> to a string in <code>UTF-8</code> character encoding.
	 * {@inheritDoc}
	 */
	@Override
	public void writeTo(JsonStructure object,
						Class<?> type,
						Type genericType,
						Annotation[] annotations,
						MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders,
						OutputStream entityStream)
						throws IOException {
		StringWriter json = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(json);
		jsonWriter.write(object);
		jsonWriter.close();
		entityStream.write(json.toString().getBytes("UTF-8"));
	}

}
