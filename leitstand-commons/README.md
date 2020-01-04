# Leitstand Module

This document outlines the conventions and the key Leitstand foundation classes for implementing Leitstand Java modules.

## Module CDI Qualifier

__Convention:__ _Each Leitstand module provides a CDI qualifier._

By that CDI can manage certain infrastructure component per Leitstand module.
The listing below shows the CDI qualifier for the Leitstand inventory module.

```java
package io.leitstand.inventory.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * Leitstand inventory module qualifier.
 */
@Retention(RUNTIME)
@Target({METHOD, PARAMETER, FIELD, TYPE})
@Inherited
@Qualifier
public @interface Inventory {
	// Marker annotation
}
```

__Convention:__ _The module qualifier is always part of the module implementation and never part of the module API._

## Value Objects
A value object models the data that is being exchanged over the module's API.
Value objects are immutable. Changing the attributes of a value object requires the instantiation of a new value object. Two value objects are considered equal if both objects are of the same data type and the attributes of both value objects have the same values.

__Convention:__ _Value objects are considered to be valid. It is not allowed to create invalid value objects._

This convention ensures that all service and process implementations can bank on not being invoked when a value object is not valid and consequently do not have to validate the value object itself again.

Leitstand uses _Bean Validation_ to validate value objects.

### Scalar

A scalar is a value object that consist of a single attribute only.

__Convention:__ _All scalars are derived from `io.leitstand.commons.model.Scalar`._

A scalar is immutable, comparable and obeys the [java.lang.Object equals and hashcode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#equals(java.lang.Object)) and [comparable](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Comparable.html)  contracts.
The `io.leitstand.commons.model.Scalar` supplies a generic implementation of both contracts for all classes derived - directly or indirectly - from `Scalar`. 
A scalar improves type-safeness, because typed values instead of default Java data types are exchanged. 
This enables the compiler to check whether the correct data types are passed to the API call and enables IDE code assist to provide more information on what parameters a method expects.

The listing below shows the element name scalar, that describes the unique name of an element in the resource inventory:

```java
package io.leitstand.inventory.service;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.leitstand.commons.model.Scalar;
import io.leitstand.inventory.jsonb.ElementNameAdapter;

/**
 * The <code>ElementName</code> is a unique name of an element.
 * <p>
 * An element can consist of up to 64 characters. 
 * The element name is unique among all existing elements in the network.
 * While the {@link ElementId} is immutable, the element name can be updated
 * provided that the new element name is still unique.
 * </P>
 */
@JsonbTypeAdapter(ElementNameAdapter.class)
public class ElementName extends Scalar<String> {

  private static final long serialVersionUID = 1L;
	
  /**
   * Creates an <code>ElementName</code> from the passed literal value. Returns <code>null</code>
   * if the passed literal value is <code>null</code>, empty or consists of whitespaces only.
   * @param name the element name.
   * @return the typed element name or <code>null</code> if the passed literal value is 
   * <code>null</code>, empty or consists of whitespaces only.
   */
  public static ElementName valueOf(String name) {
    return fromString(name,ElementName::new);
  }
   
  /**
   * Alias of the {@link #valueOf(String)} method to improve readability.
   * <p>
   * Creates an <code>ElementName</code> from the specified string.
   * @param name the element name
   * @return the <code>ElementName</code> or <code>null</code> if the specified string is <code>null</code> or empty.
   */
  public static ElementName elementName(String name) {
    return valueOf(name);
  }
   
  @NotNull(message="{element_name.required}")
  @Pattern(message="{element_name.invalid}", regexp="\\p{Print}{1,64}")
  private String value;
	
  /**
   * Creates a new <code>ElementName</code> instance.
   * @param name the element name
   */
  public ElementName(String name){
    this.value = name;
  }
	
  /**
   * {@inheritDoc}
   */
  @Override
  public String getValue() {
    return value;
  }
}
```

A JPA converter and a JSON-B type adapter is needed to translate a scalar into a base type and vice versa.
The listings below show the `ElementNameAdapter` and `ElementNameConverter` implementations:

```java
package io.leitstand.inventory.jsonb;

import javax.json.bind.adapter.JsonbAdapter;

import io.leitstand.inventory.service.ElementName;

public class ElementNameAdapter implements JsonbAdapter<ElementName,String> {

  @Override
  public String adaptToJson(ElementName obj) throws Exception {
    return ElementName.toString(obj);
  }

  @Override
  public ElementName adaptFromJson(String obj) throws Exception {
    return ElementName.valueOf(obj);
  }

}
```

```java
package io.leitstand.inventory.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import io.leitstand.inventory.service.ElementName;

@Converter
public class ElementNameConverter implements AttributeConverter<ElementName, String>{

  @Override
  public String convertToDatabaseColumn(ElementName attribute) {
    return ElementName.toString(attribute);
  }

  @Override
  public ElementName convertToEntityAttribute(String dbData) {
    return ElementName.valueOf(dbData);
  }

}
```

__Convention:__ _Each scalar provides a `static valueOf` factory method to instantiate the scalar from the default Java type. The factory method returns `null` if the Java type is_ `null` or an empty string._

Each scalar can provide an additional factory method to avoid static import conflicts. 
For example, `ElementName` provides an additional static `elementName` factory method. 



### ValueObject and CompositeValue

A value object consists of multiple attributes.

__Convention:__ _All value objects are derived from `io.leitstand.commons.model.ValueObject`._

A value object is immutable and obeys the [java.lang.Object equals and hashcode](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#equals(java.lang.Object)) contract.
`io.leitstand.commons.model.ValueObject` supplies a generic implementation of this contract which applies to all classes derived - directly or indirectly - from `ValueObject`.
A value object can contain scalars, nested value objects and Java default types (e.g. `String` or `Date`).

#### CompositeValue

A composite value is a value object that consists of multiple attributes but can also be considered as a single string. 
For example, a semantic version numbers contains the major, minor, patch and optional pre-release versions but is also represented as single string in `major.minor.patch[-prerelease]` format.

__Convention:__ _All composite values are derived from_ `io.leitstand.commons.model.CompositeValue`.

## Services

A service implements a certain function or set of functions.

__Convention:__ _Service implementations are stateless and thread-safe._

The `@Service` annotation makes the service an [`@ApplicationScoped`](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/context/ApplicationScoped.html) [`@Transactional`](https://javaee.github.io/javaee-spec/javadocs/javax/transaction/Transactional.html) CDI managed bean.

__Convention:__ _Transactional services are annotated with the Leitstand `@Service` annotation._

A control flow orchestrates service invocations, 
executes a sequence of transactions, each represented by a service method invocation.
Each control flow is processed within a single request.

__Convention:__ _Control flows are annotated with the Leitstand `@ControlFlow` annotation._ 

`@ControlFlow` makes the control flow a [`@Dependent`-scoped](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/context/Dependent.html) CDI managed bean.

The `TaskUpdateFlow` is an example for a control flow: the first transaction updates the task state and searches for tasks eligible for execution. 
All discovered tasks are executed in a dedicated transaction.

## Entities
An entity is a persistent object.
Every entity has a unique _identity_.
The identity of an entity never changes.
Two entities are considered to be the same entity, if they have the same identity, even if entity attributes have different values.
In that case two different versions of the same entity exist.
Entities can have dependencies to other entities thereby forming an object model. 

Leitstand uses JPA to implement a persistent object model.

__Convention:__ _The identity of an aggregate root entity is a contiguous number (long)._

__Convention:__ _The identity is never exposed to the API, rather each aggregate root entity provides an immutable UUID in v4 format as external identifier._

__Convention:__ _The identity of an aggregate sub-resource entity is typically a composite key that contains the aggregate root identity and a descriptive name._

__Convention:__ _The naming convention for an aggregate sub-resource entity obeys `<AGGREGATE>_<SUBRESOURCE>`._


### Example

The resource inventory element entity is an aggregate root with a bunch of sub-resources such as physical interfaces for example.
Each element has a certain role like LEAF or SPINE.
An element role can exist even if no element for that particular is available.
Consequently, an element role can exist by its own but does not form an aggregate root because it has no sub-resources.
Obeying the conventions mentioned before, the resource inventory contains the following entities and tables:

| Entity 		| Table 					| Identity (Primary Key) | Description 			  																													   	|
| :------------	| :--------------------	| :--------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------	|	
| `Element`		| `inventory.element` 	| `id` 					 | Element root aggregate. 																														|
| `Elemen_Ifp`  	| `inventory.element_ifp`	| `element_id`, `ifp_name` | Physical interface. Identity is formed by the element identity (`element_id`) and the interface name (`ifp_name`), which is unique per element 	|
| `ElementRole` 	| `inventory.role` 		| `id` 					 | Element role.																										   							|

The naming conventions simplifies to navigate through the object model:

- An `id` column indicates that this entity can exist by its own.
- A `<TABLE>_id` column indicates that this entity is a sub-resource of `<TABLE>`.

For more information on the naming conventions see the resource inventory and job scheduler documentations.

### AbstractEntity
`io.leitstand.commons.model.AbstractEntity` is a base class for Leitstand entities.
It introduces the `id` column mentioned before along with a generator configuration to set the `id` automatically.
Moreover, it introduces a creation timestamp (`tscreated`) and last modification timestamp (`tsmodified`).

### VersionableEntity
`io.leitstand.commons.model.VersionableEntity` extends `AbstractEntity` by a modification counter (`modcount`) and an UUID (`uuid`) as external reference.
The `modcount` is leveraged for optimistic locking in order to detect concurrent updates of the same entity.

__Convention:__ _Aggregate root entities are derived from `VersionableEntity`._

This allows [OPTIMISTIC_FORCE_INCREMENT](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/LockModeType.html)-locking of an aggregate root in order to detect concurrent updates on sub-resources. 

## Repository
The repository is used to add new entities, to remove existing entities, and to query entities.
 `io.leitstand.commons.model.Repository` is a generic repository that can be leveraged for all Leitstand modules and all entities.

### Query

`io.leitstand.commons.model.Query` is a _functional interface_ intended for implementing _named JPQL queries_ as lambda expressions.
The entity provides a static factory method to create the JPQL lambda expression.

The listing below shows the `@NamedQuery` to search for elements by name, the lambda expression and the factory method:

```java
@NamedQuery(name="Element.findByElementName", 
            query="SELECT e FROM Element e WHERE e.elementName=:name")

...

public static Query<Element> findElementByName(ElementName name) {
  return em -> em.createNamedQuery("Element.findByElementName",Element.class)
                 .setParameter("name",name)
                 .getSingleResult();
}
```

The listing below outlines how to query for an element

```java
import static io.leitstand.inventory.model.Element.findElementByName;
...
@Inject
@Inventory
private Repository repository;
...
ElementName elementName = ...

// Fetch element and for updating sub-resource
Element element = repository.execute(findElementByName(elementName));

```

The key advantage of lambda expressions is to encapsulate the mapping of search arguments to the underlying query in the factory method.


__Convention:__ _All JPQL queries are implemented as lambda expressions._


### Update
`io.leitstand.commons.model.Update` implements the same concept as outlined before for executing updates.
An update returns the number of records being subject of the update.

The listing below shows a sample update statement to remove all element metric bindings for a given metric.


```java
@NamedQuery(name="Element_Metric.removeBindings",
			query="DELETE FROM Element_Metric m WHERE m.metric=:metric")

public static Update removeElementMetricBindings(Metric metric) {
  return em -> em.createNamedQuery("Element_Metric.removeBindings",int.class)
                 .setParameter("metric",metric)
                 .executeUpdate();
}
```

The execution of an update works like the execution of a query:

```java
import static io.leitstand.inventory.model.Element_Metric.removeElementMetricBindings;
...
@Inject
@Inventory
private Repository repository;
...
Metric metric = ...

// Remove all element metric bindings.
int count = repository.execute(removeElementMetricBindings(metric));

LOG.fine( () -> format("%d bindings removed, count));
```

### Producer

The persistent context contains all entities and auxiliary classes of a persistent model.

__Convention:__ _Each Leitstand module defines its own persistence context._

The _repository producer_ creates the repository including the entity manager for the persistence context.

Basically two different types of JTA entity manager exists:
- _transaction-scoped_ entity managers are bound to the transaction lifecyle.
- _extended_ entity managers can be used for subsequent transactions, which requires to synchronize the entity manager for new transactions.

An _attached_ entity is managed by the entity manager and all changes to an attached entity are written to the database when the transaction gets committed.

Sharing a managed entity across multiple entity managers is not allowed as stated in Section _3.2.8 Managed Instances_ of the JPA specification:

> It is the responsibility of the application to insure that an instance is managed in only a single persistence context. The behavior is undefined if the same Java instance is made managed in more than one persistence context.

JTA, the Java Transaction API, does not support nested transactions.
A running transaction is suspended when a new transaction is started and will be resumed after the new transaction is completed.
Using transaction-scoped JTA entity-managers ensures that the CDI container will create a new entity manager for every new transaction,
as required by the JPA specification.

__Convention:__ _Leitstand uses only transaction-scoped JTA entity managers to avoid troubles._

The listing below shows the repository producer of the inventory module:

```java
/**
 * Produces transaction-scoped repositories for the inventory module.
 */
@Dependent
public class InventoryRepositoryProducer {

  @PersistenceUnit(unitName="inventory")
  private EntityManagerFactory emf;
	
  /**
   * Obtains a transaction-scoped entity manager to create an inventory repository.
   * @return a transaction-scoped inventory repository.
   */
  @Produces
  @TransactionScoped
  @Inventory
  public Repository createInventoryRepository() {
    return new Repository(emf.createEntityManager());
  }
	
  /**
   * Closes a repository and the underlying transaction-scoped entity manager.
   * @param repository the repository to be closed
   */
  public void closeRepository(@Disposes @Inventory Repository repository) {
    repository.close();
  }
	
}
```

It is important to close a repository after the transaction ended.

## Database Service

The `io.leitstand.commons.db.DatabaseService` simplifies the execution of SQL statements and the processing of the result set.
The `DatabaseService` opens the database connection, executes the SQL statement, iterates over the result set and applies the given mapping function to each row.
The database connection is obtained from a data source, which maintains a connection pool.
The `DatabaseService` also closes all JDBC resources.

The listing below outlines how to execute a SELECT statement and to map each row of the returned result set to a value object.
The SQL query in the example reads the metadata of the latest element configurations. 
Each element configuration has a unique name.

```Java
import static io.leitstand.commons.db.DatabaseService.prepare;
import static io.leitstand.inventory.service.ConfigurationState.configurationState;
import static io.leitstand.inventory.service.ElementConfigId.elementConfigId;
import static io.leitstand.inventory.service.ElementConfigName.elementConfigName;
import static io.leitstand.inventory.service.ElementConfigReference.newElementConfigReference;
import static io.leitstand.security.auth.UserId.userId;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import io.leitstand.commons.db.DatabaseService;
import io.leitstand.inventory.service.ElementConfigReference;

...

@Inject
@Inventory
private DatabaseService database;

...

List<ElementConfigReference> configs = database.executeQuery(prepare("WITH history AS ("+ 
                                                                     "SELECT element_id, name, max(tsmodified) AS tsmodified "+
                                                                     "FROM inventory.element_config "+
                                                                     "WHERE element_id = ? "+
                                                                     "AND name ~ ? "+
                                                                     "GROUP BY element_id, name )"+
                                                                     "SELECT c.uuid, c.name, c.state, c.creator, c.comment, c.contenttype, c.tsmodified "+
                                                                     "FROM history h "+
                                                                     "JOIN inventory.element_config c "+
                                                                     "ON h.name = c.name "+
                                                                     "AND h.element_id = c.element_id "+
                                                                     "AND h.tsmodified = c.tsmodified "+
                                                                     "ORDER BY c.name ",	
                                                                     element.getId(),
                                                                     filter),
                                                             rs -> newElementConfigReference()
                                                                   .withConfigId(elementConfigId(rs.getString(1)))
                                                                   .withConfigName(elementConfigName(rs.getString(2)))
                                                                   .withConfigState(configurationState(rs.getString(3)))
                                                                   .withCreator(userId(rs.getString(4)))
                                                                   .withComment(rs.getString(5))
                                                                   .withContentType(rs.getString(6))
                                                                   .withDateModified(rs.getTimestamp(7))
                                                                   .build());
```

The `DatabaseService` provides methods to process a list of results, a single result or to execute an update.

### Regular Expression Support
Many modern databases support regular expressions in addition to the SQL pattern matching.
Unfortunately, there is no standard _how_ to match for a regular expression, which is why different syntaxes exist.

| Database   | Regular Expression Matcher |
| :---------:| ---------------------------|
| PostgreSQL | `~` |
| MariaDB    | `REGEXP` |
| MySQL 	 | `REGEXP` |
| H2		 | `REGEXP` |

The `DatabaseServices` translates `~` to `REGEXP` if another database as PostgreSQL is in use. 
This also enables tests to be run with [H2](https://www.h2database.com/html/main.html) as in-memory database.

### Producer
The `DatabaseService` requires a managed datasource to open database connections.
The _database service producer_ creates a database service per datasource.

The listing below shows the producer of the inventory database service.

```Java
@Dependent
public class InventoryDatabaseServiceProducer {

  @Resource(lookup="java:/jdbc/leitstand")
  private DataSource ds;
	
  @Produces
  @ApplicationScoped
  @Inventory
  public DatabaseService createInventoryDatabaseService() {
   	return new DatabaseService(ds);
  }
	
}
```

The producer obtains the managed datasource as resource from the runtime environment and creates an application scoped database service instance.

## Domain Events
A domain event informs about a transaction that has been successfully completed.
Domain events are _transactional_, which means that a domain event is not fired if the transaction rolls back.

The listing below outlines how to fire a domain event

```Java
import static io.leitstand.inventory.event.ElementRenamedEvent.newElementRenamedEvent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

...

@Inject
private Event<ElementEvent> event;

...

event.fire(newElementRenamedEvent()
           .withGroupId(element.getGroupId())
           .withGroupName(element.getGroupName())
           .withGroupType(element.getGroupType())
           .withElementId(element.getElementId())
           .withElementName(element.getElementName())
           .withElementAlias(element.getElementAlias())
           .withElementRole(element.getElementRoleName())
           .withPreviousName(previousElementName)
           .build());

```

CDI is used to fire and observe events.

Leitstand observes all events and stores them as _domain event_ in a message table.
The domain event contains the original event as payload and adds the following metadata:
- a unique domain event ID in UUIDv4 format
- the simple class name of the event value object as domain event type
- the event topic and
- the event creation date.

The events are only stored in the messages table if the transaction gets committed.
Processors can query events from the message queue.

## Resource

Resources rely on the JAX-RS and JSON-B APIs and implement the REST API of a Leitstand module.
Each resource validates all input parameters with bean validation ( `@Valid` ) and 
checks whether the caller has sufficient privileges to execute the requested operation (`@RolesAllowed`).

__Convention:__ _A resource is a [`@RequestScoped`](https://javaee.github.io/javaee-spec/javadocs/javax/enterprise/context/RequestScoped.html) CDI-managed bean_.


The listing below shows the resource that implements the REST API endpoint to manage the general settings of an element.

```Java
/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.inventory.rs;

import static io.leitstand.commons.UniqueKeyConstraintViolationException.key;
import static io.leitstand.commons.model.ObjectUtil.isDifferent;
import static io.leitstand.commons.model.Patterns.UUID_PATTERN;
import static io.leitstand.commons.rs.ReasonCode.VAL0003E_IMMUTABLE_ATTRIBUTE;
import static io.leitstand.commons.rs.Responses.created;
import static io.leitstand.commons.rs.Responses.success;
import static io.leitstand.inventory.service.ReasonCode.IVT0307E_ELEMENT_NAME_ALREADY_IN_USE;
import static io.leitstand.security.auth.Role.OPERATOR;
import static io.leitstand.security.auth.Role.SYSTEM;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.leitstand.commons.EntityNotFoundException;
import io.leitstand.commons.UniqueKeyConstraintViolationException;
import io.leitstand.commons.UnprocessableEntityException;
import io.leitstand.commons.messages.Messages;
import io.leitstand.inventory.service.ElementId;
import io.leitstand.inventory.service.ElementName;
import io.leitstand.inventory.service.ElementSettings;
import io.leitstand.inventory.service.ElementSettingsService;

/**
 * Manages the general settings of an element.
 */
@RequestScoped
@Path("/elements")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ElementSettingsResource{

  @Inject
  private ElementSettingsService service;
  
  @Inject
  private Messages messages;
  
  /**
   * Loads the general settings of the specified element.
   * @param element the element UUID
   * @return the element settings
   */
  @GET
  @Path("/{element:"+UUID_PATTERN+"}/settings")
  public ElementSettings getElementSettings(@Valid @PathParam("element") ElementId element){
    return service.getElementSettings(element);
  }

  /**
   * Load the general settings of the specified element.
   * @param element the element name
   * @return the element settings
   */
  @GET
  @Path("/{element}/settings")
  public ElementSettings getElementSettings(@Valid @PathParam("element") ElementName element){
    return service.getElementSettings(element);
  }
  
  /**
   * Stores the general settings of the specified element.
   * Creates a new element if the specified element does not exist.
   * @param element the element UUID
   * @param settings the element settings
   * @return a created response if a new element was created, a success response if the settings of an existing element have been updated
   */
  @PUT
  @Path("/{element:"+UUID_PATTERN+"}/settings")
  @RolesAllowed({OPERATOR,SYSTEM})
  public Response storeElementSettings(@Valid @PathParam("element") ElementId element, 
                                       @Valid ElementSettings settings){
    
    if (isDifferent(element, settings.getElementId())) {
      throw new UnprocessableEntityException(VAL0003E_IMMUTABLE_ATTRIBUTE, 
                                             "element_id", 
                                             element, 
                                             settings.getElementId()); 
    }
    
    try {
      if(service.storeElementSettings(settings)){
        return created(messages,"/elements/%s/settings",element);
      }
      return success(messages);
    } catch (PersistenceException e) {
      throw resolveRootCause(e,settings.getElementName());
    }
  }
  
  
  /**
   * Stores the general settings of the specified element.
   * Creates a new element if the specified element does not exist.
   * @param element the element UUID
   * @param settings the element settings
   * @return a created response if a new element was created, a success response if the settings of an existing element have been updated
   */
  @PUT
  @Path("/{element}/settings")
  @RolesAllowed({OPERATOR,SYSTEM})
  public Response storeElementSettings(@PathParam("element") ElementName element, 
                                       @Valid ElementSettings settings){
    try {
      if(service.storeElementSettings(settings)){
        return created(messages,"/elements/%s/settings",element);
      }
      return success(messages);
    } catch (PersistenceException e) {
      throw resolveRootCause(e,element);
    }
  }
  
  
  /**
   * Adds a new element or updates an existing element.
   * @param settings the element settings
   * @return a created response if a new element was created, a success response if the settings of an existing element have been updated
   */
  @POST
  @RolesAllowed({OPERATOR,SYSTEM})
  public Response storeElementSettings(@Valid ElementSettings settings){
    return storeElementSettings(settings.getElementId(),
                   settings);
  }

  private PersistenceException resolveRootCause(PersistenceException p, ElementName elementName) {
    try {
      // A unique key constraint violation occurred if an element with the given name exists.
      service.getElementSettings(elementName);
      throw new UniqueKeyConstraintViolationException(IVT0307E_ELEMENT_NAME_ALREADY_IN_USE,
                                                      key("element_name", elementName) );
    } catch(EntityNotFoundException e) {
      // Element does not exist. Continue with original exception
      throw p;
    }
  }
  

}

```

### Responses

The `io.leitstand.commons.rs.Responses` class provides factory methods to simplify the creation of REST API responses.
For example, the `success` method selects the HTTP success status code based on whether the response entity is empty (`204 No content`) or not (`200 OK`).

### ApiResourcesProvider

Leitstand modules can either be assembled as monolithic application or deployed in a distributed, microservice-flavored style. 
The `@ApplicationPath` must be unique in an application assembly. 
Leitstand leverages CDI bean discovery to solve this problem.

__Convention:__ _All Leitstand modules provide an `ApiResourceProvider` to declare the REST API resources._

The `ApiResourceProvider` simply returns a list of resource classes.
The listing below is an excerpt of the `io.leitstand.commons.rs.ApiResourceProvider` implementation of the resource inventory.

```java
package io.leitstand.inventory.rs;

import static io.leitstand.commons.model.ObjectUtil.asSet;

import java.util.Set;

import javax.enterprise.context.Dependent;

import io.leitstand.commons.rs.ApiResourceProvider;

/**
 * Provider of all inventory module REST API resources.
 */
@Dependent
public class InventoryResources implements ApiResourceProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Class<?>> getResources() {
    return asSet( CloneElementResource.class,
                  ElementConfigResource.class,
                  ElementGroupElementsResource.class,
                  ...
                  OperationalStateReader.class);
  }

}
```

## Lifecycle Listeners

Leitstand defines two lifecycle listeners:

- The `io.leitstand.commons.StartupListener` is notified when a Leitstand module gets started.
- The `io.leitstand.commons.ShutdownListener` is notified when Leitstand is being stopped.

A module can contain multiple listener implementations.

__Constraint:__ _A listener implementation __must__ be either a `@Dependent` or `@ApplicationScoped`_.


## Leitstand Reason Codes

Leitstand uses an 8-digit reason code to identify each status message unambiguously.
The first three digits identify the Leitstand module.
The next four digits form the module-internal reason code number. 
The reason code number allocation is left to the module.
The last digit represents the severity:
- `E` for `ERROR` severity
- `W` for `WARNING` severity
- `I` for `INFORMATION` severity

The following module identifiers exist today:

| Identifier | Module              |
| :---------:| :------------------ |
| IVT     	 | Resource Inventory  |
| JOB		 | Job Scheduler       |
| BUS		 | Message Bus         |
| WHK		 | Webhooks  		   |
| IDM		 | Identity Management |
| AKY		 | Access Keys 		   |
| AUT		 | Authentication 	   |

__Convention:__ _Each module supplies an enumeration of existing reason codes that implements the `io.leitstand.commons.Reason` interface._

### Leitstand Exception

Leitstand fires an exception to cancel the processing of the current request.
All active transactions originated from request are rolled back

A Leitstand exception conveys a reason code to explain the root cause of the reported problem.

__Convention:__ _The `io.leitstand.commons.LeitstandException` is the base class for all Leitstand exceptions._

The `LeitstandException` class conveys the reason code as stated before.

The following pre-defined exceptions exists:

- `io.leitstand.commons.AccessDeniedException` is fired when the callers has insufficient privileges.
- `io.leitstand.commons.ConflictException` is fired when the attempted modification is conflicting with the current resource state. 
- `io.leitstand.commons.EntityNotFoundException` is fired when an entity does not exist. 
- `io.leitstand.commons.UniqueKeyConstrainViolationException` is fired when an attempted operation violates a unique key constraint in the underlying database.
- `io.leitstand.commons.UnprocessableEntityException` is fired when a request was syntactically correct but is semantically wrong

All Leitstand exceptions are resolved by a corresponding `ExceptionMapper`.
The exception mapper translates a Leitstand exception to a REST API response:

| ExceptionMapper 													| HTTP Status 				|
| :-----------------------------------------------------------------	| :------------------------	|
| `io.leitstand.commons.rs.AccessDeniedExceptionMapper`				 	| `403 Forbidden `			|
| `io.leitstand.commons.rs.ConflictExceptionMapper` 					 	| `409 Conflict`				|
| `io.leitstand.commons.rs.EntityNotFoundExceptionMapper`			 	| `404 Not Found` 			|
| `io.leitstand.commons.rs.OptimisticLockExceptionMapper` 			 	| `409 Conflict`				|
| `io.leitstand.commons.rs.UniqueKeyConstraintViolationExceptionMapper`	| `409 Conflict`				|
| `io.leitstand.commons.rs.UnprocessableEntityExceptionMapper`			| `422 Unprocessable Entity `	|
| `io.leitstand.commons.rs.ValidationExceptionMapper`                 	| `422 Unprocessable Entity `	|

The `ValidationExceptionMapper` resolves all bean validation issues.

The following reason codes are set by the Java EE runtime environment by default:
- `400 Bad request` for syntactically incorrect data (e.g. malformed JSON)
- `401 Unauthorized` for unauthenticated requests.
- `405 Method Not Allowed` when the API endpoint does not support the specified HTTP method. 
   For example, sending a `PUT` message to an endpoint that accepts `POST` messages only results in a `405 Method Not Allowed`reply.
- `412 Unsupported Media Type` if the API endpoint does not support content type of the request entity. 
   For example, sending `text/plain` data to an API endpoint that only accepts `application/json` data results in a `412 Unsupported Media Type` reply.
- All other exceptions are reported as `500 Internal Server Error`.


### Messages
`io.leitstand.commons.messages.Messages` is a `@RequestScoped` container to convey status messages to the client without aborting the transaction.
A status message provides details about the outcome of an operation.
A message also includes the reason code as described before.

The listing below outlines how to create a status message.
First the service obtains a reference to the `Messages` container and then simply uses the `MessageFactory` to create a new message.

```java
import static io.leitstand.commons.messages.MessageFactory.createMessage;
...

@Inject
private Messages messages;
	
...
	
Metric metric = ...
repository.remove(metric);
LOG.fine(() -> format("%s: %s metric removed",
  				     IVT0602I_METRIC_REMOVED.getReasonCode(),
					 metricName));
			
messages.add(createMessage(IVT0602I_METRIC_REMOVED,
						 metricName));
```

The message factory reads the message text from a resource bundle.
The reason code enum constant is used as key:

```
IVT0602I_METRIC_REMOVED=Metric {0} removed.
```

The message text can contains parameters, which are replaced by the parameters passed to the message factory call.
For example, the parameter `{0}` in the message template gets replaced by the metric name (`metricName`).


The REST resource sends all messages to the caller.
The `success` method of the `Responses` class checks whether messages exist and creates a `204 No Content` response if the message collection is empty.
Otherwise a `200 OK` response is send conveying the messages in the response entity.

```java
import static io.leitstand.commons.rs.Responses.success;
...
@Inject
private MetricService metric;
...
@DELETE
@Path("/{metric}")
@RolesAllowed({OPERATOR,SYSTEM})
public Response removeMetric(@PathParam("metric") MetricName metricName,
						   @QueryParam("force") boolean force) {
  if(force) {
    service.forceRemoveMetric(metricName);
  } else {
    service.removeMetric(metricName);
  }
  return success(messages);
}
```
## Logging

Leitstand considers the target audiences of log messages by means of differentiating between _log_ and _trace_ information.

- Log information is targeted to Leitstand administrators.
  A log message contains a status information that is important for a Leitstand administrator's day-to-day work, 
  especially system errors that require some remedy.
- Trace information is targeted to Leitstand developers.
  A trace message contains details about the outcome of an operation, specifically about application errors.
  Application errors are issues that have to be solved by the caller rather than the Leitstand administrator.
 
The rationale of keeping log and trace information strictly separated is to avoid that system errors are hidden by application errors.

Leitstand uses the following conventions to simplify log and trace seggregation:

- All log levels >= INFO are used for log messages.
- All log levels < INFO are used for trace messages.

This enables to route log and trace messages into different files based on log level filters.

## Java Package Conventions

Leitstand does not impose strict package naming conventions.
It is basically the responsibility of the module supplier to introduce as well-devised package structure.
More important than package names is to avoid ciruclar dependencies between packages.
Packages with circular dependencies must be kept in the same java library.
This can become a limitation when a certain packages shall be moved into different JARs such that different implementations can be chosen at assembly time.

__Convention:__  _Leitstand packages have no circular dependencies!_

The existing Leitstand modules use the following package structure:

- `io.leitstand.[module].[submodule].service` packages contain the transactional API of the Leitstand module.
- `io.leitstand.[module].[submodule].model` packages contain the domaim model and implement the transactional service API.
- `io.leitstand.[module].[submodule].event` packages contain the events fired by the model.
- `io.leitstand.[module].[submodule].rs` packages contain the REST API endpoints mapping the REST API calls to the transactional API.

The submodule segment is optional. It is used if a module contains multiple bounded contexts, i.e. different sets of services that implement a specific feature set each.
For example, the Leitstand security module contains a submodule for encryption, a submodule for access keys and another submodule for user authentication.

### Assembly

Leitstand APIs are always packed in a dedicated JAR file with `-api` suffix, whereas the model, events, and resources are bundled in a JAR file with `-model` suffix.
For example, `leitstand-inventory-api` contains the Leitstand Resource Inventory API and `leitstand-inventory-model` contains the inventory implementation, including the REST API implementation.

The rationale behind this segregation is that API consumers get no access to implementation classes by not adding the implementation classes to the compile class path.