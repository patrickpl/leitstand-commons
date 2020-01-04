/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.EndpointConfig.defaultEndpoint;
import static io.leitstand.commons.etc.Environment.getSystemProperty;

import io.leitstand.commons.model.ValueObject;

public class LeitstandSettings extends ValueObject {

	private EndpointConfig leitstand = defaultEndpoint(getSystemProperty("LEITSTAND_ENDPOINT","http://localhost:8080/api/v1"));
	private OAuthConfig oauth2 = new OAuthConfig();
	private EndpointConfig repository = defaultEndpoint(getSystemProperty("IMAGE_REPOSITORY_BASE_URL","http://localhost:8080/images"));
	private String uiModulesDir = getSystemProperty("leitstand.ui.modules.dir","/META-INF/resources");
	
	public EndpointConfig getLeitstandApi() {
		return leitstand;
	}
	
	public OAuthConfig getOAuthConfig() {
		return oauth2;
	}
	
	public EndpointConfig getImageRepository() {
		return repository;
	}
	
	public String getUIModulesDir() {
		return uiModulesDir;
	}
	
}
