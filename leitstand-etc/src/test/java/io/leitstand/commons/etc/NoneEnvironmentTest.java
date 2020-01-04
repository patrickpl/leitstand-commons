/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.FileProcessor.yaml;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the behavior of {@link Environment} if no <code>ems.properties</code> file exists.
 */
public class NoneEnvironmentTest {

	private Environment env = new Environment();
	
	@Before
	public void initEnvironment() {
		// Init env as CDI container would do it
		env.readEnvironmentProperties();
	}
	

	@Test
	public void return_empty_map_if_configuration_file_does_not_exist() {
		assertTrue(env.loadFile("non-existing", yaml()).isEmpty());
	}

	@Test
	public void return_null_if_configuration_file_does_not_exist() {
		assertNull(env.loadFile("non-existing", yaml(DummyConfig.class)));
	}

	@Test
	public void return_default_config_if_configuration_file_does_not_exist() {
		DummyConfig cfg = env.loadConfig("non-existing", yaml(DummyConfig.class),DummyConfig::defaultConfig);
		assertNotNull(cfg);
		assertEquals(DummyConfig.DEFAULT,cfg);
	}
	
}
