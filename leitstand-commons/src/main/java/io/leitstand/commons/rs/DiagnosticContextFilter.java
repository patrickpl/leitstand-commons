/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.log.DiagnosticContext.clear;
import static io.leitstand.commons.log.DiagnosticContext.push;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.log.DiagnosticContext;

/**
 * A REST-API filter that reads the <code>Transaction-ID</code> HTTP header and pushes it to the {@link DiagnosticContext}.
 *
 */
@Provider
public class DiagnosticContextFilter implements ClientRequestFilter, ClientResponseFilter{

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		clear();
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		push("TID: "+requestContext.getHeaderString("Transaction-ID"));
	}

}
