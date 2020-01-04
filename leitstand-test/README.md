# Leitstand Testing

Leitstand uses [Mockito](https://site.mockito.org "Visit Mockito Website") to isolate the _Unit under Test_ from other units.
[JUnit](https://junit.org/junit5/ "Visit Junit Website") is used as test automation framework.

__Convention:__ _All unit test classes are suffixed with `Test`._

## Exception Validation

Leitstand throws a  `LeitstandException` with a proper reason code
if a request cannot be processed.
The `LeitstandCoreMatchers` class provides a matcher to validate the reason code.
The source snippet below outlines how to validate an expected exception. 

```Java
import static io.leitstand.testing.ut.LeitstandCodeMatchers.reason;

...

@Rule
public ExpectedException exception = ExpectedException.none();

...

@Test
public void cannot_remove_active_element() {
  // Mock an active element
  Element activeElement = mock(Element.class);
  when(activeElement.isActive()).thenReturn(true);
 
  // Declare what exception and reason code is excepted.	
  exception.expect(ConflictException.class);
  exception.expect(reason(IVT0303E_ELEMENT_NOT_REMOVABLE));
	
  try {
    // Attempt to remvove an active element is expected to report a conflict.
    manager.remove(activeElement);
  } finally {
	// Verify that element was not removed from the database...
	verify(repository,never()).remove(activeElement);
	// and that also no removed event was fired.
	verify(event,never()).fire(any());
  }
}
```

## Transaction Testing
Leitstand runs [H2 database](https://h2database.com/html/main.html "Visit H2 website") as in-memory database for transaction testing.
A transaction test validates that a _transactional_ service is able to modify the database as expected.
The service and all units forming the service need to be integrated in order to run the transaction test.
Hence transaction tests have rather an integration test nature than a unit test nature.

__Convention:__ _All transaction test classes are suffixed with `IT`._

All transaction tests of a module share the same in-memory database instance for performance reasons.
A transaction test needs to ensure that records written to the in-memory database or read from the in-memory database will not conflict with other transaction tests. 
A simple approach is to incorporate the transaction class name into the names of all entities that are subject of the transaction test. 
A more sophisticated approach is prepare the database _before_ the actual test starts and to _clear_ the database after the test was completed irrespectively of the test result. 

### Module Integration Test Base Class

Each Leitstand module needs an integration test base class that initializes the in-memory database.

The integeration test base class has three responsibilities:
1. Providing the name of the module JPA persistence unit.
2. Providing the properties to setup the JPA persistence unit including the database connection.
3. Initializing the in-memory database (e.g. load test data into the database).

The source listing below shows the integration test base class of the Leitstand Resource Inventory module.

```Java
/**
 * Inventory transaction tests base class.
 */
public class InventoryIT extends JpaIT {

  /** {@inheritDoc} */
  @Override
  protected Properties getConnectionProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(getSystemResourceAsStream("inventory-it.properties"));
    return properties;
  }
	
  /** {@inheritDoc} */
  @Override
  protected void initDatabase(DataSource ds) throws SQLException{
    try (Connection c = ds.getConnection()) {
	  // Creating empty schemas to enable JPA to create all tables. 
	  c.createStatement().execute("CREATE SCHEMA inventory;");
      c.createStatement().execute("CREATE SCHEMA leitstand;");
    } 
  }
	
  /** {@inheritDoc} */
  @Override
  protected String getPersistenceUnitName() {
    // Inventory module persistence unit name.
	return "inventory";
  }

}
```
The complete set of properties to initialize JPA (EclipseLink) is listed below.

```properties
javax.persistence.provider=org.eclipse.persistence.jpa.PersistenceProvider
javax.persistence.jdbc.driver=org.h2.Driver
# Set DB_CLOSE_DELAY=-1 to share the in-memory database among different JDBC connections.
javax.persistence.jdbc.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
javax.persistence.jdbc.user=sa
javax.persistence.jdbc.password=sa
# JTA is not supported in the transaction test environment.
javax.persistence.transactionType=RESOURCE_LOCAL
javax.persistence.jtaDataSource=
eclipselink.target-database=org.eclipse.persistence.platform.database.H2Platform
eclipselink.ddl-generation=create-tables
eclipselink.ddl-generation.output-mode=both
eclipselink.create-ddl-jdbc-file-name=createDDL_ddlGeneration.jdbc
eclipselink.logging.level=FINE
eclipselink.logging.level.sql=ALL
```

### Transaction Test

A transaction test typically consists of a series of transactions to modify and validate the database state.
The `JpaIT` class provides means to simplify transaction management and to access the JPA `EntityManager` and the Leitstand `DatabaseService`:
- `transaction(Transaction)` allows to declare a transaction as lambda expression
- `getEntityManager()` provides access to the JPA  `EntityManager`
- `getDatabaseService()` provides access to the  Leitstand `DatabaseService`.

The listing below shows an excerpt of the transaction test to maintain 
element environment.
The transactional test uses the JUnit lifecycle callbacks to initialize and clear the in-memory database:
- `@Before` (`initTestEnvironment`) creates an element including an element group an d element role. This is required because environments are stored per element.
- `@After` (`clearTestEnvironment`) removes all environments created by a transaction test.
 
`@Test` annotates the actual test cases.

```Java
package io.leitstand.inventory.model;

import static io.leitstand.inventory.model.Element.findElementById;
import static io.leitstand.inventory.model.ElementGroup.findElementGroupById;
import static io.leitstand.inventory.model.ElementRole.findRoleByName;
import static io.leitstand.inventory.model.Element_Environment.removeEnvironments;
import static io.leitstand.inventory.service.ElementGroupId.randomGroupId;
import static io.leitstand.inventory.service.ElementGroupName.groupName;
import static io.leitstand.inventory.service.ElementGroupType.groupType;
import static io.leitstand.inventory.service.ElementId.randomElementId;
import static io.leitstand.inventory.service.ElementName.elementName;
import static io.leitstand.inventory.service.ElementRoleName.elementRoleName;
import static io.leitstand.inventory.service.Environment.newEnvironment;
import static io.leitstand.inventory.service.EnvironmentId.randomEnvironmentId;
import static io.leitstand.inventory.service.EnvironmentName.environmentName;
import static io.leitstand.inventory.service.ReasonCode.IVT0300E_ELEMENT_NOT_FOUND;
import static io.leitstand.inventory.service.ReasonCode.IVT0390E_ELEMENT_ENVIRONMENT_NOT_FOUND;
import static io.leitstand.testing.ut.LeitstandCoreMatchers.reason;
import static javax.json.Json.createObjectBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ElementEnvironmentServiceIT extends InventoryIT {
	
  private static final ElementGroupId   GROUP_ID         = randomGroupId();
  private static final ElementGroupType GROUP_TYPE       = groupType("unittest");
  private static final ElementGroupName GROUP_NAME       = groupName(ElementEnvironmentServiceIT.class.getSimpleName());
  private static final ElementId        ELEMENT_ID       = randomElementId();
  private static final ElementName      ELEMENT_NAME     = elementName(ElementEnvironmentServiceIT.class.getSimpleName());
  private static final ElementRoleName  ELEMENT_ROLE     = elementRoleName(ElementEnvironmentServiceIT.class.getSimpleName());
  private static final EnvironmentId    ENVIRONMENT_ID   = randomEnvironmentId(); 
  private static final EnvironmentName  ENVIRONMENT_NAME = environmentName("environment");

  @Rule
  public ExpectedException exception = ExpectedException.none(); 
	
  private ElementEnvironmentService service;
  private Repository repository;
	
  @Before
  public void initTestEnvironment() {
    this.repository = new Repository(getEntityManager());
    ElementProvider elements = new ElementProvider(repository);
    ElementEnvironmentManager manager = new ElementEnvironmentManager(repository, 
                                                                      mock(Event.class), 
                                                                      mock(Messages.class));
    service = new DefaultElementEnvironmentService(elements, manager);
		
    // Create element role, element group and element.
    // Environments are maintained per element.
    transaction(()->{
      ElementRole role = repository.addIfAbsent(findRoleByName(ELEMENT_ROLE),
                                                () -> new ElementRole(ELEMENT_ROLE, Plane.DATA));	
			
      ElementGroup group = repository.addIfAbsent(findElementGroupById(GROUP_ID),
                                                  () -> new ElementGroup(GROUP_ID, GROUP_TYPE, GROUP_NAME));
			
      repository.addIfAbsent(findElementById(ELEMENT_ID),
                             () -> new Element(group, role, ELEMENT_ID, ELEMENT_NAME));
			
	});
		
  }
	
  @After
  public void clearTestEnvironment() {
    // Remove stored test environments. 
    transaction(()->{
      Element element = repository.execute(findElementById(ELEMENT_ID));
      repository.execute(removeEnvironments(element));
    });
  }
	
  @Test
  public void create_element_environment() {
    Environment env = newEnvironment()
                      .withEnvironmentId(ENVIRONMENT_ID)
                      .withEnvironmentName(ENVIRONMENT_NAME)
                      .withCategory("category")
                      .withDescription("description")
                      .withType("type")
                      .withVariables(createObjectBuilder().add("foo", "bar").build())
                      .build();
		
    transaction(()->{
      boolean created = service.storeElementEnvironment(ELEMENT_ID, env);
      assertTrue(created);
    });
		
    transaction(()->{
      Environment reloaded = service.getElementEnvironment(ELEMENT_ID, ENVIRONMENT_NAME).getEnvironment();
      assertEquals(env,reloaded);
    });
  }

  @Test
  public void update_element_environment() {

    transaction(()->{
      Environment env = newEnvironment()
                        .withEnvironmentId(ENVIRONMENT_ID)
                        .withEnvironmentName(ENVIRONMENT_NAME)
                        .withType("type")
                        .withVariables(createObjectBuilder().add("foo", "bar").build())
                        .build();
      boolean created = service.storeElementEnvironment(ELEMENT_ID, env);
      assertTrue(created);
    });
		
    Environment env = newEnvironment()
                      .withEnvironmentId(ENVIRONMENT_ID)
                      .withEnvironmentName(ENVIRONMENT_NAME)
                      .withCategory("category")
                      .withDescription("description")
                      .withType("type")
                      .withVariables(createObjectBuilder().add("foo", "bar").build())
                      .build();

     transaction(()->{
       boolean created = service.storeElementEnvironment(ELEMENT_ID, env);
       assertFalse(created);
     });
		
     transaction(()->{
       Environment reloaded = service.getElementEnvironment(ELEMENT_ID, ENVIRONMENT_NAME).getEnvironment();
       assertEquals(env,reloaded);
     });
   }
	
   @Test
   public void remove_element_environment() {
		
      transaction(()->{
        Environment env = newEnvironment()
                          .withEnvironmentId(ENVIRONMENT_ID)
                          .withEnvironmentName(ENVIRONMENT_NAME)
                          .withType("type")
                          .withVariables(createObjectBuilder().add("foo", "bar").build())
					  	.build();
        boolean created = service.storeElementEnvironment(ELEMENT_ID, env);
        assertTrue(created);
      });

      transaction(()->{
        service.removeElementEnvironment(ELEMENT_NAME, ENVIRONMENT_NAME);
      });
		
      transaction(()->{
        exception.expect(EntityNotFoundException.class);
        exception.expect(reason(IVT0390E_ELEMENT_ENVIRONMENT_NOT_FOUND));
        service.getElementEnvironment(ELEMENT_NAME, ENVIRONMENT_NAME);
      });
  }
}
```




