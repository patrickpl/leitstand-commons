/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;



/**
 * The <code>LeistandException</code> is the base class for all Leitstand exceptions.
 * <p>
 * Every <code>LeitstandException</code> conveys the {@link Reason} why the exception was fired.  
 * </p>
 * @see Reason
 */
public abstract class LeitstandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Reason reason;
	
	/**
	 * Create a <code>LeitstandException</code>. 
	 * @param reason the reason for this fault
	 * @param arguments arguments to form the message text
	 */
	protected LeitstandException(Reason reason, Object... arguments){
		super(reason.getMessage(arguments));
		this.reason = reason;
	}
	
	
	/**
	 * Create a <code>LeitstandException</code>. 
	 * @param cause the cause of this exception
	 * @param reason the reason for this fault
 	 * @param arguments arguments to form the message text
	 */
	protected LeitstandException(Exception cause, Reason reason, Object... arguments){
		super(cause);
		this.reason = reason;
	}

	/**
	 * Returns the reason of the fault reported by this exception.
	 * @return the reason code of the reported error.
	 */
	public Reason getReason() {
		return reason;
	}
	
}