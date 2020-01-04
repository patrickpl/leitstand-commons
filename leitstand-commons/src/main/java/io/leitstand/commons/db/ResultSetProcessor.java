/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Performs an operation on each result set record.
 */
@FunctionalInterface
public interface ResultSetProcessor {

	/**
	 * Executes an operation on the current result set record.
	 * @param rs - the result set to be processed.
	 * @throws SQLException in case of an error.
	 */
	void process(ResultSet rs) throws SQLException;
	
}