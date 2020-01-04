/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

public class DummyConfig {
	
	static DummyConfig DEFAULT = new DummyConfig();
	static {
		DEFAULT.name="junit";
		DEFAULT.value="test";
	}
	
	public static DummyConfig defaultConfig() {
		return DEFAULT;
	}

	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
}
