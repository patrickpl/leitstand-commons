/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.template;

import static io.leitstand.commons.jpa.SerializableJsonObjectConverter.parseJson;
import static io.leitstand.commons.json.MapMarshaller.marshal;
import static io.leitstand.commons.jsonb.JsonbDefaults.jsonb;
import static org.yaml.snakeyaml.introspector.BeanAccess.FIELD;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.yaml.snakeyaml.Yaml;

/**
 * A post processor for templates to convert the string output of a template into an object structure.
 */
@FunctionalInterface
public interface TemplateProcessor<T> {

	static TemplateProcessor<Map<String,Object>> jsonAsMap(){
		return yaml();
	}
	
	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>YAML</code> data and to 
	 * return the root object of the <code>YAML</code> file as <code>java.util.Map</code>.
	 * Returns an empty map, if the file URL is <code>null</code>
	 * @return a file processor to read a <code>YAML</code> file.
	 */
	static  TemplateProcessor<Map<String,Object>> yaml() {
		return text -> {
			Yaml yaml = new Yaml();
			yaml.setBeanAccess(FIELD);
			return  yaml.load(text);
		};
	}
	
	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>YAML</code> data and to 
	 * return the root object of the <code>YAML</code> file as instance of the specified
	 * object type.
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>YAML</code> file root object
	 * @return a file processor to read a <code>YAML</code> file.
	 */
	static <T> TemplateProcessor<T> yaml(Class<T> type) {
		return text -> {
			Yaml yaml = new Yaml();
			yaml.setBeanAccess(FIELD);
			return (T)  yaml.loadAs(text,type);
		};
	}	
	
	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>JSON</code> data and to 
	 * return the root object as instance of the specified root data type.
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>JSON</code> file root object
	 * @return a file processor to read a <code>JSON</code> file.
	 */
	static <T> TemplateProcessor<T> json(Class<T> type) {
		return json -> jsonb().fromJson(json, type);
	}	
	
	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>JSON</code> data and to 
	 * return the root object of the <code>JSON</code> data as <code>JsonObject</code>
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>JSON</code> file root object
	 * @return a file processor to read a <code>JSON</code> file.
	 */
	static TemplateProcessor<JsonObject> json() {
		return text ->  parseJson(text);
	}	


	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>YAML</code> data and to 
	 * return the root object of the <code>JSON</code> file as instance of the specified
	 * object type.
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>JSON</code> file root object
	 * @return a file processor to read a <code>JSON</code> file.
	 */
	static TemplateProcessor<JsonObject> yamlToJson(){
		return text -> marshal(yaml().parse(text)).toJson();
	}
	
	/**
	 * Creates a <code>TemplateProcessor</code> to parse <code>PROPERTIES</code> data and to 
	 * return the root object of the <code>PROPERTIES</code> file as instance of the specified
	 * object type.
	 * Returns <code>null</code> if the specified file URL is <code>null</code>
	 * @param type - the type of the <code>PROPERTIES</code> file root object
	 * @return a file processor to read a <code>PROPERTIES</code> file.
	 */
	static TemplateProcessor<Properties> properties() {
		return text -> {
			try(StringReader reader = new StringReader(text)){
				Properties properties = new Properties();
				properties.load(reader);
				return properties;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}	
	
	
	/**
	 * Returns the outcome of the template as is.
	 * @return the template outcome
	 */
	static TemplateProcessor<String> plain(){
		return text -> text;
	}
	
	/**
	 * Processes the output of a template.
	 * @param text - the text to be processed
	 * @return the processed text.
	 */
	T parse(String text);	

}
