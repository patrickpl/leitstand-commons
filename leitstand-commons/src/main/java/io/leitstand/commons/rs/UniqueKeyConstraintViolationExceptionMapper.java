/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.messages.Message.Severity.ERROR;
import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.json.JsonArrayBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.UniqueKeyConstraintViolationException;

/**
 * Maps a {@link UniqueKeyConstraintViolationExceptionMapper} to HTTP Status Code <code>409 Conflict</code>.
 */
@Provider
public class UniqueKeyConstraintViolationExceptionMapper implements ExceptionMapper<UniqueKeyConstraintViolationException>{
	
	@Override
	public Response toResponse(UniqueKeyConstraintViolationException e) {
		JsonArrayBuilder messages = createArrayBuilder();
		for(Object property : e.getProperties()) {
			messages.add(createObjectBuilder()
					.add("severity", ERROR.name())
				    .add("reason", e.getReason().getReasonCode())
				    .add("property",property.toString())
				    .add("message", e.getMessage()));
		}
		return status(CONFLICT)
			   .type(APPLICATION_JSON)
			   .entity(messages.build())
			   .build();
	}
}
