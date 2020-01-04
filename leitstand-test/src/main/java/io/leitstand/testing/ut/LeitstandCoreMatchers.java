/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.testing.ut;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import io.leitstand.commons.LeitstandException;
import io.leitstand.commons.Reason;

public class LeitstandCoreMatchers {

	
	public static <T> Matcher<Collection<T>> containsExactly(T... values){
		return new BaseMatcher<Collection<T>>() {

			@Override
			public boolean matches(Object item) {
				List<T> test = new LinkedList<>((Collection<T>)item);
				for(T value : values) {
					test.remove(value);
				}
				return test.isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:containsExactly %s", 
											  getClass().getSimpleName(),
											  asList(values)));
			}

		};
	}
	
	public static Matcher<String> isEmptyString(){
		return new BaseMatcher<String>(){

			@Override
			public boolean matches(Object item) {
				return ((String)item).isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:isEmptyString", getClass().getSimpleName()));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendValue(actualValue);
				description.appendText("String is not empty!");
			}
			
		};
	}
	
	public static <T extends Map<?,?>> Matcher<T> isEmptyMap(){
		return new BaseMatcher<T>(){

			@Override
			public boolean matches(Object item) {
				return ((Collection<?>)item).isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:isEmptyMap", getClass().getSimpleName()));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendText("Map is not empty!");
			}
			
		};
	}
	
	public static <T extends Set<?>> Matcher<T> isEmptySet(){
		return new BaseMatcher<T>(){

			@Override
			public boolean matches(Object item) {
				return ((Collection<?>)item).isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:isEmptySet", getClass().getSimpleName()));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendText("Set is not empty!");
			}
			
		};
	}
	
	
	public static <T extends List<?>> Matcher<T> isEmptyList(){
		return new BaseMatcher<T>(){

			@Override
			public boolean matches(Object item) {
				return ((Collection<?>)item).isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:isEmptyList", getClass().getSimpleName()));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendText("List is not empty!");
			}
			
		};
	}
	
	
	public static <T extends Collection<?>> Matcher<T> isEmptyCollection(){
		return new BaseMatcher<T>(){

			@Override
			public boolean matches(Object item) {
				return ((Collection<?>)item).isEmpty();
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:isEmptyCollection", getClass().getSimpleName()));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendText("Collection is not empty!");
			}
			
		};
	}
	
	
	public static <T extends Collection<?>> Matcher<T> hasSizeOf(int size){
		return new BaseMatcher<T>(){

			@Override
			public boolean matches(Object item) {
				return ((Collection<?>)item).size() == size;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:hasSizeOf %d", 
											  getClass().getSimpleName(),
											  size));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendValue(actualValue);
				description.appendText(format(" instead of %d",size));
			}
			
		};
	}
	
	public static <T extends LeitstandException> Matcher<T> reason(Reason reason){
		return new BaseMatcher<T>() {

			@Override
			public boolean matches(Object object) {
				return Objects.equals(((LeitstandException)object).getReason(),reason);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:reason %s",
											  getClass().getSimpleName(),
											  reason));
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendValue(actualValue);
				description.appendText(format(" instead of %s",reason));
			}

		};
	}
	
	public static <E,T extends Collection<E>> Matcher<T> contains(Predicate<E> predicate){
		return new BaseMatcher<T>() {

			@Override
			public boolean matches(Object item) {
				for(E element : ((Collection<E>)item)) {
					if(predicate.test(element)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(format("%s:contains item with specified predicate",
										getClass().getSimpleName()));				
			}
			
			@Override
			public void describeMismatch(Object actualValue, Description description) {
				description.appendValue(actualValue);
				description.appendText(format(" does not contain any matching elements."));
			}
			
		};
	}
	
}
