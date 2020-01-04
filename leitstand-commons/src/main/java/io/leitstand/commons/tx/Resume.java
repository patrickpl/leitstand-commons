/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.tx;

import io.leitstand.commons.model.Repository;

/**
 * Resumes to the original transaction after execution of a nested {@link Transaction} as new container managed transaction.
 * <p>
 * JPA entities are managed by the JPA <code>EntityManager</code> and both, the entity manager and the entity are attached to the current transaction.
 * Modifications of attached entities are automatically written to the database when the corresponding transaction gets committed.
 * Once the transaction is completed (i.e. committed or rolled back), the entity is <em>detachted</em>, which means that further changes to this
 * entity are not written to the database. The <code>attach</code> method of the JPA <code>EntityManager</code> enables to attach detached entities.
 * </p>
 * The idea of <code>Resume</code> is to query the required entities <em>again</em> to get attached entities. 
 * By that, <code>Resume</code> does not care whether a transaction was successful or not.
 * The inventory tries to create stub records for missing records, such that a transaction never fails because of a missing data record. 
 * The stub record can be completed later.
 * However, when two transaction aim to create a missing stub record, only one can succeed.
 * Therefore the creation of stub records is done in a dedicated transaction to keep the original transaction alive. 
 * The creation of a stub record most likely fails because the stub record was created by a different transaction in the meantime.
 * Thus reading the record after the transaction is the best option, as it works for failed and succeeded transactions.
 * The additional query is no performance issue, because the creation of stub record is an exception and hence the additional query is not
 * executed very often as well.
 * @param <T> an entity or a list of entities
 */
@FunctionalInterface
public interface Resume<T> {
	
	/**
	 * Queries for the entities modified in a {@link Transaction} to return attached entities to the initial transaction that run the transaction.
	 * @param repository - the repository to obtain entities from
	 * @return an entity or a collection of entities.
	 */
	 T resume(Repository repository);
}
