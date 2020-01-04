/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.testing.it;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;

import io.leitstand.commons.db.DatabaseService;

public abstract class JpaIT {
	
	@FunctionalInterface
	protected static interface Transaction{
		void run();
	}

	private static boolean createSchema = true;
	static {
		System.setProperty("POSTGRES", "false");
	}

	private EntityManagerFactory emf;
	private DatabaseService database;
	private EntityManager em;
	private EntityTransaction tx;

	@Before
	public void before() throws IOException {
		emf = createEntityManagerFactory();
		em = emf.createEntityManager();
		tx = em.getTransaction();
		tx.begin();
	}

	private EntityManagerFactory createEntityManagerFactory() throws IOException {
		Properties connectionProperties = getConnectionProperties();
		DataSource dataSource = getDataSource(connectionProperties);
		prepareSchemas(dataSource);
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(getPersistenceUnitName(),
																			 			   connectionProperties);
		this.database = new DatabaseService(dataSource);
		return entityManagerFactory;
	}
	
	protected abstract Properties getConnectionProperties() throws IOException;
	protected abstract String getPersistenceUnitName();
	
	protected DataSource getDataSource(Properties properties) {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setUrl(properties.getProperty("javax.persistence.jdbc.url"));
		ds.setUser(properties.getProperty("javax.persistence.jdbc.user"));
		ds.setPassword(properties.getProperty("javax.persistence.jdbc.password"));
		return ds;
	}

	private void prepareSchemas(DataSource ds) {
		if(createSchema) {
			try {
				initDatabase(ds);
			} catch (SQLException e) {
				fail(e.toString());
			} finally {
				createSchema = false;
			}
		}
		
	}

	protected void initDatabase(DataSource ds) throws SQLException {
		try(Connection c = ds.getConnection()){
			c.createStatement().execute("CREATE SCHEMA leitstand");
			c.createStatement().execute("CREATE SCHEMA \"auth\"");
			c.createStatement().execute("CREATE SCHEMA inventory");
			c.createStatement().execute("CREATE SCHEMA job");
			c.createStatement().execute("CREATE SCHEMA grafana");
		} 
	}

	@After
	public void after() {
		if (tx != null && tx.isActive()) {
			tx.commit();
		}
		if (em != null) {
			em.close();
		}
		emf.close();
	}

	public final EntityManager getEntityManager() {
		return em;
	}

	public final DatabaseService getDatabase(){
		return database;
	}
	
	public final void beginTransaction() {
		getTransaction().begin();
	}
	
	public final void rollbackTransaction() {
		getTransaction().rollback();
	}
	
	public final void commitTransaction() {
		getTransaction().commit();
	}
	
	public void transaction(Transaction tx) {
		beginTransaction();
		tx.run();
		commitTransaction();
	}
	
	private final TransactionService getTransaction(){
		return new TransactionService() {
			
			@Override
			public void rollback() {
				if(tx != null && tx.isActive()){
					tx.rollback();
					em.clear();
				}
			}
			
			@Override
			public void commit() {
				if(tx != null && tx.isActive()){
					em.flush();
					tx.commit();
				}
			}
			
			@Override
			public void begin() {
				commit();
				em.clear();
				tx.begin();
			}
		};
	}
	
}
