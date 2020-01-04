/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.EndpointConfig.defaultEndpoint;
import static io.leitstand.commons.etc.Environment.getSystemProperty;

public class OAuthConfig {

	private EndpointConfig openId = defaultEndpoint(getSystemProperty( "OAUTH2_OPENID_ENDPOINT", 
			  														   "http://localhost:8080/api/v1/oauth2/openid"));
	private EndpointConfig authorizationService = defaultEndpoint(getSystemProperty( "OAUTH2_AUTHORIZATION_ENDPOINT", 
																				    "http://localhost:8080/api/v1/oauth2/authorize"));
	private EndpointConfig graylog =  defaultEndpoint(getSystemProperty(  "OAUTH2_GRAYLOG_ENDPOINT", 
		    															  "http://localhost:8080/api/v1/oauth2/graylog/user"));
	
	
	public EndpointConfig getOpenIdEndpoint() {
		return openId;
	}
	
	public EndpointConfig getAuthorizationServiceEndpoint() {
		return authorizationService;
	}
	
	public EndpointConfig getGraylogEndpoint() {
		return graylog;
	}
	
}
