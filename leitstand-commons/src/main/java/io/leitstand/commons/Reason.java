/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons;

import java.io.Serializable;

import io.leitstand.commons.messages.Message;
import io.leitstand.commons.messages.Message.Severity;

/**
 * The reason code forms a unique identifier for all issues detected at runtime.
 * <p>
 * A reason code must obey the following naming conventions:
 * <ul>
 *  <li>The reason code is capitalized</li>
 *  <li>The first eight characters form the actual reason code being unique across all Leitstand modules</li>
 * 	<li>The first three characters identify the module reporting an issue</li>
 *  <li>The next four characters identify the reason code within the module unambiguously</li>
 *  <li>The next character expresses the severity. <code>E</code> for error, <code>W</code> for warning and <code>I</code> for information</li>
 *  <li>The next characters are an underscore (<code>_</code>) as delimiter followed by the reason code name.
 * </ul>
 * Each Leitstand module contains an enumeration of defined reason codes.
 */
public interface Reason extends Serializable{

	/**
	 * Returns a formatted text message.
	 * @param keys - the arguments for all parameters in the message format.
	 * @return the formatted text message.
	 */
	String getMessage(Object... keys);
	
	/**
	 * Returns a three character code to identify the EMS module that has created a message.
	 * See {@link #getReasonCode()} for further information.
	 * @return the EMS module that has created a message.
	 */
	default String getModule() {
		String reason = getReasonCode();
		assert reason != null && reason.length() == 8;
		if(reason.length() < 3) {
			return "UNK";
		}
		return getReasonCode().substring(0,3);
	}

	/**
	 * Returns the severity of this reason code.
	 * By default, the severity is derived from the last character of the 8 character reason code.
	 * See {@link #getReasonCode()} for further information.
	 * @return the severity of the message.
	 */
	default Severity getSeverity()  {
		String reason = getReasonCode();
		assert reason != null && reason.length() == 8;
		switch(getReasonCode().charAt(7)) {
			case 'I': return Message.Severity.INFO;
			case 'W': return Message.Severity.WARNING;
			default: return Severity.ERROR;
		}
	}

	/**
	 * Returns an 8 character string representation of this reason code.
	 * The first three characters identify the module and the last character
	 * defines the severity. Supported severity values are 
	 * <ul>
	 * 	<li><code>E</code> for {@link Severity#ERROR}</li>
	 * 	<li><code>W</code> for {@link Severity#WARNING}</li>
	 * 	<li><code>I</code> for {@link Severity#INFO}</li>
	 * </ul>
	 * @return the string representation of this reason code.
	 */
	default String getReasonCode() {
		return toString().substring(0, 8);
	}
	
}
