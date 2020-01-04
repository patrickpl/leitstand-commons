/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.messages;

import io.leitstand.commons.Reason;

public final class MessageFactory {

	public static Message createMessage(Reason key, 
									    Object... args){
		return createMessage(key,
							 null,
							 args);
	}

	public static Message createMessage(Reason key,
										String property,
										Object... args){
		Message.Severity severity = key.getSeverity();
		return new Message(severity,
						   key.getReasonCode(),
						   property,
						   key.getMessage(args));
	}

	private MessageFactory() {
		// No instances allowed.
	}
	
	
}
