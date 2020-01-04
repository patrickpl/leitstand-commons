/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.StringUtil.isEmptyString;
import static java.lang.Integer.toHexString;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static java.util.logging.Level.FINE;
import static javax.validation.Validation.buildDefaultValidatorFactory;

import java.util.Set;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Provides utilities to facilitate the implementation of <a href="https://en.wikipedia.org/wiki/Builder_pattern">builders</a> for composite values. 
 * <p>
 * A builder provides a <em>fluent</em> API to build a new immutable composite value instance. 
 * A builder eliminates the need for constructor overloading if multiple properties are optional as the corresponding methods merely are not invoked to omit a value.
 * A builder <em>must</em> ensure that all mandatory properties are set.
 * A builder <em>must</em> provide a build method to create a new instance and invalidate itself to make sure, that the builder cannot be
 * leveraged to manipulate the created instance.
 * A builder improves readability since the builder methods express the semantic of passed arguments. 
 * </p>
 * @see CompositeValue
 */
public final class BuilderUtil {
	
	private static final Logger LOG = Logger.getLogger(BuilderUtil.class.getName());

	/**
	 * A builder can invalidate itself by setting the instance currently being constructed to <code>null</code>. 
	 * This method raises an exception, if the passed instance under construction is <code>null</code>. 
	 * 
	 * @param builder - the builder class
	 * @param instance - the instance currently under construction
	 * @throws IllegalStateException if the passed instance is <code>null</code>.
	 */
	public static void assertNotInvalidated(Class<?> builder, Object instance) {
		if(instance == null) {
			throw new IllegalStateException(format("%s is invalidated and must not be used any longer",builder.getSimpleName()));
		}
	}

	/**
	 * Verifies whether a certain string property is set, i.e. the value of the property is not <code>null</code> and the string is not empty.
	 * @param builder - the builder class
	 * @param property - the property name
	 * @param value - the assigned property value
	 * @throws IllegalStateException if the passed property value is <code>null</code> or empty.
	 */
	public static void requires(Class<?> builder, String property, String value) {
		if(isEmptyString(value)) {
			throw new IllegalStateException(format("%s requires %s to be specified!", 
												   builder.getSimpleName(),
												   property));
		}
	}
	
	/**
	 * Verifies whether a certain property is set, i.e. the value of the property is not <code>null</code>.
	 * @param builder - the builder class
	 * @param property - the property name
	 * @param value - the assigned property value
	 * @throws IllegalStateException if the passed property value is <code>null</code>.
	 */
	public static void requires(Class<?> builder, String property, Object value) {
		if(value == null) {
			throw new IllegalStateException(format("%s requires %s to be specified!", 
										   		   builder.getSimpleName(),
										   		   property));
		}
	}
	
	/**
	 * Runs bean validation for the passed object instance.
	 * @param builder - the builder class
	 * @param instance - the object to validated
	 * @throws ConstraintViolationException if constraint violations were detected
	 */
	public static <T> T valid(T instance) {
		Set<ConstraintViolation<Object>> violations = buildDefaultValidatorFactory()
													  .getValidator()
													  .validate(instance);
		if(violations.isEmpty()) {
			return instance;
		}
		if(LOG.isLoggable(FINE)) {
			// We create a token to identify all violations originated from the same value object
			// in the trace.
			String token = toHexString(abs(identityHashCode(violations)));
			LOG.log(FINE,"["+token+"]: "+instance.getClass());
			for(ConstraintViolation<?> violation : violations) {
				LOG.log(FINE,   " ["+token+"]: "+violation);
			}
		}
		throw new ConstraintViolationException(violations);
	
	}
	
	private BuilderUtil() {
		// No instances allowed
	}
	
}
