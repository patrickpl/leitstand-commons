/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static java.lang.String.format;

import java.net.URI;

import javax.ws.rs.core.Response;

import io.leitstand.commons.messages.Messages;

public final class Responses {

	public static Response created(Messages messages, String template, Object...args ) {
		return created(messages,
					   URI.create(format(template, args)));
	}
	
	public static Response created(String template, Object... args) {
		return created(null,template,args);
	}
	
	public static Response created(Object uri) {
		return created(URI.create(uri.toString()));
	}

	public static Response created(Messages messages, Object uri) {
		return created(messages,URI.create(uri.toString()));
	}
	
	public static Response created(URI uri) {
		return Response.created(uri).build();
	}
	
	public static Response seeOther(String template, Object...args ) {
		return seeOther(URI.create(format(template, args)));
	}
	
	public static Response seeOther(Object uri) {
		return seeOther(URI.create(uri.toString()));
	}
	
	public static Response seeOther(URI uri) {
		return Response.seeOther(uri).build();
	}
	
	public static Response created(Messages messages, URI uri) {
		if(messages == null || messages.isEmpty()) {
			return created(uri);
		}
		return Response.created(uri).entity(messages).build();
	}

	
	public static Response accepted(Messages messages) {
		if(messages.isEmpty()) {
			return Response.accepted().build();
		}
		return Response.accepted(messages).build();
		
	}
	
	public static Response success(Messages messages) {
		if(messages == null || messages.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(messages).build();
		
	}
	
	public static Response success(Object entity) {
		if(entity == null) {
			return Response.noContent().build();
		}
		return Response.ok(entity).build();
	}
	
	public static Response success(Object entity, String contentType) {
		if(entity == null) {
			return Response.noContent().build();
		}
		return Response.ok(entity,contentType).build();
	}
	
	private Responses() {
		// No instances allowed
	}
	
}
