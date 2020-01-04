/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.model;

import javax.persistence.EntityManager;

/**
 * A functional interface to facilitate the execution of named JPQL queries.
 * <p>
 * A {@literal @NamedQuery} is typically defined on an {@literal @Entity}. 
 * The <code>EntityManager</code> provides the methods to obtain a named query.
 * The named query in turn allows to set the query parameters and to execute the query.
 * The drawback of this generic API is that the consumer of the API must be aware of the query name
 * and all query parameters including parameter names or parameter positions and parameter types.
 * </p>
 * <p>
 * The intention of the <code>Query</code> is to encapsulate the query creation and parameter
 * propagation in a lambda expression. By that, a named query can be passed as argument to 
 * the {@link Repository} which executes the query.
 * The entity provides <code>static</code> query producer methods for each named query.
 * The parameter list of the producer method is a typed representation of the query parameters.
 * </p>
 * The <code>ElementGroup</code> entity defines a query named <code>ElementGroup.findByElementGroupName</code> as an example:
 * <pre>
 * <code>
 * {@literal @Entity}
 * {@literal @NamedQuery(name="ElementGroup.findByName", "SELECT p FROM ElementGroup p WHERE p.name=:name")}
 * public class ElementGroup {
 * 
 *   public static Query&lt;ElementGroup;gt; findGroupByName(ElementGroupName name){
 *     return em -&gt; em.createNamedQuery("ElementGroup.findGroupByName",ElementGroup.class)
 *     				  .setParameter("name",name)
 *     				  .getSingleResult();
 *   }
 * 	
 *   {@literal @Id}
 *   private Long id;
 * 
 *   {@literal @Column}
 *   private ElementGroupName name;
 *   ...
 * 
 * }
 * </code>
 * </pre>
 * A component can leverage the <code>Repository</code> to execute the query <code>repository.execute(ElementGroup.findGroupByElementGroupName(group))</code>
 * where <code>group</code> is the name of the requested group.
 * The producer method states clearly what parameters are expected and the IDE can be leveraged to find which components execute a certain query.
 * Additionally, the IDE displays the javadoc of the producer methods as context help.
 * 
 * @param <T> - the type of the query result which is either an entity or a list of entities
 */
@FunctionalInterface
public interface Query<T> {

	/**
	 * Leverages the passed entity manager to create and execute a query.
	 * @param em - the entity manager that runs the query
	 * @return the appropriate entity or <code>null</code> if no appropriate 
	 * 		   entity is available, or a list of appropriate entities or an empty 
	 * 		   list if no appropriate entities were found
	 */
	T execute(EntityManager em);
	
}
