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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.http.HttpMethod;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.QueryStringFactory;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.http.Response;
import com.stormpath.sdk.impl.http.support.DefaultRequest;
import com.stormpath.sdk.impl.http.support.Version;
import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.util.StringInputStream;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @since 0.1
 */
public class DefaultDataStore implements InternalDataStore {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataStore.class);

    public static final String DEFAULT_SERVER_HOST = "api.stormpath.com";

    public static final int DEFAULT_API_VERSION = 1;

    private final RequestExecutor requestExecutor;
    private final ResourceFactory resourceFactory;
    private final MapMarshaller mapMarshaller;
    private volatile CacheManager cacheManager;
    private volatile CacheRegionNameResolver cacheRegionNameResolver;

    private final String baseUrl;

    private final QueryStringFactory queryStringFactory;

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
        this.queryStringFactory = new QueryStringFactory();
        this.cacheRegionNameResolver = new DefaultCacheRegionNameResolver();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheRegionNameResolver(CacheRegionNameResolver cacheRegionNameResolver) {
        this.cacheRegionNameResolver = cacheRegionNameResolver;
    }

    /* =====================================================================
       Resource Instantiation
       ===================================================================== */

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz) {
        return this.resourceFactory.instantiate(clazz);
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Map<String, Object> properties) {
        return this.resourceFactory.instantiate(clazz, properties);
    }

    /* =====================================================================
       Resource Retrieval
       ===================================================================== */

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        Map<String, ?> data = executeRequest(HttpMethod.GET, href);
        //TODO: CACHING: cache(data);
        return this.resourceFactory.instantiate(clazz, data);
    }

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters) {
        QueryString qs = queryStringFactory.createQueryString(queryParameters);
        return getResource(href, clazz, qs);
    }

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Criteria criteria) {
        Assert.isInstanceOf(DefaultCriteria.class, criteria,
                "The " + getClass().getName() + " implementation only functions with " +
                        DefaultCriteria.class.getName() + " instances.");

        DefaultCriteria dc = (DefaultCriteria) criteria;
        QueryString qs = queryStringFactory.createQueryString(dc);
        return getResource(href, clazz, qs);
    }

    private <T extends Resource> T getResource(String href, Class<T> clazz, QueryString qs) {

        if (!isFullyQualified(href)) {
            href = qualify(href);
        }

        /*if (this.cacheManager != null) {
            Cache cache = this.cacheManager.getCache("resources");
            DefaultCacheKey key = new DefaultCacheKey(href, qs);

            String url = key.toString();
            if (!url.contains("?")) {  //TODO: cache resources with query parameters
                Object value = cache.get(url);
                if (value != null) {
                    Assert.state();
                }
                Map<String,Object> props = (Map<String,Object>)cache.get(url);
                if (props != null) {

                }

            }

            Object o = cache.get(key.toString());
            if (o != null) {
                Assert.state(clazz.isAssignableFrom(o.getClass()), "The existing cached value for cache entry [" +
                        key + "] is not an instance of " + clazz.getName() + ".  It is an instance of " +
                        o.getClass());
                return clazz.cast(o);
            }
        } */

        //not cached - perform a query:
        Map<String, ?> data = executeRequest(HttpMethod.GET, href, qs);

        //TODO: CACHING: cache(data);

        return this.resourceFactory.instantiate(clazz, data, qs);
    }

    /* =====================================================================
       Resource Persistence
       ===================================================================== */

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T create(String parentHref, T resource) {

        Class<T> clazz = (Class<T>) resource.getClass();

        T returnValue = create(parentHref, resource, clazz);

        //ensure the caller's argument is updated with what is returned from the server:
        AbstractResource in = (AbstractResource) resource;
        AbstractResource ret = (AbstractResource) returnValue;
        LinkedHashMap<String, Object> props = toMap(ret);
        in.setProperties(props);

        return (T) in;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource & Saveable> void save(T resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.isInstanceOf(Saveable.class, resource);

        AbstractResource aResource = (AbstractResource) resource;

        String href = aResource.getHref();
        Assert.isTrue(Strings.hasLength(href), "'save' may only be called on objects that have already been persisted and have an existing href.");

        Class<T> clazz = (Class<T>) resource.getClass();

        T returnValue = save(href, resource, clazz);

        //ensure the caller's argument is updated with what is returned from the server:
        AbstractResource ret = (AbstractResource) returnValue;
        LinkedHashMap<String, Object> props = toMap(ret);
        aResource.setProperties(props);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType) {
        return save(parentHref, resource, returnType);
    }

    @Override
    public <T extends Resource & Saveable, R extends Resource> R save(T resource, Class<? extends R> returnType) {
        return save(resource.getHref(), resource, returnType);
    }

    private <T extends Resource, R extends Resource> R save(String href, T resource, Class<? extends R> returnType) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);

        if (!isFullyQualified(href)) {
            href = qualify(href);
        }

        AbstractResource abstractResource = (AbstractResource) resource;

        LinkedHashMap<String, Object> props = toMap(abstractResource);

        String bodyString = mapMarshaller.marshal(props);

        StringInputStream body = new StringInputStream(bodyString);
        long length = body.available();

        Request request = new DefaultRequest(HttpMethod.POST, href, null, null, body, length);

        Map<String, Object> responseBody = executeRequest(request);

        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }

        //TODO: CACHING: cache(responseBody);

        return resourceFactory.instantiate(returnType, responseBody);
    }

    /* =====================================================================
       Resource Deletion
       ===================================================================== */

    @Override
    public <T extends Resource> void delete(T resource) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        AbstractResource abstractResource = (AbstractResource) resource;
        String href = abstractResource.getHref();
        uncache(abstractResource);
        executeRequest(HttpMethod.DELETE, href);
    }

    /* =====================================================================
       Resource Caching
       ===================================================================== */

    @SuppressWarnings("unchecked")
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data) {
        if (this.cacheManager == null || Collections.isEmpty(data)) {
            return;
        }

        String href = null;

        Map<String, Object> toCache = new LinkedHashMap<String, Object>(data.size());

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if ("href".equals(key)) {
                href = (String) value;
            }

            if (value instanceof Map) {
                Map<String, ?> nested = (Map<String, ?>) value;
                //TODO: CACHING: cache(nested);

                String nestedHref = (String) nested.get("href");
                Map<String, String> replaced = new LinkedHashMap<String, String>();
                replaced.put("href", nestedHref);
                value = replaced;
            }

            toCache.put(key, value);
        }

        Assert.state(href != null, "href cannot be null.");

        Cache cache = this.cacheManager.getCache("resources");
        cache.put(href, toCache);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> void uncache(T resource) {

        if (this.cacheManager == null || resource == null) {
            return;
        }

        String href = resource.getHref();
        String regionName = this.cacheRegionNameResolver.getCacheRegionName(resource);
        Cache cache = this.cacheManager.getCache(regionName);
        if (cache != null) {
            cache.remove(href);
        }
    }

    private LinkedHashMap<String, Object> toMap(AbstractResource resource) {
        Set<String> propNames = resource.getPropertyNames();

        LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>(propNames.size());

        for (String propName : propNames) {
            Object prop = resource.getProperty(propName);

            //if the property is a reference, don't write the entire object - just the href will do:
            if (prop instanceof Map) {
                prop = toSimpleReference(propName, (Map) prop);
            } else if (prop instanceof Resource) {
                prop = toSimpleReference(propName, (Resource) prop);
            }

            props.put(propName, prop);
        }

        return props;
    }

    private Map<String, String> toSimpleReference(String propName, Map map) {
        Assert.isTrue(!map.isEmpty() && map.containsKey(AbstractResource.HREF_PROP_NAME),
                "Nested resource '" + propName + "' must have an 'href' property.");
        String href = String.valueOf(map.get(AbstractResource.HREF_PROP_NAME));

        Map<String, String> reference = new HashMap<String, String>(1);
        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }

    /**
     * @since 0.6.0
     */
    private Map<String, String> toSimpleReference(String propName, Resource resource) {
        String href = resource.getHref();
        Assert.hasText(href, "Nested Resource '" + propName + "' must have an 'href' property.");

        Map<String, String> reference = new HashMap<String, String>(1);
        reference.put(AbstractResource.HREF_PROP_NAME, href);

        return reference;
    }

    private Map<String, ?> executeRequest(HttpMethod method, String href) {
        return executeRequest(method, href, null);
    }

    private Map<String, ?> executeRequest(HttpMethod method, String href, Map<String, ?> queryParameters) {
        Assert.notNull(href, "href argument cannot be null.");

        if (!isFullyQualified(href)) {
            href = qualify(href);
        }

        QueryString qs = queryStringFactory.createQueryString(queryParameters);

        Request request = new DefaultRequest(method, href, qs);

        return executeRequest(request);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> executeRequest(Request request) {

        applyDefaultRequestHeaders(request);

        Response response = this.requestExecutor.executeRequest(request);

        String body = null;

        if (response.hasBody()) {
            body = toString(response.getBody());
        }

        Map<String, Object> mapBody = null;

        if (body != null) {
            log.trace("Obtained response body: \n{}", body);
            mapBody = mapMarshaller.unmarshal(body);
        }

        if (response.isError()) {
            DefaultError error = new DefaultError(mapBody);
            throw new ResourceException(error);
        }

        return mapBody;
    }

    protected void applyDefaultRequestHeaders(Request request) {
        request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        request.getHeaders().set("User-Agent", "Stormpath-JavaSDK/" + Version.getClientVersion());
        if (request.getBody() != null) {
            //this data store currently only deals with JSON messages:
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
    }

    protected boolean isFullyQualified(String href) {
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
