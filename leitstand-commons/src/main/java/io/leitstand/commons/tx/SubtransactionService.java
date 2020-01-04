/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.tx;

import static java.util.logging.Level.FINE;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

import java.util.logging.Logger;

import javax.inject.Provider;
import javax.transaction.Transactional;

import io.leitstand.commons.model.Repository;
import io.leitstand.commons.model.Service;

/**
 * Runs a {@link Transaction} in a new container managed transaction.
 * <p>
 * Nested transaction are not supported.
 * The current transaction is <em>suspended</em> to run a new transaction.
 * The suspended transaction is <em>resumed</em> if the new transaction ended.
 * All entities are attached to the transaction that has read the entity from the database.
 * All modifications of an attached entity are written to the database if the transaction is committed.
 * All modifications of a detached entity are <em>ignored</em>.
 * <p>
 * The {@link Transaction} interface is executed in a new transaction. 
 * By that, all entities passed by reference to this transaction are detached and must be attached via {@link Repository#merge} again,
 * The <code>merge</code> operation returns an attached copy of the passed entity. 
 * It is utmost important to proceed with the returned copy to avoid troubles.
 * If the transaction is done, the {@link Resume} operation is executed to attach all created entities to the resumed transaction.
 */
@Service
public abstract class SubtransactionService {
	
	private static final Logger LOG = Logger.getLogger(SubtransactionService.class.getName());
	
	/**
	 * Runs the passed transaction in a new container managed transaction.
	 * @param tx - the transaction to be executed
	 */
	@Transactional(value=REQUIRES_NEW, rollbackOn=RuntimeException.class)
	public void run(Transaction tx) {
		Repository repository = getRepository();
		tx.transaction(repository);
	}
	
	/**
	 * Runs the flow in a new container managed transaction and invokes the resume operation for the suspended transaction.
	 * @param flow - the flow to be executed
	 * @return the outcome of the flow as attached entities
	 */
	public <T> T run(Flow<T> flow) {
		return run(flow,flow);
	}
	
	/**
	 * Runs the specified {@link Transaction} in a new container managed transaction and executes the {@link Resume} operation 
	 * regardless whether the transaction was executed successfully or the execution has failed.
	 * @param tx - the transaction to be executed in a new container managed transaction
	 * @param resume - the resume operation to return to the first transaction, that was active before the new transaction was spawned.
	 * @return the outcome of the resume operation
	 */
	public <T> T run(Transaction tx, Resume<T> resume) {
		try {
			getServiceProvider().get().run(tx);
		} catch (RuntimeException e) {
			LOG.log(FINE,e.getMessage(),e);
		}
		return resume.resume(getRepository());
	}
	
	protected abstract Repository getRepository();
	protected abstract Provider<SubtransactionService> getServiceProvider();
	
}
