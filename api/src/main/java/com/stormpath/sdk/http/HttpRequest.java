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

import java.util.Map;

/**
 * Users not depending on the Servlet API will need to construct {@link HttpRequestBuilder HttpRequest objects} to be
 * able to use this SDK's {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object) Oauth
 * authentication mechanism}.
 *
 * @see HttpRequestBuilder
 * @since 1.0.RC
 */
public interface HttpRequest {

    /**
     * Returns the request's headers.  Never null.
     *
     * @return the request's headers.  Never null.
     * @see #getHeader(String)
     */
    public Map<String, String[]> getHeaders();

    /**
     * Returns the first available value for the request header with the specified name, or {@code null} if the header
     * does not exist.
     *
     * <p>If there is more than one value, only the first will be returned and any subsequent values will be omitted.
     * If you believe a header will have multiple values, use {@link #getHeaders() getHeaders()} instead.
     *
     * @param headerName the name of the header to inspect
     * @return the first available value for the request header with the specified name, or {@code null} if the header
     *         does not exist.
     */
    public String getHeader(String headerName);

    /**
     * Returns the request method.
     *
     * @return the request method.
     */
    public HttpMethod getMethod();

    /**
     * Returns the request parameters.
     *
     * @return the request parameters.
     */
    public Map<String, String[]> getParameters();

    /**
     * Returns the first available value of the request parameter with the specified name or {@code null} if the
     * parameter does not exist.
     *
     * @param parameterName the name of the parameter to look up
     * @return the first available value of the request parameter with the specified name or {@code null} if the
     *         parameter does not exist.
     * @since 1.0.RC2
     */
    public String getParameter(String parameterName);

    /**
     * Returns the request query parameter String, or {@code null} if there is no URI query parameter component.
     *
     * @return the request query parameter String, or {@code null} if there is no URI query parameter component.
     */
    public String getQueryParameters();

}
