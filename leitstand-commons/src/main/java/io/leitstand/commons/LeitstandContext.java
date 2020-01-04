/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

import static java.lang.String.format;
import static java.util.logging.Level.WARNING;

import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LeitstandContext implements ServletContextListener {

	private static final Logger LOG = Logger.getLogger(LeitstandContext.class.getName());
	
	@Inject
	private Instance<StartupListener> startupListeners;
	
	@Inject
	private Instance<ShutdownListener> shutdownListeners;
	
	public LeitstandContext() {
		// Tool constructor
	}
	
	LeitstandContext(Instance<StartupListener> startupListeners, 
					 Instance<ShutdownListener> shutdownListeners) {
		this.startupListeners = startupListeners;
		this.shutdownListeners = shutdownListeners;
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		for(StartupListener listener : startupListeners) {
			try {
				listener.onStartup();
				LOG.info(() -> format("Successfully called %s startup listener.",listener.getClass().getSimpleName()));
			} catch (Exception e) {
				LOG.log(WARNING, 
						format("Startup listener invocation failed: %s",e.getMessage()),
						e);
			}
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		for(ShutdownListener listener : shutdownListeners) {
			try {
				listener.onShutdown();
				LOG.info(() -> format("Successfully called %s shutdown listener.",listener.getClass().getSimpleName()));
			} catch (Exception e) {
				LOG.log(WARNING, 
						format("Shutdown listener invocation failed: %s",e.getMessage()),
						e);
			}
		}
	}
}
