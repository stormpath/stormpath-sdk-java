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
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link HttpRequest} instances.
 * <p/>
 * <p/>
 * The {@link HttpRequestBuilder} is useful to build {@link HttpRequest} instances for developers that don't
 * have access to the Servlet container, and therefore, cannot execute operations using implementations
 * of the {@code javax.servlet.HttpServlet} api.
 *
 * @see HttpRequest
 * @see HttpRequests
 * @see com.stormpath.sdk.application.Application#authenticate(Object)
 * @see com.stormpath.sdk.application.Application#authenticateOauth(Object)
 * @since 1.0.RC
 */
public interface HttpRequestBuilder {

    public HttpRequestBuilder headers(Map<String, String[]> headers);

    public HttpRequestBuilder body(InputStream body);

    public HttpRequestBuilder parameters(Map<String, String[]> parameters);

    public HttpRequestBuilder queryParameters(String queryParameters);

    public HttpRequest build();
}
