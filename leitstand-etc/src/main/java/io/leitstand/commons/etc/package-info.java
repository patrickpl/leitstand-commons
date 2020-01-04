/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
/**
 * Provides access to the EMS configuration located in the <code>/etc/rbms</code> directory.
 * <p>
 * The {@link Environment} is a CDI managed <code>{@literal @ApplicationScoped}</code> bean that provides access to 
 * the EMS settings stored in the <code>/etc/rbms/rbms.properties</code> file. 
 * Additionally, the environment provides access to configuration
 * files and provides convenience methods to load and process configuration file. The <code>Environment</code>
 * expects a {@link FileProcessor} to process a configuration file. The <code>Environment</code> passes the file URL
 * to the specified file processor. 
 * The processor is responsible to parse the configuration file and to convert the file in a proper object model.
 * Furthermore, the processor must handle all errors that occurred.
 * </p>
 * <p>
 * The <code>FileProcessor</code> provides default processors to read YAML files.
 * </p>
 */
package io.leitstand.commons.etc;