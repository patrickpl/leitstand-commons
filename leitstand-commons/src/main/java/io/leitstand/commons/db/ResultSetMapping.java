/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps a result set record to a typed object.
 * @param <T> the object type.
 */
@FunctionalInterface
public interface ResultSetMapping<T> {

	/**
	 * Maps the current result set row to the specified target type.
	 * @param rs - the current result set
	 * @return the typed object created from the result set record.
	 * @throws SQLException in case of a SQL error
	 */
	T map(ResultSet rs) throws SQLException;
	
}
