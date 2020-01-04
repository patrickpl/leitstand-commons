/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.template;

import static io.leitstand.commons.template.TemplateProcessor.plain;
import static io.leitstand.commons.template.TemplateProcessor.yaml;
import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Properties;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.junit.Test;

public class TemplateProcessorTest {
	
	public static final class DataBean {
		
		private String foo;
		private String bar;
		@JsonbProperty("camelCase")
		private String camelCase;
		private String snake_case;
		private String snakeCamelCase;
		
		public String getFoo() {
			return foo;
		}
		
		public String getBar() {
			return bar;
		}
		
		public String getCamelCase() {
			return camelCase;
		}

		public String getSnakeCamelCase() {
			return snakeCamelCase;
		}
		
		@JsonbTransient
		public String getSnakeCase() {
			return snake_case;
		}

	}

	private static String load(String file) {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getSystemResourceAsStream(file)))){
			StringWriter buffer = new StringWriter();
			String line = reader.readLine();
			while(line != null) {
				buffer.write(line);
				buffer.write("\n");
				line = reader.readLine();
			}
			return buffer.toString();
		} catch(IOException e) {
			fail(e.getMessage());
			throw new UncheckedIOException(e);
		}
	}
	
	@Test
	public void can_read_yaml_typed() {
		DataBean databean = yaml(DataBean.class).parse(load("databean.yaml"));
		assertEquals("foo-value",databean.getFoo());
		assertEquals("bar-value",databean.getBar());
		assertEquals("camel case value",databean.getCamelCase());
		assertEquals("snake case value",databean.getSnakeCase());
		assertEquals("snake camel case value",databean.getSnakeCamelCase());	}
	
	@Test
	public void can_read_yaml_map() {
		Map<String,Object> databean = yaml().parse(load("databean.yaml"));
		assertEquals("foo-value",databean.get("foo"));
		assertEquals("bar-value",databean.get("bar"));
		assertEquals("camel case value",databean.get("camelCase"));
		assertEquals("snake case value",databean.get("snake_case"));
		assertEquals("snake camel case value",databean.get("snakeCamelCase"));
	}

	@Test
	public void can_read_json_typed() {
		DataBean databean = TemplateProcessor.json(DataBean.class).parse(load("databean.json"));
		assertEquals("foo-value",databean.getFoo());
		assertEquals("bar-value",databean.getBar());
		assertEquals("camel case value",databean.getCamelCase());
		assertEquals("snake case value",databean.getSnakeCase());
		assertEquals("snake camel case value",databean.getSnakeCamelCase());

	}
	
	@Test
	public void can_read_json_map() {
		Map<String,Object> databean = yaml().parse(load("databean.json"));
		System.out.println(databean);
		assertEquals("foo-value",databean.get("foo"));
		assertEquals("bar-value",databean.get("bar"));
		assertEquals("camel case value",databean.get("camelCase"));
		assertEquals("snake case value",databean.get("snake_case"));
		assertEquals("snake camel case value",databean.get("snake_camel_case"));
	}
	
	@Test
	public void can_read_properties() {
		Properties properties = TemplateProcessor.properties().parse(load("dummy.properties"));
		assertEquals("bar",properties.get("foo"));
	}
	
	@Test
	public void can_read_plain() {
		assertEquals("plain",plain().parse("plain"));
	}
	
}
