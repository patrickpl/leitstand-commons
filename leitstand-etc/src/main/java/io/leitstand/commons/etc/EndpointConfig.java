/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class EndpointConfig {
	
	public static EndpointConfig defaultEndpoint(String url) {
		return new EndpointConfig(url);
	}

	protected EndpointConfig() {
		// Tool Ctor
		this.timeout = Duration.ofMillis(10000).toString();
	}
	
	protected EndpointConfig(String endpoint) {
		this();
		this.endpoint = endpoint;
	}
	
	private String endpoint;
	private String timeout;
	private String accessKey;
	
	public URI getEndpoint() {
		return URI.create(endpoint);
	}
	
	public URI getEndpoint(String path) {
		return URI.create(endpoint+path);
	}
	
	public URI getEndpoint(String path, Object... args) {
		return URI.create(endpoint+format(path, args));
	}
	
	public Duration getTimeout() {
		return Duration.parse(timeout);
	}
	
	public long getTimeoutMillis() {
		return getTimeout(MILLIS);
	}

	public long getTimeout(TemporalUnit unit) {
		return getTimeout().get(unit);
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	
}
