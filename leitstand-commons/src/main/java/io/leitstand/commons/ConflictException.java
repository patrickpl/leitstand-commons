/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

/**
 * The <code>ConflictException</code> is thrown whenever a conflict has been detected during request processing.
 * <p>
 * Example causes of conflicts are:
 * <ul>
 * 	<li>Attempts to modify the same resource concurrently</li>
 *  <li>Attempts to remove a resource which is required to be available for other active resources.</li>
 * </ul>
 */
public class ConflictException extends LeitstandException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a <code>ConflictException</code>.
	 * @param reason the reason of the conflict.
	 * @param args message template parameters.
	 */
	public ConflictException(Reason reason, Object... args){
		super(reason,args);
	}
	
	
}
