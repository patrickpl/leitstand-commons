/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
/**
 * Contains bases classes and utilities to build model implementations.
 * <p>
 * According to Domain Driven Design by Eric Evans a domain model is constituted by entities, aggregates, value objects, domain services, repositories and domain events..
 * {@link Scalar} and {@link CompositeValue} are base classes to build immutable scalar and composite value objects respectively.
 * Two value objects are considered equal if all properties of the value objects have the same value.
 * As immutable objects, value objects are perfect candidates for keys in caches, because the state the key is derived from cannot be changed.
 * This is a mandatory constraint of the <code>equals</code> and <code>hashCode</code> contract defined by {@link Object}, the base class for all java objects.
 * </p>
 * <p>
 * JPA is leveraged to build persistence object models. All entities are simply annotated by the JPA {@literal @Entity} annotation. 
 * {@link VersionableEntity} is a base class for all entities that support a version column to facilitate optimistic locking.
 * Optimistic locking is a concept to avoid database locks. 
 * The trick is to define a column that represents the expected version of an entity.
 * Every update increments the version and checks whether the current version is equal to the expected version.
 * If the versions are different, the entity was updated in the meanwhile and an <code>OptimisticLockingException</code> is raised to abort the current transaction.
 * Not all versionable entities are aggregates but all aggregates are versionable.
 * Whenever an entity of an aggregate gets updated, the aggregate root must be updated as well to be able to detect concurrent modifications.
 * Fortunately, JPA <code>LockType</code> provides all necessary options to implement optimistic locking, aggregates and versionable entities properly.
 * The entity is responsible to return a named {@link Query} with the correct lock mode.
 * </p>
 * <p>
 * {@link Query} and {@link Update} wraps a JPQL query in an executable lambda expression. 
 * The implementation is responsible for selecting the named query, passing all parameters to the query and configure the lock mode properly.
 * An entity shall provide static producer methods for queries and updates.
 * </p>
 * <p>
 * The {@link Repository} wraps the JPA <code>EntityManager</code> and provides the methods to execute the queries and updates outlined before.
 * Every module creates its own repository instance and passes the module's persistence context to the repository.
 * </p>
 * 
 */
package io.leitstand.commons.model;