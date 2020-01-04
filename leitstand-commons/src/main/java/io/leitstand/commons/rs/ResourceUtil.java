/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.rs;

import static io.leitstand.commons.jsonb.IsoDateAdapter.parseIsoDate;

import java.util.Date;

public final class ResourceUtil {

	public static int tryParseInt(String s, int defaultValue) {
		if(s == null || s.isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static Date tryParseDate(String date) {
		return parseIsoDate(date);
	}

	private ResourceUtil() {
		// No instances allowed
	}

}
