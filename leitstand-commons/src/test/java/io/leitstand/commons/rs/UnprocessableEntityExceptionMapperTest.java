/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.junit.Test;

import io.leitstand.commons.Reason;
import io.leitstand.commons.UnprocessableEntityException;

public class UnprocessableEntityExceptionMapperTest {

	private UnprocessableEntityExceptionMapper mapper = new UnprocessableEntityExceptionMapper();
	
	@Test
	public void report_unprocessable_entity_exception_as_unprocessable_entity_status() {
		Object[] args = new Object[0];
		Reason reason = mock(Reason.class);
		when(reason.getReasonCode()).thenReturn("TST0001E");
		when(reason.getMessage(args)).thenReturn("Test message");
		
		UnprocessableEntityException conflict = new UnprocessableEntityException(reason, args);
		
		Response response = mapper.toResponse(conflict);
		assertEquals(422, response.getStatus());
		assertEquals("application/json",response.getMediaType().toString());
		JsonObject entity = (JsonObject) response.getEntity();
		assertEquals("ERROR",entity.getString("severity"));
		assertEquals("Test message",entity.getString("message"));
		
	}
	
}
