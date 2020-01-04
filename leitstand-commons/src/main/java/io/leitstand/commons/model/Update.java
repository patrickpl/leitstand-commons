/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import javax.persistence.EntityManager;

/**
 * A functional interface to facilitate the execution of named JPQL queries.
 * <p>
 * The <code>Update</code> class is based on the same concept as outlined in {@link Query} 
 * but is used for the execution of <code>UPDATE</code> and <code>DELETE</code> statements.
 * Consequently, an update does not return a result set but the number of affected database records.
 * </p>
 * 
 * @see Query The <code>Query</code> description
 */
@FunctionalInterface
public interface Update {
	
	/**
	 * Execute and update and returns the number of affected rows.
	 * @param em - the entity manager executing the update
	 * @return the number of affected rows.
	 */
	int execute(EntityManager em);
}
