/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.messages.Message.Severity.ERROR;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.AccessDeniedException;
import io.leitstand.commons.EntityNotFoundException;

/**
 * Maps a {@link EntityNotFoundException} to HTTP Status Code <code>404 Not found</code>.
 */
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException>{
	@Override
	public Response toResponse(AccessDeniedException e) {
		JsonObject message = createObjectBuilder()
				             .add("severity", ERROR.name())
				             .add("reason", e.getReason().getReasonCode())
				             .add("message", e.getMessage())
				             .build();
		return status(FORBIDDEN)
			   .type(APPLICATION_JSON)
			   .entity(message)
			   .build();
	}
}
