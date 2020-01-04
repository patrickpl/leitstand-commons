/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static io.leitstand.commons.model.StringUtil.isNonEmptyString;
import static java.lang.String.format;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract base class for an immutable <code>Scalar</code> value object.
 * <p>
 * Two scalars are considered equal, when both scalars are of the same type and 
 * have an equal value. 
 * Scalars are immutable which means that changing the value is not possible without creating a new scalar instance.
 * This makes scalars to perfect keys for hash maps because the calculated hashcode is stable.
 * See the {@link Object#equals(Object)} and {@link Object#hashCode()} contract for further information.
 * </p>
 * <p>
 * Scalars have a natural ordering because every scalar value is a {@link Comparable} object.
 * </p>
 * 
 * @param <T> the Java standard type of the scalar
 * @see CompositeValue
 */
public abstract class Scalar<T extends Comparable<T>> implements Serializable, Comparable<Scalar<T>> {

	private static final long serialVersionUID = 1L;

	public static String toString(Scalar<?> s) {
		if(s == null) {
			return null;
		}
		return s.toString();
	}
	
	protected static <T extends Scalar<?>> T fromString(String s, Function<String,T> producer){
		if(isNonEmptyString(s)) {
			return producer.apply(s);
		}
		return null;
	}
	
	protected Scalar() {
		// Tool constructor
	}

	/**
	 * Compares two scalars by their respective values. 
	 * <p>
	 * Scalar values have a natural ordering because every
	 * scalar value is a {@link Comparable} object.
	 * @param o the scalar to compare with this scalar
	 * @return a negative, zero or positive integer as this object is less than, equal to or greater than the specified object.
	 * @throws IllegalArgumentException if the passed scalar and this scalar have different types
	 */
	@Override
	public int compareTo(Scalar<T> o) {
		if(o.getClass() != getClass()) {
			String msg = format("Cannot compare scalars of different types (%s,%s)",
								getClass(),
								o.getClass());
			throw new IllegalArgumentException(msg);
		}
		return getValue().compareTo(o.getValue());
	}

	/**
	 * Returns whether this object is equal to the specified object.
	 * Two scalars are considered equal, if both scalars share the same type and have an equal value.
	 * @param o - the object to be compared with this object
	 * @return <code>true</code> if the passed object is equal to this object and <code>false</code> otherwise 
	 *
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		Scalar<?> other = (Scalar<?>) o;
		return Objects.equals(getValue(), other.getValue());
	}

	/**
	 * Calculates the hash code of this scalar 
	 * by passing the scalar value to the {@link Objects#hash(Object...)} method.
	 * @return the calculated hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getValue());
	}

	/**
	 * Returns the scalar value as string.
	 * 
	 * @return the scalar value as string.
	 */
	@Override
	public String toString() {
		return getValue() != null ? getValue().toString() : "null";
	}

	/**
	 * Returns the scalar value.
	 * 
	 * @return the scalar value.
	 */
	public abstract T getValue();
}
