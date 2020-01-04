/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.http;

import static io.leitstand.commons.http.JsonRequest.HttpMethod.DELETE;
import static io.leitstand.commons.http.JsonRequest.HttpMethod.GET;
import static io.leitstand.commons.http.JsonRequest.HttpMethod.POST;
import static io.leitstand.commons.http.JsonRequest.HttpMethod.PUT;
import static io.leitstand.commons.json.JsonMarshaller.marshal;
import static io.leitstand.commons.json.MapMarshaller.marshal;
import static io.leitstand.commons.json.MapUnmarshaller.unmarshal;
import static io.leitstand.commons.jsonb.IsoDateAdapter.isoDateFormat;
import static io.leitstand.commons.model.BuilderUtil.assertNotInvalidated;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.JsonObject;

import io.leitstand.commons.jsonb.JsonProcessor;

public class JsonRequest {
	
	public static Builder newJsonRequest() {
		return new Builder();
	}
	
	public static class Builder {
		
		private JsonRequest request = new JsonRequest();
		
		public Builder withMethod(HttpMethod method) {
			assertNotInvalidated(getClass(), request);
			request.method = method;
			return this;
		}
		
		public Builder withPath(String path) {
			assertNotInvalidated(getClass(), request);
			request.path = path;
			return this;
		}
		
		public Builder withHeader(String header, String value) {
			return addHeader(header,value);
		}
		
		public Builder withHeader(String header, Date value) {
			return addHeader(header,isoDateFormat(value));
		}
		
		public Builder withHeader(String header, int value) {
			return addHeader(header,value);
		}
		
		private Builder addHeader(String header, Object value) {
			if(request.headers == null) {
				request.headers = new LinkedHashMap<>();
			}
			request.headers.put(header, value);
			return this;
		}
		
		public Builder withHeaders(Map<String,Object> headers) {
			assertNotInvalidated(getClass(), request);
			request.headers = new LinkedHashMap<>(headers);
			return this;
		}
		
		public Builder withBody(Object body) {
			return withBody(marshal(body));
		}
		
		public Builder withBody(JsonObject body) {
			return withBody(unmarshal(body).toMap());
		}
		
		public Builder withBody(Map<String,Object> body) {
			assertNotInvalidated(getClass(), request);
			request.body = body;
			return this;
		}
		
		public JsonRequest build() {
			try {
				assertNotInvalidated(getClass(), request);
				return request;
			} finally {
				this.request = null;
			}
		}
		
		
	}
	

	public enum HttpMethod {
		GET,
		POST,
		PUT,
		DELETE
	}
	
	private String path;
	private HttpMethod method;
	private Map<String,Object> headers;
	private Map<String,Object> body;
	
	public String getPath() {
		return path;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	public boolean isGetRequest() {
		return method == GET;
	}
	
	public boolean isPostRequest() {
		return method == POST;
	}
	
	public boolean isDeleteRequest() {
		return method == DELETE;
	}
	
	public boolean isPutRequest() {
		return method == PUT;
	}
	
	public Map<String, Object> getHeaders() {
		if(headers == null) {
			return emptyMap();
		}
		return unmodifiableMap(headers);
	}
	
	public <T> T getHeader(String name) {
		return (T) headers.get(name);
	}
	
	public <T> T getBody(Class<T> entity){
		return JsonProcessor.unmarshal(entity, getBody().toString());
	}
	
	public JsonObject getBody() {
		if(body == null) {
			return null;
		}
		return marshal(body).toJson();
	}
	
}