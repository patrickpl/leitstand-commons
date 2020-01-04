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

import io.leitstand.commons.AccessDeniedException;
import io.leitstand.commons.Reason;

public class AccessDeniedExceptionMapperTest {

	private AccessDeniedExceptionMapper mapper = new AccessDeniedExceptionMapper();
	
	@Test
	public void report_access_denied_as_forbidden() {
		Object[] args = new Object[0];
		Reason reason = mock(Reason.class);
		when(reason.getReasonCode()).thenReturn("TST0001E");
		when(reason.getMessage(args)).thenReturn("Test message");
		
		AccessDeniedException conflict = new AccessDeniedException(reason, args);
		
		Response response = mapper.toResponse(conflict);
		assertEquals(403, response.getStatus());
		assertEquals("application/json",response.getMediaType().toString());
		JsonObject entity = (JsonObject) response.getEntity();
		assertEquals("ERROR",entity.getString("severity"));
		assertEquals("Test message",entity.getString("message"));
		
	}
	
}
