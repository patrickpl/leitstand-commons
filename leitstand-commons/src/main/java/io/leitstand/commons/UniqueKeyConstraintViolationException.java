/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.List;

/**
 * The <code>UniqueKeyConstraintViolationException</code> is fired to report a unique key constraint violation.
 */
public class UniqueKeyConstraintViolationException extends LeitstandException {

	private static final long serialVersionUID = 1L;
	
	public static KeyValuePair key(String name, Object value) {
		return new KeyValuePair(name,value);
	}
	
	
	public static class KeyValuePair {
		
		private String key;
		private Object value;
		
		protected KeyValuePair(String key, Object value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		
		public Object getValue() {
			return value;
		}
		
		public String toString() {
			return value != null ? value.toString() : "null";
		}
		
	}
	
	private final List<String> properties;

	/**
	 * Create a <code>UniqueKeyConstraintViolationException</code>.
	 * @param reason - the reason code
	 * @param properties - the names of the conflicting properties
	 */
	public UniqueKeyConstraintViolationException(Reason reason, KeyValuePair... properties) {
		super(reason,(Object[])properties);
		this.properties = stream(properties)
						  .map(KeyValuePair::getKey)
						  .collect(toList());
	}

	/**
	 * Returns the names of the conflicting properties.
	 * @return the names of the conflicting properties.
	 */
	public List<String> getProperties() {
		return unmodifiableList(properties);
	}

}
