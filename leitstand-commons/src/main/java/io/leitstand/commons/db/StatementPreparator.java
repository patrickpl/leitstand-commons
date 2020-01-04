/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Creates a prepared statement on an established SQL exception
 */
@FunctionalInterface
public interface StatementPreparator {
	/**
	 * Returns the prepared statement created on the specified connection.
	 * @param c - the connection
	 * @return the prepared statement
	 * @throws SQLException if the connection is stale
	 */
	PreparedStatement apply(Connection c) throws SQLException;
}
