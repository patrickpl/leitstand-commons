/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.template;

import static io.leitstand.commons.template.TemplateProcessor.plain;
import static io.leitstand.commons.template.TemplateProcessor.yaml;
import static java.lang.ClassLoader.getSystemResource;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TemplateTest {

	
	private TemplateService templates;
	
	
	@Before
	public void initService() {
		templates = new TemplateService();
		templates.initTemplateFactory();
	}
	
	@Test
	public void can_execute_template_without_model() {
		String template = "Hello, {{name}}!";
		Map<String,String> env = new HashMap<>();
		env.put("name", "Jane");
		Template<String> compiled = templates.compileTemplate(template, plain());
		assertEquals("Hello, Jane!",compiled.apply(env));
	}
	
	
	@Test
	public void can_execute_template_with_model() {
		String template = "Hello, {{function}}!";
		String model    = "model.function = function() {return model.name}";
		Map<String,String> env = new HashMap<>();
		env.put("name", "Jane");
		Template<String> compiled = templates.compileTemplate(model,template, plain());
		assertEquals("Hello, Jane!",compiled.apply(env));
	}
	
	@Test
	public void can_load_template_with_model() throws Exception {
		URL template = 	getSystemResource("template.yaml");
		Map<String,String> env = new HashMap<>();
		env.put("name", "Jane");
		Template<Map<String,Object>> compiled = templates.loadTemplate(template, yaml());
		assertEquals("Hello, Jane!",compiled.apply(env).get("message"));
	}
	
	
	@Test
	public void can_load_template_and_model() {
		URL template = 	getSystemResource("template.yaml");
		URL model 	 = 	getSystemResource("model.js");
		Map<String,String> env = new HashMap<>();
		env.put("name", "Jane");
		Template<Map<String,Object>> compiled = templates.loadTemplate(model,template, yaml());
		assertEquals("Hello, Jane!",compiled.apply(env).get("message"));
	}
	
}
