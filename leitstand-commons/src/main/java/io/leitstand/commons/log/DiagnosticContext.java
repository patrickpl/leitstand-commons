/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.log;

import static io.leitstand.commons.model.StringUtil.isEmptyString;
import static java.lang.ThreadLocal.withInitial;

/**
 * <code>DiagnosticContext</code> provides means to define information that shall be logged with every log message.
 * <p>
 * Use {@link #push(String)} to add new information to the diagnostic context and {@link #clear()} to clean the diagnostic context.
 * The information is assigned to the current thread. Hence it is utmost important to clear the diagnostic context before the thread 
 * gets returned to the application server thread pool. Moreover, diagonstic context is not shared to threads forked by the current thread.
 * 
 * @see PatternFormatter 
 */
public final class DiagnosticContext {

	private static ThreadLocal<String> threadContext = withInitial(()->"");
	
	/**
	 * Appends the given data to the current diagnostic context using a space as delimiter.
	 * @param info - the info to be pushed on the diagonstic context
	 */
	public static void push(String info) {
		if(isEmptyString(info)) {
			// Ignore all empty context informations.
			return;
		}
		String s = threadContext.get();
		if(!s.isEmpty()) {
			s = s.concat(" ");
		}
		s = s.concat(info);
		threadContext.set(s);
	}

	/**
	 * Returns the diagnostic context string.
	 * @return the diagnostic context string.
	 */
	public static String getContext() {
		return threadContext.get() ;
	}
	
	/**
	 * Empties the diagnostic context.
	 */
	public static void clear() {
		threadContext.remove();
	}
	
	private DiagnosticContext() {
		// No instances allowed.
	}
	
}
