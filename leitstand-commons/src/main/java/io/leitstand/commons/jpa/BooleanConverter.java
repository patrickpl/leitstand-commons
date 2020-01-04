/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jpa;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply=true)
public class BooleanConverter implements AttributeConverter<Boolean, String>{

	@Override
	public String convertToDatabaseColumn(Boolean value) {
		if(value == null){
			return "N";
		}
		if(value.booleanValue()){
			return "Y";
		}
		return "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String value) {
		return parseBoolean(value);
	}

	public static Boolean parseBoolean(String dbValue) {
		if(dbValue == null){
			return FALSE;
		}
		if("Y".equalsIgnoreCase(dbValue)){
			return TRUE;
		}
		return FALSE;
	}
	
}
