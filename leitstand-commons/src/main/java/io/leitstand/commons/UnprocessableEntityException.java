/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

/**
 * A <code>UnprocessableEntityException</code> is thrown when a request
 * entity was syntactically correct but the server was not able to complete the operation
 * due to a semantical errors.
 */
public class UnprocessableEntityException extends LeitstandException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Create a <code>UnprocessableEntityException</code>.
	 * @param reason - the reason explains why the specified entity was not processable
	 * @param context - provides context information such as the invalid values
	 */
	public UnprocessableEntityException(Reason reason, Object... context){
		super(reason,context);
	}
	
}
