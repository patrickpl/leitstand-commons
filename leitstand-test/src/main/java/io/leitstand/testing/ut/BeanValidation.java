/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.testing.ut;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class BeanValidation {

	public static <T> void validate(T o) {
		Set<ConstraintViolation<T>> violations = buildDefaultValidatorFactory().getValidator().validate(o);
		if(violations.isEmpty()) {
			return;
		}
		throw new ConstraintViolationException(violations);
	}
	
}
