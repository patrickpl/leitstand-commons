package io.leitstand.commons.jsonb;

import static io.leitstand.commons.jsonb.JsonbDefaults.jsonb;
import static io.leitstand.commons.model.StringUtil.isEmptyString;

import java.io.StringReader;

public final class JsonProcessor {
	
	public static <M> M unmarshal(Class<M> eventType, String json) {
		if(isEmptyString(json)) {
			return null;
		}
		try(StringReader reader = new StringReader(json)){
			return jsonb().fromJson(reader, eventType);
		}
	}

	public static String marshal(Object o) {
		if(o == null) {
			return null;
		}
		return jsonb().toJson(o);
	}
	
	private JsonProcessor() {
		// No instances allowed.
	}
	
	
}
