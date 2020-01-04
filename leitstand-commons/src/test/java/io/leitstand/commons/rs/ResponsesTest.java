/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.rs.Responses.accepted;
import static io.leitstand.commons.rs.Responses.created;
import static io.leitstand.commons.rs.Responses.success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import io.leitstand.commons.messages.Messages;

public class ResponsesTest {

	private static final int HTTP_NO_CONTENT = 204;
	private static final int HTTP_OK = 200;
	private static final int HTTP_ACCEPTED = 202;
	private static final int HTTP_CREATED = 201;
	
	private Messages messages;
	
	@Before
	public void initMessages() {
		this.messages = mock(Messages.class);
	}
	
	@Test
	public void ok_response() {
		when(messages.isEmpty()).thenReturn(true);
		Response response = success("entity");
		assertEquals(HTTP_OK, response.getStatus());
		assertEquals("entity",response.getEntity());
	}
	
	@Test
	public void null_entity_is_translated_to_no_content() {
		when(messages.isEmpty()).thenReturn(true);
		Response response = success((Object)null);
		assertEquals(HTTP_NO_CONTENT, response.getStatus());
		assertNull(response.getEntity());
	}
	
	@Test
	public void send_no_content_response_when_messages_is_null() {
		Response response = success((Messages)null);
		assertEquals(HTTP_NO_CONTENT, response.getStatus());
		assertNull(response.getEntity());
	}
	
	@Test
	public void send_no_content_response_when_messages_is_empty() {
		when(messages.isEmpty()).thenReturn(true);
		Response response = success(messages);
		assertEquals(HTTP_NO_CONTENT, response.getStatus());
		assertNull(response.getEntity());
	}
	
	@Test
	public void send_ok_response_when_messages_exist() {
		when(messages.isEmpty()).thenReturn(false);
		Response response = success(messages);
		assertEquals(HTTP_OK, response.getStatus());
		assertSame(messages,response.getEntity());
	}
	
	@Test
	public void send_created_response_with_detail_messages() {
		when(messages.isEmpty()).thenReturn(false);
		Response response = created(messages,URI.create("test"));
		assertEquals(HTTP_CREATED,response.getStatus());
		assertEquals(URI.create("test"),response.getLocation());
		assertSame(messages,response.getEntity());
	}
	
	@Test
	public void send_created_response_without_detail_messages() {
		when(messages.isEmpty()).thenReturn(true);
		Response response = created(messages,URI.create("test"));
		assertEquals(HTTP_CREATED,response.getStatus());
		assertEquals(URI.create("test"),response.getLocation());
		assertNull(response.getEntity());
	}
	
	@Test
	public void accepted_response_with_detail_messages() {
		when(messages.isEmpty()).thenReturn(false);
		Response response = accepted(messages);
		assertEquals(HTTP_ACCEPTED,response.getStatus());
		assertSame(messages,response.getEntity());
	}
	
	@Test
	public void accepted_response_without_detail_messages() {
		when(messages.isEmpty()).thenReturn(true);
		Response response = accepted(messages);
		assertEquals(HTTP_ACCEPTED,response.getStatus());
		assertNull(response.getEntity());
	}
	
	
}
