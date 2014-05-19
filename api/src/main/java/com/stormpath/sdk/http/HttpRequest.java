/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.http;

import java.io.InputStream;
import java.util.Map;

/**
 * Users not depending on the Servlet API will need to construct {@link HttpRequestBuilder HttpRequest objects} in order to be able to use this
 * SDK's {@link com.stormpath.sdk.application.Application#authenticate(Object) Oauth authentication mechanism}.
 *
 * @see HttpRequestBuilder
 * @since 1.0.RC
 */
public interface HttpRequest {

    public Map<String, String[]> getHeaders();

    public String getHeader(String headerName);

    public InputStream getBody();

    public HttpMethod getMethod();

    public Map<String, String[]> getParameters();

    public String getUri();

    public String getQueryParameters();

}
