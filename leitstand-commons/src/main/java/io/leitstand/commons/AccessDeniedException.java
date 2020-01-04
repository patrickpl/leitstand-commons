/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

/**
 * The <code>AccessDeniedException</code> signals that the authenticated user 
 * is not allowed to perform the requested operation.
 */
public class AccessDeniedException extends LeitstandException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a <code>AccessDeniedException</code>
	 * @param reason the reason why the access is denied
	 * @param cause the root cause
	 */
	public AccessDeniedException(Exception cause, Reason reason, Object... args) {
		super(cause, reason, args);
	}
	
	public AccessDeniedException(Reason reason, Object... args) {
		super(reason,args);
	}


}
