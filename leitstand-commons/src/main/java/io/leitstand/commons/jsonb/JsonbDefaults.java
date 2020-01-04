/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jsonb;

import static javax.json.bind.JsonbBuilder.newBuilder;
import static javax.json.bind.config.PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import io.leitstand.commons.model.CompositeValue;

/**
 * Sets the default <code>PropertyNamingStrategy</code>, <code>PropertyVisibilityStrategy</code> and default date format for all REST services.
 * <p>
 * By default, all REST services follow the snake case property naming scheme (e.g. element_id). 
 * The {@link FieldAccessVisibilityStrategy} enables support for private fields without setter methods, as they exist in all immutable composite values.
 * All properties are serialized in lexicographical order.
 * Finally, the default date format is set to ISO date format (<code>yyyy-MM-dd'T'HH:mm.ss.SSSXXX</code>) for all REST services.
 * </p>
 * @see PropertyNamingStrategy#LOWER_CASE_WITH_UNDERSCORES
 * @see PropertyOrderStrategy#LEXICOGRAPHICAL
 * @see FieldAccessVisibilityStrategy
 * @see CompositeValue
 */
@Provider
public class JsonbDefaults implements ContextResolver<Jsonb> {
	
	//Jsonb is thread-safe
	private static final Jsonb JSONB;

	static {
        JsonbConfig config = new JsonbConfig()
				 			 .withPropertyVisibilityStrategy(new FieldAccessVisibilityStrategy())
				 			 .withPropertyNamingStrategy(LOWER_CASE_WITH_UNDERSCORES)
				 			 .withEncoding("utf8")
				 			 .withDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", null);
        JSONB = newBuilder()
        		.withConfig(config)
        		.build();
	}
	

	public static Jsonb jsonb() {
		return new JsonbDefaults().getContext(null);
	}
	
	/**
	 * Creates a <code>Jsonb</code> configuration in order to support field access and to switch to snake case property naming scheme.
	 * @see PropertyNamingStrategy#LOWER_CASE_WITH_UNDERSCORES
	 * @see FieldAccessVisibilityStrategy
	 * @return <code>Jsonb</code> context with mentioned strategies enabled.
	 */
	@Override
	public Jsonb getContext(Class<?> type) {
		return JSONB;
	}

}
