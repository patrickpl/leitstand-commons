/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
/**
 * Contains utilities to work with container managed transactions.
 * <p>
 * The EJB specification defines the transaction boundaries for container managed transactions.
 * All operations of a <em>stateless session bean</em> are transactional by default, which means 
 * that a stateless session bean either participates in an existing transaction or creates a new transaction,
 * when no transaction is currently active.
 * In certain situations it is necessary to run a certain control flow in a different transaction.
 * The {@link SubtransactionExecutionService} enables to run a {@link Transaction} in a new container managed transaction.
 * The <code>Transaction</code> encapsulates the instructions to be executed in a new transaction. 
 * Once the new transaction is committed, the {@link Resume} action defines the necessary steps to return to previous transaction.
 * </p>
 * <p>
 * For example, if a service gets registered for an element the service definition is expected to be present.
 * If the service definition is not available, the service registration creates a <em>stub</em> record for the service.
 * If multiple transactions aim to register the same service stub record, only one transaction succeeds and all other transaction are rolled back,
 * although the missing service stub record is available now as it has been created by the first transaction.
 * Thus the stub record creation should be a dedicated transaction. 
 * If this transaction fails, then most likely because the stub record was created by a different transaction in the meanwhile.
 * Additionally, passing managed entities across transaction boundaries has unwanted side-effects as managed entities become <em>detached</em>.
 * The {@link SubtransactionExecutionService#tryNewTransaction(Transaction, Resume)} method executes the specified transaction in a 
 * new container managed transaction and executes the specified resume action regardless whether the transaction succeeded or failed.
 * As a result the very first transaction, which was suspended for the execution of the transaction run by the <code>SubtransactionExecutionService</code>,
 * will never get in touch with detached entities.
 * </p>
 * <p>
 * The {@link RetryOnOptimisticLockException} aspect retries a transactional operation in case of an <code>OptimisticLockingException</code>.
 * The aspect can be activated via <code>{@literal @RetryOnOptimisticLockException}</code> on every transactional method that 
 * defines the transaction boundary of a container managed transaction.
 * </p>
 * 
 */
package io.leitstand.commons.tx;