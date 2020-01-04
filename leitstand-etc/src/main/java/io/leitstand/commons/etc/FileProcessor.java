/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.etc;

import static java.util.Collections.emptyMap;
import static org.yaml.snakeyaml.introspector.BeanAccess.FIELD;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;

/**
 * Reads a file from a specified URL and creates an object structure from the file content.
 *
 * @param <T> - the root type of the created object structure.
 */
@FunctionalInterface
public interface FileProcessor<T> {
	
	/**
	 * Creates a <code>FileProcessor</code> to parse a <code>properties</code> file.
	 * Returns empty <code>java.uti.Properties</code> if the file does not exist.
	 * @return a <code>FileProcessor</code> to parse a <code>properties</code> file.
	 */
	static FileProcessor<Properties> properties() {
		return reader -> {
			Properties properties = new Properties();
			if(reader != null) {
				properties.load(reader);
			}
			return properties;
		};
	}

	/**
	 * Creates a <code>FileProcessor</code> to parse a <code>YAML</code> file and to 
	 * return the root object of the <code>YAML</code> file as <code>java.util.Map</code>.
	 * Returns an empty map, if the file URL is <code>null</code>
	 * @return a file processor to read a <code>YAML</code> file.
	 */
	static  FileProcessor<Map<String,Object>> yaml() {
		return reader -> {
			if(reader == null) {
				return emptyMap();
			}
			Yaml yaml = new Yaml();
			yaml.setBeanAccess(FIELD);
			return  yaml.load(reader);
		};
	}
	
	/**
	 * Creates a <code>FileProcessor</code> to parse a <code>YAML</code> file and to 
	 * return the root object of the <code>YAML</code> file as instance of the specified
	 * object type.
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>YAML</code> file root object
	 * @return a file processor to read a <code>YAML</code> file.
	 */
	static <T> FileProcessor<T> yaml(Class<T> type) {
		return reader -> {
			if(reader == null) {
				return null;
			}
			Yaml yaml = new Yaml();
			yaml.setBeanAccess(FIELD);
			try{
				return (T)  yaml.loadAs(reader,type);
			} catch (ConstructorException e) {
				throw new IOException(e);
			}
		};
	}
	
	/**
	 * Reads a configuration file from the specified URL and 
	 * creates a typed representation of the file content.
	 * The processor defines how <code>null</code> values are processed.
	 * The processor can either throw an exception, return <code>null</code> or an empty data structure.
	 * @param file - the location of the configuration file
	 * @return the root object of the created object structure.
	 * @throws IOException if an error occurred while reading the file
	 */
	default T process(URL file) throws IOException{
		if(file == null) {
			return process((Reader)null);
		}
		return process(new InputStreamReader(file.openStream(),"UTF-8"));
	}
	
	
	/**
	 * Reads a configuration from the specified reader and 
	 * creates a typed representation of the file content.
	 * The processor defines how <code>null</code> values are processed.
	 * The processor can either throw an exception, return <code>null</code> or an empty data structure.
	 * @param reader - the location of the configuration reader
	 * @return the root object of the created object structure.
	 * @throws IOException if an error occurred while reading the file
	 */
	T process(Reader reader) throws IOException;


}
