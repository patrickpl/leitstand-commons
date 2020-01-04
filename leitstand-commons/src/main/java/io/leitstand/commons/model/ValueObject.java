/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * An abstract base class for immutable value objects.
 * <p>
 * A value object is constituted by multiple scalar values and/or other value objects.
 * Value objects are considered equal, when both objects have the same type and all properties have the same value. 
 * Value objects are immutable, which means that changing the value object's internal state is not possible.
 * Instead a new value object is instantiated.
 * This makes value objects perfect keys for hash maps as the calculated hashcode is stable. 
 * See the {@link Object#equals(Object)} and {@link Object#hashCode()} contract for further information.
 * </p>
 * @see Scalar
 */
public abstract class ValueObject {

	/**
	 * Returns all properties of the specified class.
	 * @param clazz - the class to retrieve the properties form.
	 * @return the list of existing properties
	 */
	private static List<PropertyDescriptor> discoverProperties(Class<?> clazz){
		try{
			BeanInfo info = Introspector.getBeanInfo(clazz);
			List<PropertyDescriptor> properties = new LinkedList<>();
			for(PropertyDescriptor property : info.getPropertyDescriptors()){
				if(property.getReadMethod() != null){
					properties.add(property);
				}
			}
			return properties;
		} catch (Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Compares the values of the specified property of the specified value objects.
	 * @param descriptor - the property to be compared
	 * @param a - the reference value object
	 * @param b - the value object to be compared to the reference
	 * @return <code>true</code> if the object have different values for the specified property
	 * 		   <code>false</code> otherwise.
	 */
	private static boolean isDifferent(PropertyDescriptor descriptor, Object a, Object b){
		try{
			Method getter = descriptor.getReadMethod();
			return !Objects.equals(getter.invoke(a), 
								   getter.invoke(b));
		} catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Returns the value of a certain property.
	 * @param descriptor - the property of interest
	 * @param a - the value object to read the property from
	 * @return the property value or <code>null</code> if the property is not set.
	 * @throws IllegalStateException if the property is not accessible or does not exist
	 */
	private static Object getValue(PropertyDescriptor descriptor, Object a){
		try{
			Method getter = descriptor.getReadMethod();
			return getter.invoke(a);
		} catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Returns whether the specified object is equal to this object.
	 * <p>
	 * Two <code>CompositeValue</code> objects are considered equal, when 
	 * both objects are of the same type and all properties have the same value.
	 * </p>
	 * This method compares also all properties added by subclasses when comparing to 
	 * objects.
	 * @param o - the object to be compared with this object
	 * @return <code>true</code> when the specified object is equal to this object
	 */
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		}
		if(o == this){
			return true;
		}
		if(getClass() != o.getClass()){
			return false;
		}
		for(PropertyDescriptor descriptor : discoverProperties(getClass())){
			if(isDifferent(descriptor,this,o)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Calculates a hash code for this composite value object 
	 * by passing all properties of this value object to {@link Objects#hash(Object...)}.
	 * All properties added by subclasses will go into the hashcode calculation as well.
	 * @return the hash code of this composite value.
	 */
	@Override
	public int hashCode(){
		List<PropertyDescriptor> descriptors = discoverProperties(getClass());
		Object[] values = new Object[descriptors.size()];
		for(int i=0; i < values.length; i++){
			values[i] = getValue(descriptors.get(i), this);
		}
		return Objects.hash(values);
	}
	
	/**
	 * Returns a string representation of this composite value by concatenating 
	 * the composite value's simple class name followed by the scalars as name-value 
	 * pairs in square brackets.
	 * <p>
	 * Given a composite value <code>Version</code> with the properties <code>major</code>, 
	 * <code>minor</code> and <code>patch</code> the string representation of an 
	 * <code>Version 1.0-0</code> would be <code>Version [major= 1, minor=0, patch=0]</code>.
	 * If <code>major.minor-patch</code> is the expected string representation of a version, the 
	 * <code>Version</code> class must override the <code>toString</code> method to create
	 * the string representation accordingly.
	 * </p>
	 */
	@Override
	public String toString(){
		List<PropertyDescriptor> descriptors = discoverProperties(getClass());
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName()).append("[");
		for(PropertyDescriptor descriptor : descriptors){
			String name = descriptor.getName();
			if("class".equals(name)){
				continue;
			}
			Object value = getValue(descriptor,this);
			builder.append(name)
			       .append("= ")
			       .append(value)
			       .append(", ");
		}
		builder.delete(builder.length()-2,builder.length());
		builder.append("]");
		return builder.toString();
	}
}
