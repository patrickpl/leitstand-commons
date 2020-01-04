/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

/**
 * A <code>CompositeValue</code> is a {@link ValueObject} that a represents a single value, typically a string, composed by multiple attributes..
 * <p>
 * The <code>Version</code> is a good example for a composite value. 
 * Major, minor and patch level  are the scalar values forming the version composite value. 
 * Two version instances are equal, when they have the same major, minor and patch level.
 * The version is represented as a string like <code>1.2.3</code> as an example 
 * with <code>1</code> as major version number, <code>2</code> as minor version number</code> and <code>3</code> as patch level.
 * </p> 
 */
public abstract class CompositeValue extends ValueObject {
	
}