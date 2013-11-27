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
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
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
import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ReferenceFactory;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.util.StringInputStream;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.query.Options;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
    /**
     * @since 0.9
     */
    private final ReferenceFactory referenceFactory;

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
        this.cacheManager = new DisabledCacheManager(); //disabled by default - end-user must explicitly configure caching
        this.cacheRegionNameResolver = new DefaultCacheRegionNameResolver();
        this.referenceFactory = new ReferenceFactory();
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
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Resource class argument cannot be null.");
        SanitizedQuery sanitized = QuerySanitizer.sanitize(href, null);
        return getResource(sanitized.getHrefWithoutQuery(), clazz, sanitized.getQuery());
    }

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters) {
        SanitizedQuery sanitized = QuerySanitizer.sanitize(href, queryParameters);
        return getResource(sanitized.getHrefWithoutQuery(), clazz, sanitized.getQuery());
    }

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Criteria criteria) {
        Assert.isInstanceOf(DefaultCriteria.class, criteria,
                "The " + getClass().getName() + " implementation only functions with " +
                        DefaultCriteria.class.getName() + " instances.");

        DefaultCriteria dc = (DefaultCriteria) criteria;
        QueryString qs = queryStringFactory.createQueryString(href, dc);
        return getResource(href, clazz, qs);
    }

    private <T extends Resource> T getResource(String href, Class<T> clazz, QueryString qs) {

        //need to qualify the href it to ensure our cache lookups work as expected
        //(cache key = fully qualified href):
        href = ensureFullyQualified(href);

        Map<String, ?> data = null;

        //check if cached:
        if (isCacheRetrievalEnabled(clazz)) {
            data = getCachedValue(href, clazz);
        }

        if (Collections.isEmpty(data)) {
            //not cached - execute a request:
            Request request = createRequest(HttpMethod.GET, href, qs);
            data = executeRequest(request);

            if (!Collections.isEmpty(data) && isCacheUpdateEnabled(clazz)) {
                //cache for further use:
                cache(clazz, data);
            }
        }

        if (CollectionResource.class.isAssignableFrom(clazz)) {
            //only collections can support a query string constructor argument:
            return this.resourceFactory.instantiate(clazz, data, qs);
        }
        //otherwise it must be an instance resource, so use the two-arg constructor:
        return this.resourceFactory.instantiate(clazz, data);
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
        LinkedHashMap<String, Object> props = toMap(ret, false);
        in.setProperties(props);

        return (T) in;
    }

    @SuppressWarnings("unchecked")
    public <T extends Resource> T create(String parentHref, T resource, Options options) {
        Assert.isInstanceOf(DefaultOptions.class, options,
                "The " + getClass().getName() + " implementation only functions with " +
                        DefaultOptions.class.getName() + " instances.");

        DefaultOptions defaultOptions = (DefaultOptions) options;
        QueryString qs = queryStringFactory.createQueryString(parentHref, defaultOptions);

        Class<T> clazz = (Class<T>) resource.getClass();

        T returnValue = save(parentHref, resource, clazz, qs);

        //ensure the caller's argument is updated with what is returned from the server:
        AbstractResource in = (AbstractResource) resource;
        AbstractResource ret = (AbstractResource) returnValue;
        LinkedHashMap<String, Object> props = toMap(ret, false);
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
        Assert.hasLength(href, "'save' may only be called on objects that have already been " +
                "persisted and have an existing " + AbstractResource.HREF_PROP_NAME + " attribute.");

        Class<T> clazz = (Class<T>) resource.getClass();

        T returnValue = save(href, resource, clazz);

        //ensure the caller's argument is updated with what is returned from the server:
        AbstractResource ret = (AbstractResource) returnValue;
        LinkedHashMap<String, Object> props = toMap(ret, false);
        aResource.setProperties(props);
    }

    @Override
    public <T extends Resource & Saveable> void save(T resource, Options options) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.isInstanceOf(Saveable.class, resource);

        Assert.isInstanceOf(DefaultOptions.class, options,
                "The " + getClass().getName() + " implementation only functions with " +
                        DefaultOptions.class.getName() + " instances.");

        AbstractResource aResource = (AbstractResource) resource;

        String href = aResource.getHref();
        Assert.hasLength(href, "'save' may only be called on objects that have already been " +
                "persisted and have an existing " + AbstractResource.HREF_PROP_NAME + " attribute.");

        DefaultOptions defaultOptions = (DefaultOptions) options;
        QueryString qs = queryStringFactory.createQueryString(href, defaultOptions);

        Class<T> clazz = (Class<T>) resource.getClass();

        T returnValue = save(href, resource, clazz, qs);

        //ensure the caller's argument is updated with what is returned from the server:
        AbstractResource ret = (AbstractResource) returnValue;
        LinkedHashMap<String, Object> props = toMap(ret, false);
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
        return save(href, resource, returnType, null);
    }

    private <T extends Resource, R extends Resource> R save(String href, T resource, Class<? extends R> returnType, QueryString queryString) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);

        href = ensureFullyQualified(href);

        AbstractResource abstractResource = (AbstractResource) resource;

        LinkedHashMap<String, Object> props = toMap(abstractResource, true);

        String bodyString = mapMarshaller.marshal(props);

        StringInputStream body = new StringInputStream(bodyString);
        long length = body.available();

        Request request = new DefaultRequest(HttpMethod.POST, href, queryString, null, body, length);

        Map<String, Object> responseBody = executeRequest(request);

        if (Collections.isEmpty(responseBody)) {
            return null;
        }

        //asserts invariant given that we should have returned if the responseBody is null or empty:
        assert responseBody != null && !responseBody.isEmpty() : "Response body must be non-empty.";

        if (isCacheUpdateEnabled(returnType)) {
            cache(returnType, responseBody);
        }

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

        Request request = createRequest(HttpMethod.DELETE, href, null);
        executeRequest(request);
    }

    @Override
    public <T extends Resource> void deleteResourceProperty(T resource, String propertyName) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.hasText(propertyName, "propertyName cannot be null or empty.");

        AbstractResource abstractResource = (AbstractResource) resource;
        String href = abstractResource.getHref();
        href = href + "/" + propertyName;

        uncache(abstractResource);

        Request request = createRequest(HttpMethod.DELETE, href, null);
        executeRequest(request);
    }

    /* =====================================================================
       Resource Caching
       ===================================================================== */

    /**
     * @since 0.8
     */
    protected boolean isCachingEnabled() {
        return this.cacheManager != null && !(this.cacheManager instanceof DisabledCacheManager);
    }

    /**
     * @since 0.8
     */
    private <T extends Resource> boolean isCacheRetrievalEnabled(Class<T> clazz) {
        //we currently don't cache CollectionResources themselves (only their internal instance resources).  So we
        //return false in this case so a new cache region isn't auto created unnecessarily
        //(cacheManager.getCache(name) will auto-create a region if called and it does not yet exist)
        return isCachingEnabled() && !CollectionResource.class.isAssignableFrom(clazz);
    }

    /**
     * @since 0.8
     */
    private <T extends Resource> boolean isCacheUpdateEnabled(Class<T> clazz) {
        //we _do_ allow the cache to be updated with data associated with a collection resource.  The collection
        //resource itself won't be cached, but any of its nested instance resources will be.
        return isCachingEnabled();
    }

    /**
     * Quick fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>.
     *
     * @since 0.8.1
     */
    private boolean isDirectlyCacheable(Class<? extends Resource> clazz, Map<String, ?> data) {

        return isCachingEnabled() &&

                !Collections.isEmpty(data) &&

                //Authentication results (currently) do not have an 'href' attribute, as it was not expected to support
                // GET requests.  This will be resolved within Stormpath, but this is a fix for the SDK for now (for
                // Issue #17).  They are not directly cacheable, but any materialized references they contain are:
                data.get(AbstractResource.HREF_PROP_NAME) != null &&

                //we don't cache collection resources at the moment (only the instances inside them):
                !CollectionResource.class.isAssignableFrom(clazz);
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data) {
        if (!isCachingEnabled()) {
            return;
        }

        Assert.notEmpty(data, "Resource data cannot be null or empty.");
        String href = (String) data.get(AbstractResource.HREF_PROP_NAME);

        if (isDirectlyCacheable(clazz, data)) {
            Assert.notNull(href, "Resource data must contain an '" + AbstractResource.HREF_PROP_NAME + "' attribute.");
            Assert.isTrue(data.size() > 1,
                    "Resource data must be materialized to be cached (need more than just an '" +
                            AbstractResource.HREF_PROP_NAME + "' attribute).");
        }

        Map<String, Object> toCache = new LinkedHashMap<String, Object>(data.size());

        if (CustomData.class.isAssignableFrom(clazz)) {
            toCache.putAll(data);
            Cache cache = getCache(clazz);
            cache.put(href, toCache);
            return;
        }

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                //the value is a resource reference
                Map<String, ?> nested = (Map<String, ?>) value;

                Assert.notEmpty(nested, "Resource references are expected to be complex objects with at least an '" +
                        AbstractResource.HREF_PROP_NAME + "' property.");
                Assert.notNull(nested.get(AbstractResource.HREF_PROP_NAME),
                        "Resource references must have an '" + AbstractResource.HREF_PROP_NAME + "' attribute.");

                if (isMaterialized(nested)) {
                    //If there is more than one attribute (more than just 'href') it is not just a simple reference
                    //anymore - it has been materialized to its full set of attributes.  Because we have a full
                    //materialized resource, we need to recursively cache it (and any of its referenced materialized
                    //resources) and so on.

                    //find the type of object this attribute name represents:
                    Property property = getPropertyDescriptor(clazz, name);
                    Assert.isTrue(property instanceof ResourceReference,
                            "It is expected that only ResourceReference properties are complex objects.");

                    //cache this materialized reference:
                    cache(property.getType(), nested);

                    //Because the materialized reference has now been cached, we don't need to store
                    //all of its properties again in the 'toCache' instance.  Instead, we just want to store
                    //an unmaterialized reference (a Map with just the 'href' attribute).
                    //If the a caller attempts to materialize the reference, we will hit the cached version and
                    //use that data instead of issuing a request.
                    value = this.referenceFactory.createReference(name, nested);
                }
            } else if (value instanceof Collection) { //array property, i.e. the 'items' collection resource property
                Collection c = (Collection) value;
                //We don't currently cache collection attributes; only the instances they contain.  Create a new
                //collection that has only references, caching any materialized references in the process:
                List list = new ArrayList(c.size());

                //if the values in the collection are materialized, we need to cache that materialized reference.
                //If the value is not materialized, we don't do anything.

                //find the type of objects this collection contains:
                Property property = getPropertyDescriptor(clazz, name);
                Assert.isTrue(property instanceof ArrayProperty,
                        "It is expected that only ArrayProperty properties represent collection items.");

                ArrayProperty itemsProperty = ArrayProperty.class.cast(property);
                Class itemType = itemsProperty.getType();

                for (Object o : c) {
                    Object element = o;
                    if (o instanceof Map) {
                        Map referenceData = (Map) o;
                        if (isMaterialized(referenceData)) {
                            cache(itemType, referenceData);
                            element = this.referenceFactory.createReference(referenceData);
                        }
                    }
                    list.add(element);
                }

                value = list;
            }

            if (!DefaultAccount.PASSWORD.getName().equals(name)) { //don't cache sensitive data
                toCache.put(name, value);
            }
        }

        //we don't cache collection resources at the moment (only the instances inside them):
        if (isDirectlyCacheable(clazz, toCache)) {
            Cache cache = getCache(clazz);
            cache.put(href, toCache);
        }
    }

    /**
     * @since 0.8
     */
    private boolean isMaterialized(Map<String, ?> props) {
        return props != null && props.get("href") != null && props.size() > 1;
    }

    /**
     * @since 0.8
     */
    private <T extends Resource> Property getPropertyDescriptor(Class<T> clazz, String propertyName) {
        Map<String, Property> descriptors = getPropertyDescriptors(clazz);
        return descriptors.get(propertyName);
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private <T extends Resource> Map<String, Property> getPropertyDescriptors(Class<T> clazz) {
        Class implClass = DefaultResourceFactory.getImplementationClass(clazz);
        try {
            Field field = implClass.getDeclaredField("PROPERTY_DESCRIPTORS");
            field.setAccessible(true);
            return (Map<String, Property>) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to access PROPERTY_DESCRIPTORS static field on implementation class " + clazz.getName(), e);
        }
    }

    /**
     * @since 0.8
     */
    private <T extends Resource> Map<String, ?> getCachedValue(String href, Class<T> clazz) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Class argument cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(clazz);
        return cache.get(href);
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private <T extends Resource> void uncache(T resource) {
        Assert.notNull(resource, "Resource argument cannot be null.");
        Cache cache = getCache(resource.getClass());
        String href = resource.getHref();
        cache.remove(href);
    }

    /**
     * @since 0.8
     */
    private <T> Cache<String, Map<String, ?>> getCache(Class<T> clazz) {
        Assert.notNull(clazz, "Class argument cannot be null.");
        String cacheRegionName = this.cacheRegionNameResolver.getCacheRegionName((Class) clazz);
        return this.cacheManager.getCache(cacheRegionName);
    }

    private LinkedHashMap<String, Object> toMap(AbstractResource resource, boolean isUpdateMap) {

        Set<String> propNames;

        if (isUpdateMap) {
            propNames = resource.getUpdatedPropertyNames();
        } else {
            propNames = resource.getPropertyNames();
        }

        LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>(propNames.size());

        for (String propName : propNames) {
            Object prop = resource.getProperty(propName);

            //if the property is CustomData will be written in its entirety.
            if (resource instanceof CustomData) {
                props.put(propName, prop);
                continue;
            }
            if (prop instanceof CustomData) {
                if (isUpdateMap) {
                    Assert.isInstanceOf(AbstractResource.class, prop);

                    AbstractResource customDataAbstractResource = (AbstractResource) prop;
                    LinkedHashMap<String, Object> customDataProperties = new LinkedHashMap<String, Object>(propNames.size());

                    for (String updatedCustomPropertyName : customDataAbstractResource.getUpdatedPropertyNames()) {
                        Object object = customDataAbstractResource.getProperty(updatedCustomPropertyName);
                        customDataProperties.put(updatedCustomPropertyName, object);
                    }
                    props.put(propName, customDataProperties);

                } else {
                    props.put(propName, prop);
                }
                continue;
            }
            //if the property is a reference, don't write the entire object - just the href will do:
            if (prop instanceof Map) {
                prop = this.referenceFactory.createReference(propName, (Map) prop);
            } else if (prop instanceof Resource) {
                prop = this.referenceFactory.createReference(propName, (Resource) prop);
            }
            props.put(propName, prop);
        }

        return props;
    }

    private Request createRequest(HttpMethod method, String href, Map<String, ?> queryParams) {
        Assert.notNull(href, "href argument cannot be null.");
        href = ensureFullyQualified(href);
        QueryString qs = queryStringFactory.createQueryString(queryParams);
        return new DefaultRequest(method, href, qs);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> executeRequest(Request request) {

        applyDefaultRequestHeaders(request);

        Response response = this.requestExecutor.executeRequest(request);
        log.trace("Executed HTTP request.");

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

    /**
     * @since 0.8
     */
    protected String ensureFullyQualified(String href) {
        String value = href;
        if (!isFullyQualified(href)) {
            value = qualify(href);
        }
        return value;
    }

    protected boolean isFullyQualified(String href) {
        return href.toLowerCase().startsWith("http");
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
