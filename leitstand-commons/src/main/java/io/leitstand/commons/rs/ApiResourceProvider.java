/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import java.util.Set;

/**
 * Returns all API resources provided by a EMS module.
 * <p>
 * The provider must be a CDI managed bean.
 * <code>{@literal @Dependent}</code> scope is recommended, as the bean is only required at boot time.
 * </p>
 * @see ApiResources
 */
public interface ApiResourceProvider {
	
	/**
	 * Returns a set of API resources provided by a single module.
	 * @return an immutable set of API resources
	 */
	Set<Class<?>> getResources();

}