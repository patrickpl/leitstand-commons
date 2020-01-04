/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

/**
 * Abstract base class for all entity implementations.
 * <p>
 * The <code>AbstractEntity</code> defines an <code>ID</code> column of type <code>Long</code> as ID, i.e. as primary key 
 * along with a <code>{@literal @TableGenerator}</code> to generate ID values.
 * The <code>ID</code> is an internal identifier used to model relations between data tables.
 * Additionally, the abstract entity defines two timestamps: the creation date of the entity and the last modification date of the entity.
 * The timestamps are stored in the <code>TSCREATED</code> and <code>TSMODIFIED</code> columns respectively.
 * </p>
 */
@MappedSuperclass
public class AbstractEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "Entity.Sequence",
				    schema="leitstand",
				    table = "sequence", 
				    initialValue = 1, 
				    allocationSize = 100, 
				    pkColumnName = "name", 
				    pkColumnValue = "id", 
				    valueColumnName = "count")
	@GeneratedValue(strategy=TABLE , generator="Entity.Sequence")
	private Long id;
	
	@Column(name="tscreated")
	@Temporal(TIMESTAMP)
	private Date dateCreated;
	
	@Column(name="tsmodified")
	@Temporal(TIMESTAMP)
	private Date dateModified;
	
	/**
	 * No argument constructor as required by JPA specification.
	 */
	protected AbstractEntity() {
		this.dateCreated = new Date();
		this.dateModified = new Date();
	}
	
	/**
	 * Create a <code>AbstractEntity</code> and sets the primary key to the specified value.
	 * @param id - the primary key
	 */
	protected AbstractEntity(Long id) {
		this();
		this.id = id;
	}
	
	/**
	 * Returns the creation date of this entity.
	 * @return the creation date.
	 */
	public Date getDateCreated() {
		// Date is not immutable, which is why the byte code of the Date gets instrumented by JPA 
		// to track all modifications on the Date object (via setTime)
		// This has a major drawback, has the garbage collector cannot release certain JPA resources
		// if such a date object is stored in a cache.
		// We create a copy of the date object to solve this issue.
		return new Date(dateCreated.getTime());
	}
	
	/**
	 * Returns the entity ID.
	 * Returns <code>null</code> if this entity is a new entity that has not been written to the database.
	 * @return the entity ID.
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * Returns the last modification date of this entity.
	 * @return the last modification date
	 */
	public Date getDateModified() {
		// Create a copy for the same reason as outlined in getDateCreated()
		return new Date(dateModified.getTime());
	}
	
	@PreUpdate
	protected void touchLastModified() {
		this.dateModified = new Date();
	}

}