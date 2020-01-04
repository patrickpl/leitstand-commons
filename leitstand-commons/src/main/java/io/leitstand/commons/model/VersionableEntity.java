/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Base class for versionable entities.
 * <p>
 * A versionable entity facilitates the implementation of optimistic locking.
 * Optimistic locking is a strategy to avoid lock contentions in the database.
 * An entity introduces a version column to count the modifications of an entity.
 * Every update statement specifies the expected version in the <code>WHERE</code> clause.
 * An <code>OptimisticLockException</code> is raised when the expected version does not match the current entity version.
 * The <code>VersionableEntity</code> introduces the <code>MODCOUNT</code> column to count all modificatios, 
 * thereby forming the version of an entity.
 * </p>
 * <p>
 * Apart from the version, the <code>VersionableEntity</code> introduces a <code>UUID</code> column to store an 
 * immutable external reference to the entity. The UUID must be specified in UUIDv4 format. 
 * By that, the <code>ID</code> introduced by {@link AbstractEntity} has not to be exposed. 
 * Instead the <code>UUID</code> has to be kept stable even if the data model of the intenvory changes.
 * </p>
 */
@MappedSuperclass
public abstract class VersionableEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@Column(unique=true,length=36)
	private String uuid;
	
	@Version
	private int modcount;

	/**
	 * No-argument constructor as required by the JPA specification.
	 */
	protected VersionableEntity(){
		// JPA
	}

	/**
	 * Create a <code>VersionableEntity</code> and passes the immutable entity UUID as argument.
	 * @param uuid - the immutable UUID of this entity
	 */
	protected VersionableEntity(String uuid){
		this.uuid = uuid;
	}
	
	/**
	 * Returns the entity UUID forming an immutable external reference to this entity.
	 * @return the UUID of this entity
	 */
	protected String getUuid(){
		return uuid;
	}
	
	/**
	 * Returns the modification count, i.e. the number of modifications this entity was subject of.
	 * @return the number of modifications
	 */
	protected int getModificationCount() {
		return modcount;
	}
	
}