/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.support;

import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.lang.Strings;

import java.io.InputStream;
import java.net.URI;

/**
 * @since 0.1
 */
public class DefaultRequest extends AbstractHttpMessage implements Request {

    private final HttpMethod method;
    private final URI resourceUrl;
    private final HttpHeaders headers;
    private final QueryString queryString;
    private InputStream body;

    public DefaultRequest(HttpMethod method, String href) {
        this(method, href, null, null, null, -1L);
    }

    public DefaultRequest(HttpMethod method, String href, QueryString query) {
        this(method, href, query, null, null, -1L);
    }

    public DefaultRequest(HttpMethod method, String href, QueryString query, HttpHeaders headers, InputStream body, long contentLength) {
        this.method = method;

        String[] split = Strings.split(href, "?");
        if (split != null) {
            this.resourceUrl = URI.create(split[0]);
            this.queryString = QueryString.create(split[1]);
            if (query != null && !query.isEmpty()) {
                this.queryString.putAll(query);
            }
        } else {
            //no question mark = no query string, so create a new one:
            this.resourceUrl = URI.create(href);
            this.queryString = query != null ? query : new QueryString();
        }

        this.headers = headers != null ? headers : new HttpHeaders();
        this.body = body;
        this.headers.setContentLength(contentLength);
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public URI getResourceUrl() {
        return this.resourceUrl;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(HttpHeaders headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    @Override
    public QueryString getQueryString() {
        return this.queryString;
    }

    @Override
    public void setQueryString(QueryString queryString) {
        this.queryString.clear();
        this.queryString.putAll(queryString);
    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public void setBody(InputStream body, long length) {
        this.body = body;
        getHeaders().setContentLength(length);
    }
}
