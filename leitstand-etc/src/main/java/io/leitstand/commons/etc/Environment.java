/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static io.leitstand.commons.etc.FileProcessor.yaml;
import static io.leitstand.commons.model.StringUtil.isNonEmptyString;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.FINE;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * The <code>Environment</code> provides access to the environment properties stored in the <code>/etc/rbms/environment.yaml</code> file
 * as well as access to all other EMS configuration files. 
 * All configuration files shall be located in the <code>/etc/rbms</code> directory or sub directories.
 * <p>
 * The <code>Environment</code> is an <code>{@literal @ApplicationScoped}</code> bean and can be obtained via CDI.
 */
@ApplicationScoped
public class Environment {

	private static final Logger LOG = Logger.getLogger(Environment.class.getName());

	public static Environment emptyEnvironment() {
		return new Environment();
	}
	
	public static <T> String getProperty(T config, Function<T,String> getter) {
		return getProperty(config,getter,null);
	}
	
	public static <T> String getProperty(T config, Function<T,String> getter, String defaultValue){
		if(config == null) {
			return defaultValue;
		}
		String value = getter.apply(config);
		if(value == null) {
			return defaultValue;
		}
		return null;
	}

	public static String getSystemProperty(String name) {
		return getSystemProperty(name,null);
	}
	
	public static String getSystemProperty(String name, String defaultValue) {
		String value =  System.getProperty(name);
		if(isNonEmptyString(value)) {
			return value;
		}
		value = System.getenv(name);
		if(isNonEmptyString(value)) {
			return value;
		}
		return defaultValue;
	}
	
	private File baseDir;
	private LeitstandSettings settings;
	
	@PostConstruct
	protected void readEnvironmentProperties() {
		baseDir = new File(getSystemProperty("leitstand.etc.root","/etc/leitstand"));
		settings = loadConfig("leitstand.settings", 
							  yaml(LeitstandSettings.class),
							  LeitstandSettings::new);
	}
	
	public LeitstandSettings getSettings() {
		return settings;
	}
	
	public <T> T loadConfig(String fileName,
						  FileProcessor<T> processor) {
		return loadConfig(fileName,processor);
	}
	
	public <T> T loadConfig(String fileName,
							FileProcessor<T> processor,
							Supplier<T> defaultFactory) {
		
		File config = new File(baseDir,fileName);
		if(!config.exists()) {
			if(defaultFactory != null) {
				String msg = format("File %s does not exist. Proceed with default configuration.",
	    			   			   	config.getAbsolutePath());
				LOG.warning(msg);
				return defaultFactory.get();
			}
			
			String msg = format("File %s does not exist. No default configuration available.",
								config.getAbsolutePath());
			LOG.severe(msg);
			return null;
		}
		
		try(FileReader reader = new FileReader(config)){
			return processor.process(reader);
		} catch (IOException e) {
			LOG.severe(format("An IO error occured while attempting to read %s: %s",
							   config.getAbsolutePath(),
							   e));
			LOG.log(FINE, e.getMessage(), e);
			if(defaultFactory != null) {
				LOG.info(String.format("Use %s default config",fileName));
				return defaultFactory.get();
			}	
			return null;
		}
		
	}
	
	/**
	 * Parses a configuration file and returns an object structure created from the configuration file.
	 * If the specified configuration file does not exist a default configuration file shipped with the EMS binaries can be used.
	 * All default files are typically located within the <code>/META-INF</code> directory or sub directories.
	 * However, the environment basically can access all files that are accessible by the thread's context classloader.
	 * @param file - the file to load
	 * @param processor - the processor to parse the configuration file.
	 * @return the object structure created from the configuration file.
	 */
	public <T> T loadFile(String file, 
					  	  FileProcessor<T> processor){
	
		URL url = openFileUrl(file);
		
		if(url == null){
			String msg = format("File %s not found!",
					  		    file);
			LOG.severe(msg);
		}
		
		try {
			return processor.process(url);
		} catch(IOException e) {
			String msg = format("An I/O error occured while reading %s: %s",
								url,
								e.getMessage());
			LOG.log(FINE,msg,e);
			throw new UncheckedIOException(e);
		}
	}

	private URL openFileUrl( String file) {
		
		File config = new File(file);
		if(!config.isAbsolute() && !config.exists()) {
			config = new File(baseDir,file);
		}
		
		URL defaultUrl = currentThread()
						 .getContextClassLoader()
						 .getResource(file);
		
		
		if(!config.exists() || !config.canRead()) {
			String msg = format("Cannot read from file %s. Proceed with default file (%s) shipped with RBMS binaries.",
					   			config,
					   			defaultUrl);
			LOG.warning(msg);
			return defaultUrl;
		}
		
		try {
			return config.toURI().toURL();
		} catch (MalformedURLException e) {
			String msg = format("File %s cannot be accessed due to malformed URL: %s. Proceed with default file (%s) shipped with RBMS binaries.",
					   			file,
					   			e.getMessage(),
					   			defaultUrl);
			LOG.warning(msg);
			LOG.log(FINE, e.getMessage(), e);
			return defaultUrl;
		}
	}

	public URI getLeitstandApiEndpoint() {
		return settings.getLeitstandApi().getEndpoint();
	}

	
}
