/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jsonb;

import static java.lang.reflect.Modifier.PUBLIC;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.json.bind.config.PropertyVisibilityStrategy;

/**
 * A <code>PropertyVisibilityStrategy</code> to enable full field access support for JSON-B object deserialization.
 * <p>
 * JSON-B 1.0 requires all fields to be either be <code>public</code> or to 
 * obey the Java Bean property conventions (public getter/setter) in order to be populated in the java object
 * during the deserialization of JSON objects.
 * <p>
 * All other fields are not populated by default.
 * <p>
 * With this convention, the java object behaves fairly similar to a JSON object. 
 * All state information can be changed instantly. 
 * The object can be considered as an in-memory data structure rather than an object.
 * <p>
 * However, this approach is not compatible with the concept of immutable value objects.
 * The state of value objects is set during object creation and the object provides getter methods only.
 * Consequently, the JSON deserializer must not ignore <code>private</code> fields during JSON object deserialization.
 */
public class FieldAccessVisibilityStrategy implements PropertyVisibilityStrategy {

	/**
	 * Returns always <code>true</code> since all fields are accessible when field access is enabled.
	 * @param field - the field to check for visibility
	 * @return always <code>true</code> since all fields are accessible when field access is enabled.
	 */
	@Override
	public boolean isVisible(Field field) {
		return true;
	}

	/**
	 * Returns <code>false</code> in order to marshal fields only.
	 * @param method - the method to check for visibility
	 * @return <code>false</code> to marshal fields only,.
	 */
	@Override
	public boolean isVisible(Method method) {
		return (method.getModifiers() & PUBLIC) == PUBLIC && method.getParameterCount() == 0;
	}

}
