/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

/**
 * The <code>EntityNotFoundException</code> signals that a requested entity does
 * not exist.
 */
public class EntityNotFoundException extends LeitstandException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a <code>EntityNotFoundException</code>.
	 * @param reason - the reason code explaining which entity was not found
	 * @param key - key information about the requested entity
	 */
	public EntityNotFoundException(Reason reason, Object... key) {
		super(reason, key);
	}

}
