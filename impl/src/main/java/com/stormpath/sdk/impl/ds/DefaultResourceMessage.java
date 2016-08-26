/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.impl.http.CanonicalUri;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

public class DefaultResourceMessage implements ResourceMessage {

    private final ResourceAction action;
    private final CanonicalUri uri;
    private final Class<? extends Resource> resourceClass;
    private final Map<String,Object> data;
    private HttpHeaders httpHeaders;

    public DefaultResourceMessage(ResourceAction action, CanonicalUri uri, Class<? extends Resource> resourceClass, Map<String,Object> data) {
        Assert.notNull(action, "resource action cannot be null.");
        Assert.notNull(uri, "uri cannot be null.");
        Assert.notNull(resourceClass, "resourceClass cannot be null.");
        Assert.notNull(data, "data map cannot be null - specify an empty map instead of null.");
        this.action = action;
        this.uri = uri;
        this.resourceClass = resourceClass;
        this.data = data;
    }

    public DefaultResourceMessage(ResourceAction action, CanonicalUri uri, Class<? extends Resource> resourceClass, Map<String,Object> data, HttpHeaders customHeaders) {
        this(action, uri, resourceClass, data);
        this.httpHeaders = customHeaders;
    }

    @Override
    public CanonicalUri getUri() {
        return this.uri;
    }

    @Override
    public Class<? extends Resource> getResourceClass() {
        return this.resourceClass;
    }

    @Override
    public Map<String, Object> getData() {
        return this.data;
    }

    @Override
    public ResourceAction getAction() {
        return action;
    }

    /**
     * @since 1.0.RC7
     */
    public HttpHeaders getHttpHeaders() {
        return httpHeaders != null ? httpHeaders : new HttpHeaders();
    }
}
