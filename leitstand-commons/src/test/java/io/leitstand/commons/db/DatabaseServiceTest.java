/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.db;

import static io.leitstand.commons.db.DatabaseService.prepare;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import io.leitstand.commons.model.Scalar;

public class DatabaseServiceTest {

	private enum TestState {
		ACTIVE
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static final String DUMMY_SQL = "SELECT * FROM UNITTEST";
	private DataSource ds;
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	private DatabaseService service;
	
	
	@Before
	public void prepareTestDoubles() throws SQLException{
		ds = mock(DataSource.class);
		connection = mock(Connection.class);
		statement = mock(PreparedStatement.class);
		resultSet = mock(ResultSet.class);
		when(ds.getConnection()).thenReturn(connection);
		service = new DatabaseService(ds);
	}
	
	@Test
	public void close_all_query_resources_in_proper_order() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		
		service.executeQuery(prepare("select * from dummy"), mock(ResultSetMapping.class));
		
		InOrder closeOrder = inOrder(connection,statement,resultSet);
		
		closeOrder.verify(resultSet).close();
		closeOrder.verify(statement).close();
		closeOrder.verify(connection).close();
	}
	
	@Test
	public void close_connection_when_query_statement_preperation_failed() throws SQLException{
		when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
		try {
			service.executeQuery(prepare("select * from dummy"), mock(ResultSetMapping.class));
			fail("SQL exceptieon expected!");
		} catch(DatabaseException e) {
			// Expected!
		}
		verify(connection).close();
		verifyZeroInteractions(statement,resultSet);
	}
	
	@Test
	public void close_connection_when_query_statement_execution_failed() throws SQLException{
		when(connection.prepareStatement(anyString())).thenReturn(statement);
		when(statement.executeQuery()).thenThrow(new SQLException());
		
		try {
			service.executeQuery(prepare("select * from dummy"), null);
			fail("SQL exception expected");
		} catch (DatabaseException e) {
			// Expected!
		}
		InOrder closeOrder = inOrder(connection,statement);
		closeOrder.verify(statement).close();
		closeOrder.verify(connection).close();
		verifyZeroInteractions(resultSet);
	}
	
	@Test
	public void close_all_resources_when_resultset_processing_failed() throws SQLException{
		when(connection.prepareStatement(anyString())).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		doThrow(new SQLException()).when(resultSet).next();
		try {
			service.executeQuery(prepare("select * from dummy"), mock(ResultSetMapping.class));
			fail("SQL exception expected!!");
		} catch(DatabaseException e) {
			// Expected!
		}
		
		InOrder closeOrder = inOrder(connection,statement,resultSet);
		
		closeOrder.verify(resultSet).close();
		closeOrder.verify(statement).close();
		closeOrder.verify(connection).close();
	}
	
	
	@Test
	public void close_all_update_resources_in_proper_order() throws SQLException {
		when(connection.prepareStatement(anyString())).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(0);
		
		service.executeUpdate(prepare("delete from dummy"));
		
		InOrder closeOrder = inOrder(connection,statement,resultSet);
		
		// Update statement does not create a result set.
		closeOrder.verify(statement).close();
		closeOrder.verify(connection).close();
	}
	
	@Test
	public void close_connection_when_update_statement_preperation_failed() throws SQLException{
		when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
		try {
			service.executeUpdate(prepare("delete from dummy"));
			fail("SQL exceptieon expected!");
		} catch(DatabaseException e) {
			// Expected!
		}
		verify(connection).close();
		verifyZeroInteractions(statement,resultSet);
	}
	
	@Test
	public void close_connection_when_update_statement_execution_failed() throws SQLException{
		when(connection.prepareStatement(anyString())).thenReturn(statement);
		when(statement.executeUpdate()).thenThrow(new SQLException());
		
		try {
			service.executeUpdate(prepare("Delete from dummy"));
			fail("SQL exception expected");
		} catch (DatabaseException e) {
			// Expected!
		}
		InOrder closeOrder = inOrder(connection,statement);
		closeOrder.verify(statement).close();
		closeOrder.verify(connection).close();
		verifyZeroInteractions(resultSet);
	}
	
	
	
	
	@Test
	public void set_enum_name_as_string() throws SQLException {
		// Prepare connection to return a mocked statement
		when(connection.prepareStatement(DUMMY_SQL)).thenReturn(statement);
		
		// Create function handle to prepare statement and pass an scalar as argument.
		// We use the TaskState enum but it can be virtually any enum.
		StatementPreparator handle = prepare(DUMMY_SQL, TestState.ACTIVE);
		handle.apply(connection);

		verify(statement).setString(1,TestState.ACTIVE.name());
		verifyNoMoreInteractions(statement);
	}
	
	@Test
	public void set_scalar_parameter_as_literal_value() throws SQLException{
		// Prepare connection to return a mocked statement
		when(connection.prepareStatement(DUMMY_SQL)).thenReturn(statement);
		
		// Create function handle to prepare statement and pass an scalar as argument.
		Scalar<String> scalar = new Scalar<String>() {
			@Override
			public String getValue() {
				return "dummy";
			}
		};
		StatementPreparator handle = prepare(DUMMY_SQL, scalar);
		handle.apply(connection);

		verify(statement).setObject(1,scalar.getValue());
		verifyNoMoreInteractions(statement);
	}
	
	@Test
	public void set_Date_as_SQL_timestamp() throws SQLException{
		// Prepare connection to return a mocked statement
		when(connection.prepareStatement(DUMMY_SQL)).thenReturn(statement);
		
		// Create function handle to prepare statement and pass the current date as argument.
		Date now = new Date();
		StatementPreparator handle = prepare(DUMMY_SQL, now);
		handle.apply(connection);

		verify(statement).setTimestamp(1,new Timestamp(now.getTime()));
		verifyNoMoreInteractions(statement);
	}
	
	
	@Test
	public void pass_all_other_types_as_object() throws SQLException {
		// Prepare connection to return a mocked statement
		when(connection.prepareStatement(DUMMY_SQL)).thenReturn(statement);
		
		// Create function handle to prepare statement and pass an scalar as argument.
		StatementPreparator handle = prepare(DUMMY_SQL, "Hello",Long.valueOf(123),Integer.valueOf(456));
		handle.apply(connection);

		verify(statement).setObject(1,"Hello");
		verify(statement).setObject(2,Long.valueOf(123));
		verify(statement).setObject(3, Integer.valueOf(456));
		verifyNoMoreInteractions(statement);
	}
	
	@Test
	public void process_all_resultset_records() throws SQLException{
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true)
					   .thenReturn(true)
					   .thenReturn(false);
		
		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetProcessor processor = mock(ResultSetProcessor.class);
		
		service.processQuery(stmt , processor);
		
		verify(processor,times(2)).process(rs);
	}
	
	@Test
	public void process_empty_resultset() throws SQLException{
		ResultSet rs = mock(ResultSet.class);

		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetProcessor processor = mock(ResultSetProcessor.class);
		
		service.processQuery(stmt , processor);
		
		verify(processor,never()).process(rs);
	}
	
	@Test
	public void processing_SQLException_is_wrapped_in_DatabaseException() throws SQLException{
		SQLException sqlException = new SQLException();
		
		exception.expect(DatabaseException.class);
		exception.expectCause(is(sqlException));
		
		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenThrow(sqlException);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetProcessor processor = mock(ResultSetProcessor.class);
		
		service.processQuery(stmt, processor);
		
		
	}
	
	@Test
	public void apply_mapping_to_all_resultset_records() throws SQLException{
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true)
					   .thenReturn(true)
					   .thenReturn(false);
		
		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetMapping<Object> mapping = mock(ResultSetMapping.class);
		
		service.executeQuery(stmt , mapping);
		
		verify(mapping,times(2)).map(rs);
	}
	
	@Test
	public void apply_no_mapping_to_empty_resultset_records() throws SQLException{
		ResultSet rs = mock(ResultSet.class);

		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetMapping<Object> mapping = mock(ResultSetMapping.class);
		
		service.executeQuery(stmt , mapping);
		
		verify(mapping,never()).map(rs);
	}
	
	@Test
	public void query_SQLException_is_wrapped_in_DatabaseException() throws SQLException{
		SQLException sqlException = new SQLException();
		
		exception.expect(DatabaseException.class);
		exception.expectCause(is(sqlException));
		
		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenThrow(sqlException);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		service.executeQuery(stmt ,  mock(ResultSetMapping.class));
		
	}
	
	@Test
	public void get_null_value_if_no_single_matching_records_exist() throws SQLException{
		
		ResultSet rs = mock(ResultSet.class);

		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetMapping<Object> mapping = mock(ResultSetMapping.class);
		
		assertNull(service.getSingleResult(stmt , mapping));
		
	}
	
	@Test
	public void get_translated_value_for_single_matching_record() throws SQLException{
		
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true)
					   .thenReturn(false);
		
		PreparedStatement ps = mock(PreparedStatement.class);
		when(ps.executeQuery()).thenReturn(rs);
		
		StatementPreparator stmt = mock(StatementPreparator.class);
		when(stmt.apply(any(Connection.class))).thenReturn(ps);
		
		ResultSetMapping<Object> mapping = mock(ResultSetMapping.class);
		
		Object entity = new Object();
		when(mapping.map(rs)).thenReturn(entity);
		
		assertSame(entity,service.getSingleResult(stmt , mapping));
		
	}
	
}
