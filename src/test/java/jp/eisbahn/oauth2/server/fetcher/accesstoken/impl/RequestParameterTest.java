/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package jp.eisbahn.oauth2.server.fetcher.accesstoken.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import jp.eisbahn.oauth2.server.fetcher.accesstoken.AccessTokenFetcher.FetchResult;
import jp.eisbahn.oauth2.server.models.Request;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequestParameterTest {

	private RequestParameter target;

	@Before
	public void setUp() throws Exception {
		target = new RequestParameter();
	}

	@After
	public void tearDown() throws Exception {
		target = null;
	}

	@Test
	public void testMatch() throws Exception {
		Request req;

		req = createRequestMock(null, null);
		assertFalse(target.match(req));
		verify(req);

		req = createRequestMock("token1", null);
		assertTrue(target.match(req));
		verify(req);

		req = createRequestMock(null, "token1");
		assertTrue(target.match(req));
		verify(req);

		req = createRequestMock("token1", "token2");
		assertTrue(target.match(req));
		verify(req);
	}

	@Test
	public void testParse() throws Exception {
		Request req;
		FetchResult parseResult;

		req = createRequestMock(new String[] { "oauth_token",
				"access_token_value" });
		parseResult = target.fetch(req);
		assertEquals("access_token_value", parseResult.getToken());
		assertTrue(parseResult.getParams().isEmpty());
		verify(req);

		req = createRequestMock(new String[] { "access_token",
				"access_token_value" });
		parseResult = target.fetch(req);
		assertEquals("access_token_value", parseResult.getToken());
		assertTrue(parseResult.getParams().isEmpty());
		verify(req);

		req = createRequestMock(new String[] { "access_token",
				"access_token_value", "foo", "bar" });
		parseResult = target.fetch(req);
		assertEquals("access_token_value", parseResult.getToken());
		assertFalse(parseResult.getParams().isEmpty());
		Map<String, String[]> params = parseResult.getParams();
		assertEquals(1, params.size());
		assertEquals("bar", params.get("foo")[0]);
		verify(req);
	}

	private Request createRequestMock(String oauthToken, String accessToken) {
		Request request = createMock(Request.class);
		expect(request.getParameter("oauth_token")).andReturn(oauthToken);
		expect(request.getParameter("access_token")).andReturn(accessToken);
		replay(request);
		return request;
	}

	private Request createRequestMock(String[] values) {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		putAll(parameterMap, values);
		Request request = createMock(Request.class);
		expect(request.getParameterMap()).andReturn(parameterMap);
		replay(request);
		return request;
	}

	private Map<?, ?> putAll(Map<String, String[]> map, String[] array) {
		map.size();
		if ((array == null) || (array.length == 0)) {
			return map;
		}
		for (int i = 0; i < array.length - 1;) {
			map.put(array[(i++)], new String[] { array[(i++)] });
		}
		return map;
	}

}
