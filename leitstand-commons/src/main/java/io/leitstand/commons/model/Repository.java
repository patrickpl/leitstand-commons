/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;

/**
 * The <code>Repository</code> allows to add new entities to the database,
 * remove entities from the database or to search for entities.
 * <p>
 * Internally, the repository uses a managed <code>EntityManager</code> to access JPA entities.
 * All changes are written to the database when the Java Transaction get committed.
 * </p>
 */
public class Repository implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private EntityManager em;  // NOTE: EntityManager is not serializable but the injected CDI proxy is! Hence ignore the warning from sonarbug.

	/**
	 * Creates a <code>Repository</code>
	 * 
	 * @param em the entity manager
	 */
	public Repository(EntityManager em) {
		this.em = em;
	}

	/**
	 * Default constructor used by the container to instantiate and manage a
	 * repository.
	 */
	protected Repository() {
		// CDI
	}

	/**
	 * Executes the specified query. Returns <code>null</code> if no appropriate entity was found, 
	 * or an empty list, when the query was searching for a list of entities and no existing 
	 * entity matches the search criterias.
	 * @param query - the query to be executed.
	 * @return <code>null</code> if no appropriate entity was found, 
	 * or an empty list, when the query was searching for a list of entities and no existing 
	 * entity matches the search criterias.
	 */
	public <Q> Q execute(Query<Q> query) {
		try {
			return query.execute(em);
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * Executes the specified query and applies the specified mapping to the result set.
	 * Returns <code>null</code> if no appropriate entity was found.
	 * @param query the query to run
	 * @param mapping the query result mapping
	 * @return <code>null</code> if no matching entity exists or the mapped result
	 */
	public <Q,T> T execute(Query<Q> query, Function<Q,T> mapping) {
		try {
			return mapping.apply(query.execute(em));
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * Executes the specified query and applies a mapping to each item of the result list.
	 * @param query the query to run
	 * @param mapping the mapping to apply to every list item
	 * @return the mapped result list or an empty list, if no matching entities exist
	 */
	public <Q,T> List<T> executeMapListItem(Query<List<Q>> query, 
											Function<Q,T> mapping){
		return query.execute(em)
					.stream()
					.map(mapping)
					.collect(toList());
	}

	/**
	 * Adds a new entity to the repository.
	 * @param entity - the new entity to be added
	 * @see #flush()
	 * @see #clear()
	 */
	public void add(Object entity) {
		em.persist(entity);
	}

	/**
	 * Removes an entity from the repository.
	 * @param entity - the entity to be removed
	 * @see #flush()
	 * @see #clear() 
	 */
	public void remove(Object entity) {
		em.remove(entity);
	}
	
	/**
	 * Writes all updates kept in memory to the database. 
	 * Still, the data is uncommitted, until the Java Transaction get committed.
	 * Use this method with care, since usually the entity manager automatically 
	 * writes data blockwise to the database when necessary for performance reason. 
	 * Flushing the data may have a negative performance impact, because an extensive 
	 * use of flush causes execution of additional SQL statements.
	 */
	public void flush() {
		em.flush();
	}

	/**
	 * Empties the entity manager's cache. Use this method with care, because
	 * clearing the cache requires the execution of additional SQL statements
	 * for all data not longer available in the cache.
	 * <p>
	 * This method also discards all updates kept in memory and not synchronized
	 * with the database. In order to avoid an unwanted lost of data, use flush
	 * to push all changes to the database before clearing the cache.
	 * </p>
	 * Please consider that the entity manager writes updates kept in memory
	 * automatically to the database and hence no assumptions should be made
	 * whether or not entities have already been written to the database.
	 */
	public void clear() {
		em.clear();
	}
	
	/**
	 * Executes the specified update and returns the number of affected records. 
	 * Update is not necessarily restricted to UPDATE statements but can also 
	 * execute DELETE or INSERT statements.
	 * @param update - the statement to be executed
	 * @return the number of modified rows.
	 */
	public int execute(Update update) {
		return update.execute(em);
	}

	/**
	 * Returns a reference to an entity and fetches the entity lazily on demand.
	 * @param type - the entity type
	 * @param primaryKey - the primary key of the entity 
	 * @return a reference to access an entity lazily on demand.
	 */
	public <T> T getReference(Class<T> type, Object primaryKey){
		return em.getReference(type, primaryKey);
	}
	
	/**
	 * Merges an entity into the state of this entity manager.
	 * Detached entity becomes attached again.
	 * Returns the managed entity to proceed with.
	 * @param entity - the attached entity.
	 * @return the managed entity.
	 */
	public <T> T merge(T entity) {
		return em.merge(entity);
	}

	/**
	 * Returns the entity of the specified type with the given primary key.
	 * @param type - the entity type 
	 * @param primaryKey - the primary key of the entity
	 * @return the entity or <code>null</code> if no entity for the given primary 
	 * 		   key does not exist.
	 */
	public <T> T find(Class<T> type,
					  Object primaryKey) {
		return em.find(type, primaryKey);
	}

	/**
	 * Executes the specified query to search for an entity and calls the supplier if the entity is not present
	 * in order to add the entity to the repository.
	 * @param query the query to fetch a certain entity
	 * @param supplier a supplier to creae the entity, if not already present
	 * @return the requested entity, either read from the repository or returned by the supplier
	 */
	public <T> T addIfAbsent(Query<T> query,
					 	     Supplier<T> supplier){
		T instance = execute(query);
		if(instance != null) {
			return instance;
		}
		instance = supplier.get();
		add(instance);
		return instance;
	}
	
	/**
	 * Override the lock mode of a managed entity.
	 * @param entity the managed entity
	 * @param mode the new lock mode
	 */
	public void lock(Object entity, 
					 LockModeType mode) {
		em.lock(entity, mode);
	}
	
	/**
	 * Closes this repository and the associated entity manager.
	 * Further invocations of that repository results in exception.
	 * Attempting to close a container-managed entity manager raises an exception as well.
	 */
	public void close() {
		em.close();
	}
	
}
