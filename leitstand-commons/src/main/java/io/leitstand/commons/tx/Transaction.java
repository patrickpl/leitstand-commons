/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.tx;

import io.leitstand.commons.model.Repository;

/**
 * Wraps instructions to be executed in an new transaction.
 * <p>
 * The transaction receives the current {@link Repository} as an argument to have access to the entities. 
 * The repository is attached to the current transaction, which means that all modified entities obtained via this repository
 * are written to the database when the transaction gets committed. The same applies to merged entities.
 * </p>
 * Nested transactions are not supported.
 * Instead a transaction is <em>suspended</em> when a new transaction is started and resumed after the new transaction is completed.
 * All fetched entity are attached to the transaction that loaded the entity and are detached when accessed after the transaction ended.
 * A detached entity can be merged back into a running transaction via {@link Repository#merge(Object)}.
 * It is recommended to all entities from the repository instance, that is passes as argument to this transaction.
 * If entities are passed by reference, the entities are detached and must be attached to the transaction again.
 * 
 * @see Repository#merge(Object)
 */
@FunctionalInterface
public interface Transaction {
	/**
     * Executes the transaction.
     * <p>
     * Entities to be modified can be obtained from the passed {@link Repository}.
     * Use {@link Repository#merge(Object)} to merge an entity passed by reference into this transaction.
     * This applies in particular to all entities access within an anonymous transaction implementation.
     * <pre><code>
     * Entity entity = ...
     * 
     * Transaction tx = new Transaction(){
     *   public void transaction(Repository repository){
     *      // Attach entity by merging its state to this transaction
     *   	Entity attached = repository.merge(entity);
     *   
     *      // Now its safe to work with the attached entity.
     *      // The entity itself is still attached to the suspended transaction.
     *   }
     * };
     * 
     * 
	 * @param repository - the repository attached to this transaction.
	 */
	void transaction(Repository repository);
}