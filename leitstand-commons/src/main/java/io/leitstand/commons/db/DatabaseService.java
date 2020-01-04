/*
 * (c) RtBrick, Inc All rights reserved, 2015 2019
 */
package io.leitstand.commons.db;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;
import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.FINE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.sql.DataSource;

import io.leitstand.commons.model.Scalar;

/**
 * The <code>DatabaseService</code> facilitates access to SQL databases via JDBC by means of encapsulation
 * of SQL error processing. 
 * <p>
 * The <code>DatabaseService</code> provides methods to create a prepared statement, to execute
 * a prepared statement and to process the result set of a SELECT statement. The service leverages functional 
 * interfaces to pass statement preparation as well as result set mapping as functions (lambda expressions) to the service.
 * This allows to combine statement preparation, execution and result set processing in a fluent programming model.
 * <p>
 * The <code>DatabaseService</code> is an <code>{@literal @ApplicationScoped}</code> service an can be obtained via 
 * CDI.
 */
public class DatabaseService {

	private static final Logger LOG = Logger.getLogger(DatabaseService.class.getName());
	static final boolean POSTGRES;
	
	static {
		String test = getProperty("POSTGRES","true");
		POSTGRES = parseBoolean(test);
	}
	
	static String regexp(String s) {
		if(POSTGRES) {
			// POSTGRES uses ~ rather than REGEXP for regular expression matches. 
			return s.replace(" REGEXP "," ~ ");
		}
		// H2, MariaDB among other database use REGEXP rather than ~
		return s.replace("~", " REGEXP ");
	}
	
	@FunctionalInterface
	private static interface Mapping<T> {
		void apply(PreparedStatement ps, int pos, T o) throws SQLException;
	}
	
	private static final Map<Predicate<Object>,Mapping<Object>> PARAMETER_MAPPINGS = new LinkedHashMap<>();
	
	static {
		
		// All scalars are mapped to the proper primitive type
		PARAMETER_MAPPINGS.put( t -> (t instanceof Scalar), 
								(ps,i,t) -> ps.setObject(i,((Scalar<?>)t).getValue()));	
		
		// Enums are mapped to STRING value (name())
		PARAMETER_MAPPINGS.put( t -> (t instanceof Enum), 
								(ps,i,t) -> ps.setString(i, ((Enum<?>)t).name()));

		// All dates (java.util.Date) are considered as timestamps.
		PARAMETER_MAPPINGS.put( t -> (t instanceof Date), 
								(ps,i,t) -> ps.setTimestamp(i, new Timestamp(((Date)t).getTime())));
		
		// UUID, StringBuilder and StringBuffer are considered as String values.
		PARAMETER_MAPPINGS.put( t -> (t instanceof UUID || t instanceof StringBuilder || t instanceof StringBuffer),
							    (ps,i,t) -> ps.setString(i,t.toString()));
		
		// Catch all mapping (let JDBC driver decide how to map the java type to the SQL type.
		PARAMETER_MAPPINGS.put( t -> true,
								(ps,i,t) -> ps.setObject(i, t));
		
	}
	
	private DataSource ds;
	
	protected DatabaseService() {
		// CDI
	}
	
	/**
	 * Created a new <code>DatabaseService</code>.
	 * @param ds the data source to connect to
	 */
	public DatabaseService( DataSource ds){
		this.ds = ds;
	}
	
	/**
	 * Returns a function to prepare a SQL statement on a specified JDBC connection.
	 * @param statement the SQL statement to be prepared
	 * @param params the parameters for all parameter markers in the specified SQL statement.
	 * @return a function to prepare a SQL statement in a specified JDBC connection.
	 */
	public static StatementPreparator prepare(final String statement, List<Object> params){
		return prepare(statement, params.toArray());
	}
	
	/**
	 * Returns a function to prepare a SQL statement on a specified JDBC connection.
	 * @param statement the SQL statement to be prepared
	 * @param params the parameters for all parameter markers in the specified SQL statement.
	 * @return a function to prepare a SQL statement in a specified JDBC connection.
	 */
	public static StatementPreparator prepare(final String statement, Object... params){
		return  c -> {
			PreparedStatement ps = c.prepareStatement(regexp(statement));
			if(params == null){
				return ps;
			}
			for(int i=0; i < params.length; i++){
				// Iterate over all mappings...
				for(Map.Entry<Predicate<Object>,Mapping<Object>> mapping : PARAMETER_MAPPINGS.entrySet()) {
					if(mapping.getKey().test(params[i])) {
						mapping.getValue().apply(ps, i+1, params[i]);
						// ... and break iteration, if a mappings was applied.
						break;
					}
				}
			}
			return ps;
		};
	}

	public static boolean convertToString(Object param) {
		return param instanceof UUID || param instanceof StringBuilder || param instanceof StringBuffer;
	}
	
	/**
	 * Obtains a database connection to prepare and execute the specified preparable SQL statement.
	 * Returns the number of affected database records.
	 * 
	 * @param stmt the producer function of the SQL statement 
	 * @return the number of modified records.
	 * @see #prepare(String, List) 
	 * @see #prepare(String, Object...)
	 */
	public int executeUpdate(StatementPreparator stmt){
		try(Connection c = ds.getConnection(); PreparedStatement ps = stmt.apply(c)){
			return ps.executeUpdate();
		} catch(SQLException e){
			LOG.log(FINE, e.getMessage(),e);
			throw new DatabaseException(e);
		}
	}
	
	
	/**
	 * Obtains a database connection to prepare and execute the specified preparable SQL statement and
	 * processes the returned resulset with the passed processor.
	 * 
	 * @param stmt the producer function of the SQL statement
	 * @param processor the result set processor
	 * @see #prepare(String, List)
	 * @see #prepare(String, Object...)
	 */
	public void processQuery(StatementPreparator stmt, ResultSetProcessor processor){
		try(Connection c = ds.getConnection(); 
			PreparedStatement ps = stmt.apply(c); 
			ResultSet rs = ps.executeQuery()){
			while(rs.next()){
				processor.process(rs);
			}
		} catch(SQLException e){
			LOG.log(FINE, e.getMessage(),e);
			throw new DatabaseException(e);
		}
	}
	
	
	/**
	 * Obtains a database connection to prepare and execute the specified preparable SQL statement and
	 * maps each row of the result set to an immutable value object.
	 * 
	 * @param stmt the producer function of the SQL statement
	 * @param mapper the result set mapper to create a value object from each result set record.
	 * @see #prepare(String, List)
	 * @see #prepare(String, Object...)
	 */
	public <T> List<T> executeQuery(StatementPreparator stmt, ResultSetMapping<T> mapper){
		try(Connection c = ds.getConnection(); 
			PreparedStatement ps = stmt.apply(c); 
			ResultSet rs = ps.executeQuery()){
			List<T> results = new LinkedList<>();
			while(rs.next()){
				results.add(mapper.map(rs));
			}
			return unmodifiableList(results);
		} catch(SQLException e){
			LOG.log(FINE, e.getMessage(),e);
			throw new DatabaseException(e);
		}
	}
	
	public <T> T getSingleResult(StatementPreparator stmt, ResultSetMapping<T> mapper){
		List<T> items = executeQuery(stmt, mapper);
		if(items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}
	
}