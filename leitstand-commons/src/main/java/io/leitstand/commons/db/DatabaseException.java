/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.db;

import java.sql.SQLException;


/**
 * The <code>DatabaseException</code> application exception is thrown by the 
 * {@link DatabaseService} to report SQL errors and to rollback the current transaction.
 *
 */
public class DatabaseException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a <code>DatabaseException</code>.
	 * @param cause - the SQL exception reported by the database.
	 */
	public DatabaseException(SQLException cause) {
		super(cause);
	}
	
}
