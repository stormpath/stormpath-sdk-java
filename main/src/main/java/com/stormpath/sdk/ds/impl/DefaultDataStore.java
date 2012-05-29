/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.ds.impl;

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.http.impl.Version;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.http.*;
import com.stormpath.sdk.http.impl.DefaultRequest;
import com.stormpath.sdk.util.Assert;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

/**
 * @since 0.1
 */
public class DefaultDataStore implements DataStore {

    public static final String DEFAULT_SERVER_HOST = "api.stormpath.com";

    public static final int DEFAULT_API_VERSION = 1;

    private RequestExecutor requestExecutor;
    private ResourceFactory resourceFactory;
    private MapMarshaller mapMarshaller;

    private String baseUrl;

    public DefaultDataStore(RequestExecutor requestExecutor) {
        this(requestExecutor, DEFAULT_API_VERSION);
    }

    public DefaultDataStore(RequestExecutor requestExecutor, int apiVersion) {
        this(requestExecutor, "https://" + DEFAULT_SERVER_HOST + "/v" + apiVersion);
    }

    public DefaultDataStore(RequestExecutor requestExecutor, String baseUrl) {
        Assert.notNull(baseUrl, "baseUrl cannot be null");
        Assert.notNull(requestExecutor, "RequestExecutor cannot be null.");
        this.baseUrl = baseUrl;
        this.requestExecutor = requestExecutor;
        this.resourceFactory = new DefaultResourceFactory(this);
        this.mapMarshaller = new JacksonMapMarshaller();
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.resourceFactory.instantiate(clazz);
    }

    @Override
    public <T extends Resource> T load(String href, Class<T> clazz) {
        Map<String,?> data = executeRequest(HttpMethod.GET, href);
        return this.resourceFactory.instantiate(clazz, data);
    }

    @SuppressWarnings("unchecked")
    private Map<String,?> executeRequest(HttpMethod method, String href) {
        Assert.notNull(href, "href argument cannot be null.");

        if (needsToBeFullyQualified(href)) {
            href = qualify(href);
        }

        Request request = new DefaultRequest(method, href);
        request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        request.getHeaders().set("User-Agent", "Stormpath-JavaSDK/" + Version.getClientVersion());

        Response response = this.requestExecutor.executeRequest(request);

        String body = null;

        if (response.hasBody()) {
            body = toString(response.getBody());
        }

        if (body != null) {
            return mapMarshaller.unmarshal(body);
        }

        return null;
    }

    protected boolean needsToBeFullyQualified(String href) {
        return !href.toLowerCase().startsWith("http");
    }

    protected String qualify(String href) {
        StringBuilder sb = new StringBuilder(this.baseUrl);
        if (!href.startsWith("/")) {
            sb.append("/");
        }
        sb.append(href);
        return sb.toString();
    }

    private static String toString(InputStream is) {
        try {
            return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
    }
}
