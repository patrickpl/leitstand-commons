/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.enterprise.inject.Instance;
import javax.servlet.ServletContextEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LeitstandContextTest {

	private LeitstandContext context;
	private Instance<StartupListener> startupListeners;
	private Instance<ShutdownListener> shutdownListeners;
	
	@Before
	public void initLeitstandContext() {
		this.startupListeners  = mock(Instance.class);
		this.shutdownListeners = mock(Instance.class);
		this.context = new LeitstandContext(startupListeners,shutdownListeners);
		
	}
	
	@Test
	public void invoke_all_startup_listeners() {
		StartupListener a = mock(StartupListener.class);
		StartupListener b = mock(StartupListener.class);
		StartupListener c = mock(StartupListener.class);
		
		doThrow(new RuntimeException()).when(b).onStartup();
		doReturn(asList(a,b,c).iterator()).when(startupListeners).iterator();
		
		context.contextInitialized(mock(ServletContextEvent.class));
		
		InOrder order = inOrder(a,b,c);
		order.verify(a).onStartup();
		order.verify(b).onStartup();
		order.verify(c).onStartup();
		verifyZeroInteractions(shutdownListeners);
		
	}
	
	
	@Test
	public void invoke_all_shutdown_listeners() {
		ShutdownListener a = mock(ShutdownListener.class);
		ShutdownListener b = mock(ShutdownListener.class);
		ShutdownListener c = mock(ShutdownListener.class);
		
		doThrow(new RuntimeException()).when(b).onShutdown();
		doReturn(asList(a,b,c).iterator()).when(shutdownListeners).iterator();
		
		context.contextDestroyed(mock(ServletContextEvent.class));
		
		InOrder order = inOrder(a,b,c);
		order.verify(a).onShutdown();
		order.verify(b).onShutdown();
		order.verify(c).onShutdown();
		verifyZeroInteractions(startupListeners);

	}	
}
