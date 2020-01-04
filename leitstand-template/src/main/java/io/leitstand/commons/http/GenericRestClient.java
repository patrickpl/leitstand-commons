/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.http;

import static io.leitstand.commons.json.MapUnmarshaller.unmarshal;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.json;

import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import io.leitstand.commons.template.Template;

public class GenericRestClient {
	
	private URI endpoint;
	private String token;
	
	public GenericRestClient(URI endpoint) {
		this.endpoint = endpoint;
	}
	
	public void setAuthorizationHeader(String token) {
		this.token = token;
	}
	
	public <T extends JsonStructure> T submit(JsonRequest request) {
		String raw = submit(request,String.class);
		if(raw == null || raw.isEmpty()) {
			return null;
		}
		try(JsonReader reader = Json.createReader(new StringReader(raw))){
			return (T) reader.read();
		}
	}
	
	public <T> T submit(JsonRequest request, Template<T> responseMapping) {
		JsonObject raw = submit(request);
		Map<String,Object> model = unmarshal(raw).toMap();
		return responseMapping.apply(model);
	}
	
	public <T> T submit(Map<String,Object> model, Template<JsonRequest> requestTemplate, Template<T> responseTemplate) {
		JsonRequest request = requestTemplate.apply(model);
		JsonObject raw = submit(request);
		Map<String,Object> env = new HashMap<>();
		env.putAll(model);
		env.putAll(unmarshal(raw).toMap());
		return responseTemplate.apply(env);
	}

	public <T> T submit(JsonRequest request, Class<T> responseEntity) {
		return invoke(request).readEntity(responseEntity);
	}
	
	public Response invoke(JsonRequest request){
		Builder call = newClient()
					  .target(endpoint + request.getPath())
					  .request();
		for(Map.Entry<String, Object> header : request.getHeaders().entrySet()) {
			call.header(header.getKey(),
					    header.getValue());
		}
		if(token != null) {
			call.header("Authorization", token);
		}
		
		switch(request.getMethod()) {
			case GET: return call.get();
			case PUT: return call.put(json(request.getBody().toString()));
			case DELETE: return call.delete();
			case POST:
			default: return call.post(json(request.getBody().toString()));
		}
	}

	public URI getEndpoint() {
		return endpoint;
	}
	
}
