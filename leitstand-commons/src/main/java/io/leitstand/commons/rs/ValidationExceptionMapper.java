/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.messages.Message.Severity.ERROR;
import static io.leitstand.commons.messages.Message.Severity.INFO;
import static io.leitstand.commons.messages.Message.Severity.WARNING;
import static io.leitstand.commons.rs.ReasonCode.VAL0001E_VALUE_REQUIRED;
import static io.leitstand.commons.rs.ReasonCode.VAL0002E_INVALID_VALUE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.messages.Message.Severity;


/**
 * Maps a bean validation <code>ConstraintViolationException</code> to HTTP Status Code <code>422 Unprocessable Entity</code>
 */
@Provider
public class ValidationExceptionMapper  implements ExceptionMapper<ConstraintViolationException> {
	
	@Override
	public Response toResponse(ConstraintViolationException e) {
		JsonArrayBuilder array = Json.createArrayBuilder();
		for(ConstraintViolation<?> violation : e.getConstraintViolations()){
			JsonObjectBuilder item = Json.createObjectBuilder();
			ReasonCode reason = retrieveReasonCode(violation);
			if(reason != null){
				item.add("severity", retrieveSeverity(reason).name());
				item.add("reason", reason.getReasonCode());
				item.add("property",retrievePropertyName(violation));
				Object invalid = violation.getInvalidValue();
				if(invalid != null){
					item.add("value", invalid.toString());
				}
			}
			item.add("message", violation.getMessage());
			array.add(item);
		}
		StringWriter json = new StringWriter();
		try(JsonWriter writer = Json.createWriter(json)){
			writer.writeArray(array.build());
			return Response.status(422)
					       .type(APPLICATION_JSON)
					       .entity(json.toString())
					       .build();
		}
	}
	
	private ReasonCode retrieveReasonCode(ConstraintViolation<?> constraint){
		if(constraint.getMessageTemplate().contains("required")){
			return VAL0001E_VALUE_REQUIRED;
		}
		
		return VAL0002E_INVALID_VALUE;
	}
	
	private static String retrievePropertyName(ConstraintViolation<?> constraint){
		String template = constraint.getMessageTemplate();
		if(template == null || template.isEmpty() || template.startsWith("{javax")){
			String path = constraint.getPropertyPath().toString();
			return path.substring(path.lastIndexOf('.')+1);
		}
		return template.substring(1, template.lastIndexOf('.'));
	}
	
	private static Severity retrieveSeverity(ReasonCode reason){
		if(reason.name().endsWith("I")){
			return INFO;
		}
		if(reason.name().endsWith("W")){
			return WARNING;
		}
		return ERROR;
	}

}
