/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.template;

import static java.lang.String.format;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class Template<T> {

	private static final Logger LOG = Logger.getLogger(Template.class.getName());
	private static final String TEMPLATE_MODEL = "model";
	private static final String TEMPLATE_SOURCE = "template";
	
	private ScriptEngine mustache;
	private String templateModel;
	private String templateText;
	private TemplateProcessor<T> processor;
	
	Template(ScriptEngine mustache, 
			 String templateModel, 
			 String template, 
			 TemplateProcessor<T> processor){
		this.mustache = mustache;
		this.templateModel = templateModel;
		this.templateText = template;
		this.processor = processor;
	}
	
	Template(ScriptEngine mustache, 
			 URL templateModel, 
			 URL templateFile, 
			 TemplateProcessor<T> processor){
		this.mustache = mustache;
		this.templateText = load(templateFile);
		this.templateModel = load(templateModel);
		this.processor = processor;
	}
	
	public T apply(Object model) {
		mustache.put(TEMPLATE_MODEL, model);
		if(templateModel != null) {
			loadModel();
		}
		String outcome = callTemplate();
		return processor.parse(outcome);		
	}

	private void loadModel() {
		try {
			mustache.eval(templateModel);
		} catch(ScriptException e) {
			LOG.severe(format("Cannot process template model: %s ", 
							  e.getMessage()));
			LOG.log(FINE,
					e.getMessage(),
					e);
		}
	}
	
	private static String load(URL templateFile) {
		if(templateFile == null) {
			return null;
		}
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(templateFile.openStream()))) {
			StringBuilder source = new StringBuilder();
			String line = reader.readLine();
			while(line != null) {
				source.append(line)
				.append("\n");
				line = reader.readLine();
			}
			return source.toString();
		} catch (IOException e) {
			LOG.log(FINE, "Cannot load template "+templateFile+": "+e.getMessage());
			LOG.log(FINER, e.getMessage(), e);
			throw new UncheckedIOException(e);
		}
	}
	
	private String callTemplate()  {
		try{
			mustache.put(TEMPLATE_SOURCE,templateText);
			return (String) mustache.eval("Mustache.render(template,model)");
		} catch (Exception e) {
			LOG.log(FINE, "Execution of template "+templateText+" failed: "+e.getMessage());
			LOG.log(FINER, e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}
	
	
	
	
}
