/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.messages.Message.Severity.ERROR;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.status;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.UnprocessableEntityException;

/**
 * Maps an {@link UnprocessableEntityException} to HTTP Status Code <code>422 Unprocessable entity</code>.
 */
@Provider
public class UnprocessableEntityExceptionMapper implements ExceptionMapper<UnprocessableEntityException> {

	@Override
	public Response toResponse(UnprocessableEntityException e) {
		JsonObject message = createObjectBuilder()
				             .add("severity", ERROR.name())
				             .add("reason", e.getReason().getReasonCode())
				             .add("message", e.getMessage())
				             .build();
		return status(422)
			   .type(APPLICATION_JSON)
			   .entity(message)
			   .build();
	}
	
}
