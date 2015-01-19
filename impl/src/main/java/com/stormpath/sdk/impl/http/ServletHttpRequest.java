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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the {@link com.stormpath.sdk.http.HttpRequest HttpRequest} interface that delegates to a Servlet
 * {@link javax.servlet.http.HttpServletRequest HttpServletRequest} instance.
 */
public class ServletHttpRequest implements HttpRequest {

    private final HttpServletRequest request;
    private final Map<String,String[]> headers;

    public ServletHttpRequest(HttpServletRequest request) {
        Assert.notNull(request, "request argument cannot be null.");
        this.request = request;
        this.headers = new LinkedHashMap<String, String[]>();

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {

            while(headerNames.hasMoreElements()) {

                String headerName = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(headerName);

                if (values != null) {

                    List<String> valueList = new ArrayList<String>();

                    while(values.hasMoreElements()) {
                        String value = values.nextElement();
                        valueList.add(value);
                    }

                    if (!Collections.isEmpty(valueList)) {
                        String[] valueArray = valueList.toArray(new String[valueList.size()]);
                        headers.put(headerName, valueArray);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, String[]> getHeaders() {
        return this.headers;
    }

    @Override
    public String getHeader(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.fromName(request.getMethod());
    }

    @Override
    public Map<String, String[]> getParameters() {
        return request.getParameterMap();
    }

    @Override
    public String getParameter(String parameterName) {
        return request.getParameter(parameterName);
    }

    @Override
    public String getQueryParameters() {
        return request.getQueryString();
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }
}
