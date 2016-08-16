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
package com.stormpath.sdk.http;

import java.io.InputStream;
import java.net.URI;

/**
 * @since 0.1
 */
public interface Request extends HttpMessage {

    HttpMethod getMethod();

    /**
     * Returns the request target resource's Uniform Resource Locator location <em>without a query string</em>.  Query
     * string parameters are maintained separately via the {@link #getQueryString() queryString} property.
     * <p/>
     * This URI can be thought of the request fully qualified URL before any question mark indicating query parameters,
     * e.g. the parts in bold only:
     * <p/>
     * <code><b>https://some.host.com/some/resource/path/here</b>?some=param&another=param</code>.
     * <p/>
     * Any potential question mark itself and anything after it are not included.
     *
     * @return the request target resource's Uniform Resource Locator location <em>without a query string</em>.
     */
    URI getResourceUrl();

    QueryString getQueryString();

    void setQueryString(QueryString queryString);

    void setBody(InputStream body, long length);
}
