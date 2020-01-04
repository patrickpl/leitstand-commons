/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.messages.Message.Severity.ERROR;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps an <code>OptimisticLockException</code> to HTTP Status Code <code>409 Conflict</code>. 
 */
@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {

	@Override
	public Response toResponse(OptimisticLockException e) {
		JsonObject message = Json.createObjectBuilder()
				                 .add("severity", ERROR.name())
				                 .add("message", e.getMessage())
				                 .build();
		return Response.status(CONFLICT)
				       .type(APPLICATION_JSON)
				       .entity(message)
				       .build();
	}
	
}
