/*
 * (c) RtBrick, Inc. All rights reserved, 2015 2019  
 */
package io.leitstand.commons.model;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;
import static java.util.Collections.unmodifiableSortedSet;
import static java.util.stream.Collectors.toSet;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides a bunch of utilities to work with objects.
 */
public final class ObjectUtil {
	
	/**
	 * Reads a property from an object.
	 * Returns <code>null</code> if the specified object is <code>null</code>.
	 * @param object the object
	 * @param getter the getter method to read the property
	 * @return the property value or <code>null</code> if the specified object is <code>null</code>.
	 */
	public static <O,P> P optional(O object, Function<? super O,P> getter ) {
		if(object == null) {
			return null;
		}
		return getter.apply(object);
	}
	
	/**
	 * Reads a property from an object if the object matches the specified predicate.
	 * Returns <code>null</code> if the object is <code>null</code> or does not match the specified predicate.
	 * @param object the object
	 * @param test the predicate to test the object for
	 * @param getter the getter method to read the property
	 * @return the property value or <code>null</code> if the object is <code>null</code> or the predicate does not match
	 */
	public static <O,P> P optional(O object, Predicate<O> test, Function<? super O,P> getter) {
		if(object == null) {
			return null;
		}
		if(test.test(object)) {
			return getter.apply(object);
		}
		return null;
		
	}

	/**
	 * Creates an unmodifiable <code>Set</code> from the specified objects.
	 * @param objects the set items
	 * @return an unmodifiable <code>Set</code> that contains the specified objects.
	 */
	public static <T> Set<T> asSet(T... objects){
		return unmodifiableSet(stream(objects)
							   .collect(toSet()));
	}
	
	/**
	 * Creates an unmodifiable <code>SortedSet</code> from the specified objects.
	 * @param objects the set items
	 * @return an unmodifiable <code>Set</code> that contains the specified objects.
	 */
	public static <T extends Comparable<T>> SortedSet<T> asSortedSet(T... objects){
		return unmodifiableSortedSet(new TreeSet<>(asSet(objects)));
	}
	
	/**
	 * Creates an unmodifiable <code>SortedSet</code> from the specified objects.
	 * @param comparator the ordering of the objects
	 * @param objects the set items
	 * @return an unmodifiable <code>Set</code> that contains the specified objects.
	 */
	public static <T> SortedSet<T> asSortedSet(Comparator<T> comparator,
										 	   T... objects){
		SortedSet<T> set = new TreeSet<>(comparator);
		set.addAll(asSet(objects));
		
		return unmodifiableSortedSet(set);
	}

	/**
	 * Returns <code>true</code> if both specified objects a and b are <em>different</em>.
	 * Two objects are considered different, when a and b are <em>not equal</em>, 
	 * that is <code>Objects.equals(a,b)</code> returns <code>false</code>.
	 * @param a an object
	 * @param b another object to be compared with <code>a</code>
	 * @return <code>true</code> if both objects are not equals, otherwise <code>false</code>.
	 */
	public static <T> boolean isDifferent(T a, T b){
		return !Objects.equals(a, b);
	}
	
	/**
	 * Returns the negation of the specified predicate.
	 * @param p the predicate to be negated.
	 * @return the negated predicate
	 */
	public static <T> Predicate<T> not(Predicate<T> p){
		return p.negate();
	}
	
	/**
	 * Returns a predicate to test a property for <code>null</code> values.
	 * @param getter property getter function
	 * @return a predicate to test the property for <code>null</code> values.
	 */
	public static <T> Predicate<T> _null(Function<T,?> getter ){
		return  t -> getter.apply(t) == null;
	}
	
	private ObjectUtil(){
		// No instances allowed
	}
	
}
