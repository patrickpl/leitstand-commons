/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import io.leitstand.commons.Reason;

public enum ReasonCode implements Reason{ 

	VAL0001E_VALUE_REQUIRED,
	VAL0002E_INVALID_VALUE,
	VAL0003E_IMMUTABLE_ATTRIBUTE,
	@Deprecated
	VAL0003E_IMMUTABLE_VALUE;

	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("ValidationMessages");

	
	/**
	 * {@inheritDoc}
	 */
	public String getMessage(Object... args){
		try{
			String pattern = MESSAGES.getString(name());
			return MessageFormat.format(pattern, args);
		} catch(Exception e){
			return name() + Arrays.asList(args);
		}
	}
	
}