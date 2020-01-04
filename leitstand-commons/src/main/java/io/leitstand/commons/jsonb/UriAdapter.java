/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.jsonb;

import static io.leitstand.commons.model.ObjectUtil.optional;

import java.net.URI;

import javax.json.bind.adapter.JsonbAdapter;

public class UriAdapter implements JsonbAdapter<URI, String> {

	@Override
	public String adaptToJson(URI obj) throws Exception {
		return optional(obj,URI::toString);
	}

	@Override
	public URI adaptFromJson(String obj) throws Exception {
		return optional(obj,URI::create);
	}

}
