/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static java.lang.String.format;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.leitstand.commons.jsonb.IsoDateAdapter;
import io.leitstand.commons.jsonb.JsonbDefaults;
import io.leitstand.commons.jsonb.MessagesMessageBodyWriter;

/**
 * Provider of all Leitstand API resources available in the current Leitstand installation.
 * <p>
 * The EMS can be assembled to a single application, which is typically the case for all lab environments
 * but can be deployed in a distributed fashion which makes sense in a production environment, as different
 * parts of the EMS can be scaled independently from each others and the fault isolation from different modules
 * gets improved as well.
 * </p>
 * CDI is leveraged to discover all available {@link ApiResourceProvider} instances and obtain the available resources.
 */
@ApplicationPath("/api/v1")
@ApplicationScoped
public class ApiResources extends Application {

	private static final Logger LOG = Logger.getLogger(ApiResources.class.getName());
	
	@Inject
	private Instance<ApiResourceProvider> modules;
	
	private Set<Class<?>> resources;
	
	@PostConstruct
	protected void discoverResources() {
		resources = new LinkedHashSet<>();
		for(ApiResourceProvider module : modules) {
			Set<Class<?>> moduleResources = module.getResources();
			if(resources.addAll(moduleResources)) {
				LOG.info(() -> format("Registered %2d resources from %s", 
									  moduleResources.size(), 
									  module.getClass().getSimpleName()));
				moduleResources.forEach(resource -> LOG.fine(format("Register %s for %s", 
																	resource, 
																	module.getClass().getSimpleName()) ));
			}
		}
		resources.add(AccessDeniedExceptionMapper.class);
		resources.add(PersistenceExceptionMapper.class);
		resources.add(ConflictExceptionMapper.class);
		resources.add(UniqueKeyConstraintViolationExceptionMapper.class);
		resources.add(EntityNotFoundExceptionMapper.class);
		resources.add(OptimisticLockExceptionMapper.class);
		resources.add(UnprocessableEntityExceptionMapper.class);
		resources.add(ValidationExceptionMapper.class);
		resources.add(IsoDateAdapter.class);
		resources.add(MessagesMessageBodyWriter.class);
		resources.add(JsonbDefaults.class);
		resources.add(IsoDateParamConverterProvider.class);
	}
	
	
	/**
	 * Returns all API resources available in this Leitstand installation.
	 * @return the available API resources.
	 */
	@Override
	public Set<Class<?>> getClasses(){
		return resources;
	}
	
}