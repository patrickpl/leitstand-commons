/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.jsonb.IsoDateAdapter.isoDateFormat;
import static io.leitstand.commons.jsonb.IsoDateAdapter.parseIsoDate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class IsoDateParamConverterProvider implements ParamConverterProvider{

	private static class IsoDateParamConverter implements ParamConverter<Date>{

		@Override
		public Date fromString(String value) {
			return parseIsoDate(value);
		}

		@Override
		public String toString(Date value) {
			return isoDateFormat(value);
		}
				
	}
	
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if(Date.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>) new IsoDateParamConverter();
		}	
		return null;
	}

}
