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
package com.stormpath.sdk.impl.http;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;

import java.util.Map;

/**
 * This is the default implementation for {@link HttpRequest} interface.
 *
 * @since 1.0.RC
 */
public class DefaultHttpRequest implements HttpRequest {

    private final Map<String, String[]> headers;
    private final HttpMethod method;
    private final Map<String, String[]> parameters;
    private final String queryParameters;

    public DefaultHttpRequest(Map<String, String[]> headers, HttpMethod method, Map<String, String[]> parameters, String queryParameters) {
        this.headers = headers;
        this.method = method;
        this.parameters = parameters;
        this.queryParameters = queryParameters;

        Assert.notNull(method, "method cannot be null.");
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String headerName) {
        if (Collections.isEmpty(headers)) {
            return null;
        }

        for (Map.Entry<String, String[]> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(headerName)) {
                String[] values = entry.getValue();
                if (values == null || values.length == 0) {
                    return null;
                }
                return values[0];
            }
        }
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public Map<String, String[]> getParameters() {
        return parameters;
    }

    @Override
    public String getQueryParameters() {
        return queryParameters;
    }

}
