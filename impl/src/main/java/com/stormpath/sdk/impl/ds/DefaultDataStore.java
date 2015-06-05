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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import com.stormpath.sdk.impl.ds.api.ApiKeyCachePropertiesFilter;
import com.stormpath.sdk.impl.ds.api.ApiKeyQueryPropertiesFilter;
import com.stormpath.sdk.impl.ds.api.ApiKeyResourcePropertiesFilter;
import com.stormpath.sdk.impl.ds.cache.CacheResolver;
import com.stormpath.sdk.impl.ds.cache.DefaultCacheResolver;
import com.stormpath.sdk.impl.ds.cache.ReadCacheFilter;
import com.stormpath.sdk.impl.ds.cache.WriteCacheFilter;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.http.CanonicalUri;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.QueryStringFactory;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.http.Response;
import com.stormpath.sdk.impl.http.support.DefaultCanonicalUri;
import com.stormpath.sdk.impl.http.support.DefaultRequest;
import com.stormpath.sdk.impl.http.support.UserAgent;
import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.CollectionProperties;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ReferenceFactory;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.util.SoftHashMap;
import com.stormpath.sdk.impl.util.StringInputStream;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.mail.ModeledEmailTemplate;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.provider.ProviderData;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;
import static com.stormpath.sdk.impl.resource.AbstractCollectionResource.*;

/**
 * @since 0.1
 */
public class DefaultDataStore implements InternalDataStore {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataStore.class);

    public static final String DEFAULT_SERVER_HOST = "api.stormpath.com";
    public static final int DEFAULT_API_VERSION = 1;

    public static final String DEFAULT_CRITERIA_MSG = "The " + DefaultDataStore.class.getName() +
                                                      " implementation only functions with " +
                                                      DefaultCriteria.class.getName() + " instances.";

    public static final String DEFAULT_OPTIONS_MSG = "The " + DefaultDataStore.class.getName() +
                                                     " implementation only functions with " +
                                                     DefaultOptions.class.getName() + " instances.";

    public static final String HREF_REQD_MSG = "'save' may only be called on objects that have already been " +
                                               "persisted and have an existing " + AbstractResource.HREF_PROP_NAME +
                                               " attribute.";

    private final RequestExecutor requestExecutor;
    private final ResourceFactory resourceFactory;
    private final MapMarshaller mapMarshaller;
    private volatile CacheManager cacheManager;
    private volatile CacheRegionNameResolver cacheRegionNameResolver;
    private final ApiKey apiKey;
    private final PropertiesFilterProcessor<Map<String, ?>> resourceDataFilterProcessor;
    private final PropertiesFilterProcessor<QueryString> queryStringFilterProcessor;
    private volatile Map<String, Enlistment> hrefMapStore;

    private final List<Filter> filters;

    /**
     * @since 0.9
     */
    private final ReferenceFactory referenceFactory;

    private final String baseUrl;

    private final QueryStringFactory queryStringFactory;

    private final CacheMapInitializer cacheMapInitializer;

    /**
     * @since 1.0.RC3
     */
    public static final String USER_AGENT_STRING = UserAgent.getUserAgentString();

    public DefaultDataStore(RequestExecutor requestExecutor, ApiKey apiKey) {
        this(requestExecutor, DEFAULT_API_VERSION, apiKey);
    }

    public DefaultDataStore(RequestExecutor requestExecutor, int apiVersion, ApiKey apiKey) {
        this(requestExecutor, "https://" + DEFAULT_SERVER_HOST + "/v" + apiVersion, apiKey);
    }

    public DefaultDataStore(RequestExecutor requestExecutor, String baseUrl, ApiKey apiKey) {
        Assert.notNull(baseUrl, "baseUrl cannot be null");
        Assert.notNull(requestExecutor, "RequestExecutor cannot be null.");
        Assert.notNull(apiKey, "ApiKey cannot be null.");
        this.baseUrl = baseUrl;
        this.requestExecutor = requestExecutor;
        this.resourceFactory = new DefaultResourceFactory(this);
        this.mapMarshaller = new JacksonMapMarshaller();
        this.queryStringFactory = new QueryStringFactory();
        this.cacheManager = new DisabledCacheManager(); //disabled by default, user must explicitly configure caching
        this.cacheRegionNameResolver = new DefaultCacheRegionNameResolver();
        this.referenceFactory = new ReferenceFactory();
        this.hrefMapStore = new SoftHashMap<String, Enlistment>();
        this.apiKey = apiKey;
        this.cacheMapInitializer = new DefaultCacheMapInitializer();

        List<PropertiesFilter<Map<String, ?>>> l = new ArrayList<PropertiesFilter<Map<String, ?>>>(1);
        l.add(new ApiKeyCachePropertiesFilter(apiKey));
        this.resourceDataFilterProcessor = new DefaultPropertiesFilterProcessor<Map<String, ?>>(l);

        // Adding another processor for query strings because we don't want to mix
        // the processing (filtering) of the query strings with the processing of the resource properties,
        // even though they're both (resource properties and query string objects) Maps that might apply
        // to the be added to the same filter. This separation also improves requests performance.
        List<PropertiesFilter<QueryString>> l2 = new ArrayList<PropertiesFilter<QueryString>>(1);
        l2.add(new ApiKeyQueryPropertiesFilter());
        this.queryStringFilterProcessor = new DefaultPropertiesFilterProcessor<QueryString>(l2);

        this.filters = new ArrayList<Filter>();
        this.filters.add(new ApiKeyQueryPropertiesFilter());

        CacheResolver cacheResolver = new DefaultCacheResolver(this.cacheManager, this.cacheRegionNameResolver);
        this.filters.add(new ReadCacheFilter(this.baseUrl, cacheResolver));
        this.filters.add(new WriteCacheFilter(cacheResolver, this.referenceFactory));
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("unused")
    public void setCacheRegionNameResolver(CacheRegionNameResolver cacheRegionNameResolver) {
        this.cacheRegionNameResolver = cacheRegionNameResolver;
    }

    @Override
    public ApiKey getApiKey() {
        return apiKey;
    }

    @Override
    public CacheManager getCacheManager() {
        return this.cacheManager;
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

    private <T extends Resource> T instantiate(Class<T> clazz, Map<String, ?> properties, QueryString qs) {

        if (CollectionResource.class.isAssignableFrom(clazz)) {
            //only collections can support a query string constructor argument:
            return this.resourceFactory.instantiate(clazz, properties, qs);
        }
        //otherwise it must be an instance resource, so use the two-arg constructor:
        return this.resourceFactory.instantiate(clazz, properties);
    }

    /* =====================================================================
       Resource Retrieval
       ===================================================================== */

    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz) {
        return getResource(href, clazz, (Map<String, Object>) null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T getResource(String href, Class<T> clazz, Criteria criteria) {
        Assert.isInstanceOf(DefaultCriteria.class, criteria, DEFAULT_CRITERIA_MSG);
        QueryString qs = queryStringFactory.createQueryString(href, (DefaultCriteria)criteria);
        return (T) getResource(href, clazz, (Map) qs);
    }

    public <T extends Resource> T getResource(String href, Class<T> clazz, Map<String, Object> queryParameters) {
        ResourceDataResult result = getResourceData(href, clazz, queryParameters);
        return instantiate(clazz, result.getData(), result.getUri().getQuery());
    }

    /**
     * This method provides the ability to instruct the DataStore how to decide which class of a resource hierarchy will
     * be instantiated. For example, nowadays three {@link ProviderData} resources exists (ProviderData,
     * FacebookProviderData and GoogleProviderData). The <code>childIdProperty</code> is the property that will be used
     * in the response as the ID to seek for the proper concrete ProviderData class in the <code>idClassMap</>.
     *
     * @param href            the endpoint where the request will be targeted to.
     * @param parent          the root class of the Resource hierarchy (helps to validate that the idClassMap contains
     *                        subclasses of it).
     * @param childIdProperty the property whose value will be used to identify the specific class in the hierarchy that
     *                        we need to instantiate.
     * @param idClassMap      a mapping to be able to know which class corresponds to each <code>childIdProperty</code>
     *                        value.
     * @param <T>             the root of the hierarchy of the Resource we want to instantiate.
     * @param <R>             the sub-class of the root Resource.
     * @return the retrieved resource
     */
    @Override
    public <T extends Resource, R extends T> R getResource(String href, Class<T> parent, String childIdProperty,
                                                           Map<String, Class<? extends R>> idClassMap) {
        Assert.hasText(childIdProperty, "childIdProperty cannot be null or empty.");
        Assert.notEmpty(idClassMap, "idClassMap cannot be null or empty.");

        ResourceDataResult result = getResourceData(href, parent, null);
        Map<String,?> data = result.getData();

        if (Collections.isEmpty(data)) {
            throw new IllegalStateException(childIdProperty + " could not be found in: " + data + ".");
        }

        String childClassName = null;
        Object val = data.get(childIdProperty);
        if (val != null) {
            childClassName = String.valueOf(val);
        }
        Class<? extends R> childClass = idClassMap.get(childClassName);

        if (childClass == null) {
            throw new IllegalStateException("No Class mapping could be found for " + childIdProperty + ".");
        }

        return instantiate(childClass, data, result.getUri().getQuery());
    }

    @SuppressWarnings("unchecked")
    private ResourceDataResult getResourceData(String href, Class<? extends Resource> clazz, Map<String,?> queryParameters) {

        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Resource class argument cannot be null.");

        CanonicalUri uri = canonicalize(href, queryParameters);
        ResourceDataRequest req = new DefaultResourceDataRequest(uri, clazz, ResourceAction.READ);

        FilterChain chain = new DefaultFilterChain(this.filters, new FilterChain() {
            @Override
            public ResourceDataResult filter(ResourceDataRequest request) {
                return doGetResourceData(request);
            }
        });

        return chain.filter(req);

        /*
        final QueryString query = queryStringFilterProcessor.process(clazz, uri.getQuery());
        href = uri.getAbsolutePath();
        uri = new DefaultCanonicalUri(href, query); //reflect the filtered query

        ResourceData cached = getCachedResourceData(uri, clazz);
        if (cached != null) {
            return cached;
        }

        //not cached - execute a request:
        Request request = new DefaultRequest(HttpMethod.GET, href, query);
        Response response = execute(request);
        Map<String,?> body = getBody(response);

        if (Collections.isEmpty(body)) {
            throw new IllegalStateException("Unable to obtain resource data from the API server or from cache.");
        }

        if (isCacheUpdateEnabled(clazz)) {
            //cache for further use:
            cache(clazz, body, query);
        }

        // Adding the ApiKeyResourcePropertiesFilter here because if the resource was cached, the ApiKeyCachePropertiesFilter
        // already took care of decrypting the api key secret to return to the user.
        // Transitory filters serve the purpose of filtering the resource properties to return to the user,
        // based on the current request.
        // For example: decrypting the api key secret to return to the user
        // with the current request content (query strings, etc.); which is why they are transitory, because they cannot
        // be added when initializing the filter (they depend on the current request).
        body = filterResourceData(clazz, query, body);

        //@since 1.0.RC3
        if (AbstractInstanceResource.isInstanceResource(body)) {
            body = toEnlistment(body);
        }

        return new ResourceData(uri, (Map<String,Object>)body, clazz);
        */
    }

    @SuppressWarnings("unchecked")
    private ResourceDataResult doGetResourceData(ResourceDataRequest req) {

        //not cached - execute a request:
        final Class clazz = req.getResourceClass();

        Request request = new DefaultRequest(HttpMethod.GET, req.getUri().getAbsolutePath(), req.getUri().getQuery());
        Response response = execute(request);
        Map<String,?> body = getBody(response);

        if (Collections.isEmpty(body)) {
            throw new IllegalStateException("Unable to obtain resource data from the API server or from cache.");
        }

        if (isCacheUpdateEnabled(clazz)) {
            //cache for further use:
            cache(clazz, body, req.getUri().getQuery());
        }

        // Adding the ApiKeyResourcePropertiesFilter here because if the resource was cached, the ApiKeyCachePropertiesFilter
        // already took care of decrypting the api key secret to return to the user.
        // Transitory filters serve the purpose of filtering the resource properties to return to the user,
        // based on the current request.
        // For example: decrypting the api key secret to return to the user
        // with the current request content (query strings, etc.); which is why they are transitory, because they cannot
        // be added when initializing the filter (they depend on the current request).
        body = filterResourceData(clazz, req.getUri().getQuery(), body);

        //@since 1.0.RC3
        if (AbstractInstanceResource.isInstanceResource(body)) {
            body = toEnlistment(body);
        }

        return new DefaultResourceDataResult(req.getUri(), (Map<String,Object>)body, clazz);
    }



    private ResourceDataResult getCachedResourceData(ResourceDataRequest request) {

        final CanonicalUri uri = request.getUri();
        final String href = uri.getAbsolutePath();
        final QueryString query = uri.getQuery();
        final Class clazz = request.getResourceClass();

        Map<String, ?> data = null;

        boolean isApiKeyColQuery = isApiKeyCollectionQuery(request);

        if (isCacheRetrievalEnabled(request) || isApiKeyColQuery) {

            if (isApiKeyColQuery) {

                String cacheHref = baseUrl + "/apiKeys/" + query.get(ID.getName());
                Class<ApiKey> cacheClass = com.stormpath.sdk.api.ApiKey.class;

                Map<String, ?> apiKeyData = getCachedValue(cacheHref, cacheClass);

                if (!Collections.isEmpty(apiKeyData)) {
                    int offset = query.containsKey(OFFSET.getName()) ? Integer.valueOf(query.get(OFFSET.getName())) : 0;
                    int limit = query.containsKey(LIMIT.getName()) ? Integer.valueOf(query.get(LIMIT.getName())) : 25;
                    data = new CollectionProperties.Builder().setHref(href).setOffset(offset).setLimit(limit)
                                                             .setItemsMap(apiKeyData).build();
                }
            } else {
                data = getCachedValue(href, clazz);
            }
        }

        if (Collections.isEmpty(data)) {
            return null;
        }

        return new DefaultResourceDataResult(uri, (Map<String,Object>)data, clazz);
    }

    private Map<String, ?> filterResourceData(Class clazz, QueryString qs, Map<String, ?> data) {

        List<PropertiesFilter<Map<String, ?>>> resourceDataFilters = resourceDataFilterProcessor.getFilters();

        List<PropertiesFilter<Map<String, ?>>> filters =
            new ArrayList<PropertiesFilter<Map<String, ?>>>(resourceDataFilters);

        filters.add(new ApiKeyResourcePropertiesFilter(apiKey, qs));

        PropertiesFilterProcessor<Map<String, ?>> processor =
            new DefaultPropertiesFilterProcessor<Map<String, ?>>(filters);

        return processor.process(clazz, data);
    }


    /* =====================================================================
       Resource Persistence
       ===================================================================== */

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T create(String parentHref, T resource) {
        return (T)save(parentHref, resource, resource.getClass(), null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resource> T create(String parentHref, T resource, Options options) {
        QueryString qs = toQueryString(parentHref, options);
        return (T)save(parentHref, resource, resource.getClass(), qs);
    }

    @Override
    public <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType) {
        return save(parentHref, resource, returnType, null);
    }

    @Override
    public <T extends Resource & Saveable> void save(T resource) {
        String href = resource.getHref();
        Assert.hasText(href, HREF_REQD_MSG);
        save(href, resource, resource.getClass(), null);
    }

    @Override
    public <T extends Resource & Saveable> void save(T resource, Options options) {
        Assert.notNull(options, "options argument cannot be null.");
        String href = resource.getHref();
        Assert.hasText(href, HREF_REQD_MSG);
        QueryString qs = toQueryString(href, options);
        save(href, resource, resource.getClass(), qs);
    }

    @Override
    public <T extends Resource & Saveable, R extends Resource> R save(T resource, Class<? extends R> returnType) {
        Assert.hasText(resource.getHref(), HREF_REQD_MSG);
        return save(resource.getHref(), resource, returnType, null);
    }

    private QueryString toQueryString(String href, Options options) {
        if (options == null) {
            return null;
        }
        Assert.isInstanceOf(DefaultOptions.class, options, DEFAULT_OPTIONS_MSG);
        DefaultOptions defaultOptions = (DefaultOptions)options;
        return queryStringFactory.createQueryString(href, defaultOptions);
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource, R extends Resource> R save(String href, final T resource, Class<? extends R> returnType, QueryString qs) {

        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);
        Assert.isTrue(!CollectionResource.class.isAssignableFrom(resource.getClass()), "Collections cannot be persisted.");

        final CanonicalUri uri = canonicalize(href, qs);
        qs = uri.getQuery();
        href = uri.getAbsolutePath();

        final AbstractResource abstractResource = (AbstractResource) resource;
        LinkedHashMap<String, Object> props = toMap(abstractResource, true);
        String bodyString = mapMarshaller.marshal(props);
        StringInputStream body = new StringInputStream(bodyString);
        long length = body.available();
        QueryString filteredQs = queryStringFilterProcessor.process(returnType, qs);
        Request request = new DefaultRequest(HttpMethod.POST, href, filteredQs, null, body, length);

        Response response = execute(request);
        Map<String, Object> responseBody = getBody(response);

        //since 1.0.beta: provider's account creation status (whether it is new or not) is returned in the HTTP response
        //status. The resource factory does not provide a way to pass such information when instantiating a resource. Thus,
        //after the resource has been instantiated we are going to manipulate it before returning it in order to set the
        //"is new" status
        int httpStatus = response.getHttpStatus();
        if (ProviderAccountResult.class.isAssignableFrom(returnType) && (httpStatus == 200 || httpStatus == 201)) {
            if (httpStatus == 200) { //is not a new account
                responseBody.put("isNewAccount", false);
            } else {
                responseBody.put("isNewAccount", true);
            }
        }

        // Filter resource properties to return to the user, based on the current request.
        // For example: decrypting the api key secret to return to the user
        // with the current request content (query strings, etc.); which is why they are transitory, because they cannot
        // be added when initializing the filter (they depend on the current request).
        Map<String, ?> returnResponseBody = filterResourceData(returnType, filteredQs, responseBody);

        if (Collections.isEmpty(responseBody)) {
            return null;
        }

        //since 1.0.RC3 RC: emailVerification boolean hack. See: https://github.com/stormpath/stormpath-sdk-java/issues/60
        boolean emailVerification = resource instanceof EmailVerificationToken && returnType.equals(Account.class);
        //since 1.0.RC4 : fix for https://github.com/stormpath/stormpath-sdk-java/issues/140 where Account remains disabled after
        //successful verification due to an outdated `Account` state in the cache.
        if (emailVerification && isCachingEnabled()) {
            String accountHref = (String) responseBody.get(HREF_PROP_NAME);
            if (Strings.hasText(accountHref)) {
                Cache<String, ?> cache = getCache(Account.class);
                cache.remove(accountHref);
            }
        }

        //since 1.0.RC4: uncaching boolean hack. PasswordResetToken. See: https://github.com/stormpath/stormpath-sdk-java/issues/132
        boolean doNotCache =
            (resource instanceof PasswordResetToken && PasswordResetToken.class.isAssignableFrom(returnType)) ||
            emailVerification;

        if (isCacheUpdateEnabled(returnType) && !doNotCache) {
            //@since 1.0.RC3: Let's first check if the response is an actual Resource (meaning, that it has an href property)
            if (Strings.hasText((String) returnResponseBody.get(HREF_PROP_NAME))) {
                //@since 1.0.RC3: ProviderAccountResult is both a Resource and has an href property, but it must not be cached
                if (!returnType.isAssignableFrom(ProviderAccountResult.class)) {
                    cache(returnType, responseBody, filteredQs);
                }
            }
        }

        //since 0.9.2: custom data quick fix for https://github.com/stormpath/stormpath-sdk-java/issues/30
        //If the resource saved has nested custom data, and any custom data was specified when saving the resource,
        //we need to ensure that the custom data is cached properly since it won't be returned by the server by
        //default.  There is probably a much cleaner OO way of doing this, but it wasn't worth it at impl time to
        //find a smoother way.  Helper methods have been marked as private to indicate that this shouldn't be used as
        //a dependency in case we choose to implement a cleaner way later.
        if (resource instanceof AbstractExtendableInstanceResource && isCacheUpdateEnabled(CustomData.class)) {
            cacheNestedCustomData(href, props);
        }

        //@since 1.0.RC3
        if (AbstractResource.isMaterialized(returnResponseBody)) {
            returnResponseBody = toEnlistment(returnResponseBody);
        }

        //ensure the caller's argument is updated with what is returned from the server if the types are the same:
        if (returnType.equals(abstractResource.getClass())) {
            abstractResource.setProperties((Map<String,Object>)returnResponseBody);
        }

        return resourceFactory.instantiate(returnType, returnResponseBody);
    }

    /**
     * Helps fix <a href="https://github.com/stormpath/stormpath-sdk-java/issues/30">Issue #30</a>.
     * <p/>
     * This implementation ensures that if custom data is nested inside an AbstractExtendableInstanceResource (an
     * Account, Group, Directory, Application or Tenant) and that AbstractExtendableInstanceResource is saved, that the
     * nested custom data submitted and saved to the server is also updated in any local cache.
     * <p/>
     * Ordinarily, only the object returned from the server response (The AbstractExtendableInstanceResource) is cached.
     * When updating objects, any nested objects are not returned from the server, so we have to do this preemptively
     * ourselves.
     * <p/>
     * The preemtive insert on save is more efficient than, say, adding an expand=customData query parameter to the
     * href when issuing the request: the returned customData might be huge (up to 10 Megabytes), so by pre-emptively
     * caching upon a successful parent save, we avoid pulling across custom data across the wire for only caching
     * purposes.
     *
     * @param directoryEntityHref the href of the directory entity is being was saved
     * @param props               the directory entity's properties being saved
     * @since 0.9.2
     */
    @SuppressWarnings("unchecked")
    private void cacheNestedCustomData(String directoryEntityHref, Map<String, Object> props) {
        Map<String, Object> customData =
            (Map<String, Object>) props.get(AbstractExtendableInstanceResource.CUSTOM_DATA.getName());

        if (customData != null) {
            customData.remove(AbstractResource.HREF_PROP_NAME); //we remove it here for ordering reasons (see below)
        }

        if (Collections.isEmpty(customData)) {
            return;
        }

        Map<String, Object> customDataToCache = new LinkedHashMap<String, Object>();
        String customDataHref = directoryEntityHref + "/customData";
        customDataToCache.put(AbstractResource.HREF_PROP_NAME, customDataHref); //ensure first in order

        Map<String, ?> existingCustomData = getCachedValue(customDataHref, CustomData.class);
        if (!Collections.isEmpty(existingCustomData)) {
            existingCustomData.remove(AbstractResource.HREF_PROP_NAME);
            customDataToCache.putAll(existingCustomData); //put what already exists first
        }
        customDataToCache.putAll(customData); //overwrite or add what was specified during the save operation

        cache(CustomData.class, customDataToCache, null);
    }

    /* =====================================================================
       Resource Deletion
       ===================================================================== */

    @Override
    public <T extends Resource> void delete(T resource) {
        doDelete(resource, null);
    }

    @Override
    public <T extends Resource> void deleteResourceProperty(T resource, String propertyName) {
        Assert.hasText(propertyName, "propertyName cannot be null or empty.");
        doDelete(resource, propertyName);
    }

    private <T extends Resource> void doDelete(T resource, String possiblyNullPropertyName) {

        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource, "Resource argument must be an AbstractResource.");

        AbstractResource abstractResource = (AbstractResource) resource;
        uncache(abstractResource);

        String href = abstractResource.getHref();

        if (Strings.hasText(possiblyNullPropertyName)) { //delete just that property, not the entire resource:
            href = href + "/" + possiblyNullPropertyName;
        }

        Request request = new DefaultRequest(HttpMethod.DELETE, href);

        //no need to marshal the response body since DELETE responses are HTTP 204 and do not return body content:
        execute(request);
    }

    /* =====================================================================
       Resource Caching
       ===================================================================== */

    /**
     * @since 0.8
     */
    public boolean isCachingEnabled() {
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

    private boolean isCacheRetrievalEnabled(ResourceDataRequest request) {
        return isCacheRetrievalEnabled(request.getResourceClass());
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("UnusedParameters")
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
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data, QueryString queryString) {
        if (!isCachingEnabled()) {
            return;
        }

        Assert.notEmpty(data, "Resource data cannot be null or empty.");
        String href = (String) data.get(AbstractResource.HREF_PROP_NAME);

        if (isDirectlyCacheable(clazz, data)) {
            Assert.notNull(href, "Resource data must contain an '" + AbstractResource.HREF_PROP_NAME + "' attribute.");
            Assert.isTrue(data.size() > 1, "Resource data must be materialized to be cached (need more than just an '" +
                                           AbstractResource.HREF_PROP_NAME + "' attribute).");
        }

        Map<String, Object> toCache = cacheMapInitializer.initialize(clazz, data, queryString);

        if (CustomData.class.isAssignableFrom(clazz)) {
            Cache cache = getCache(clazz);
            cache.put(href, toCache);
            return;
        }

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            boolean isDefaultModelMap =
                ModeledEmailTemplate.class.isAssignableFrom(clazz) && name.equals("defaultModel");
            //Since defaultModel is a map, the DataStore thinks it is a Resource. This causes the code to crash later one as Resources
            //do need to have an href property
            if (isDefaultModelMap) {
                value = new LinkedHashMap<String, Object>((Map) value);
            }

            if (value instanceof Map && !isDefaultModelMap) {
                //the value is a resource reference
                Map<String, ?> nested = (Map<String, ?>) value;

                Assert.notEmpty(nested, "Resource references are expected to be complex objects with at least an '" +
                                        AbstractResource.HREF_PROP_NAME + "' property.");
                Assert.notNull(nested.get(AbstractResource.HREF_PROP_NAME),
                               "Resource references must have an '" + AbstractResource.HREF_PROP_NAME + "' attribute.");

                if (AbstractResource.isMaterialized(nested)) {
                    //If there is more than one attribute (more than just 'href') it is not just a simple reference
                    //anymore - it has been materialized to its full set of attributes.  Because we have a full
                    //materialized resource, we need to recursively cache it (and any of its referenced materialized
                    //resources) and so on.

                    //find the type of object this attribute name represents:
                    Property property = getPropertyDescriptor(clazz, name);
                    Assert.isTrue(property instanceof ResourceReference,
                                  "It is expected that only ResourceReference properties are complex objects.");

                    //cache this materialized reference:
                    cache(property.getType(), nested, queryString);

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
                        if (AbstractResource.isMaterialized(referenceData)) {
                            cache(itemType, referenceData, queryString);
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
    private <T extends Resource> Property getPropertyDescriptor(Class<T> clazz, String propertyName) {
        Map<String, Property> descriptors = getPropertyDescriptors(clazz);
        return descriptors.get(propertyName);
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private <T extends Resource> Map<String, Property> getPropertyDescriptors(Class<T> clazz) {
        Class<T> implClass = DefaultResourceFactory.getImplementationClass(clazz);
        try {
            Field field = implClass.getDeclaredField("PROPERTY_DESCRIPTORS");
            field.setAccessible(true);
            return (Map<String, Property>) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Unable to access PROPERTY_DESCRIPTORS static field on implementation class " + clazz.getName(), e);
        }
    }

    /**
     * @since 0.8
     */
    private Map<String, ?> getCachedValue(String href, Class<? extends Resource> clazz) {

        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(clazz, "Class argument cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(clazz);

        Map<String, ?> cachedValue = cache.get(href);

        cachedValue = resourceDataFilterProcessor.process(clazz, cachedValue);

        return cachedValue;
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private <T extends Resource> void uncache(T resource) {
        Assert.notNull(resource, "Resource argument cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(resource.getClass());
        String href = resource.getHref();
        cache.remove(href);
    }

    /**
     * @since 0.8
     */
    public <T> Cache<String, Map<String, ?>> getCache(Class<T> clazz) {
        Assert.notNull(clazz, "Class argument cannot be null.");
        String cacheRegionName = this.cacheRegionNameResolver.getCacheRegionName((Class) clazz);
        return this.cacheManager.getCache(cacheRegionName);
    }

    private LinkedHashMap<String, Object> toMap(final AbstractResource resource, boolean partialUpdate) {

        Set<String> propNames;

        if (partialUpdate) {
            propNames = resource.getUpdatedPropertyNames();
        } else {
            propNames = resource.getPropertyNames();
        }

        LinkedHashMap<String, Object> props = new LinkedHashMap<String, Object>(propNames.size());

        for (String propName : propNames) {
            Object value = resource.getProperty(propName);
            value = toMapValue(resource, propName, value, partialUpdate);
            props.put(propName, value);
        }

        return props;
    }

    //since 0.9.2
    private Object toMapValue(final AbstractResource resource, final String propName, Object value,
                              boolean partialUpdate) {
        if (resource instanceof CustomData) {
            //no sanitization: CustomData resources retain their values as-is:
            return value;
        }

        if (value instanceof CustomData || value instanceof ProviderData || value instanceof Provider) {
            if (partialUpdate) {
                Assert.isInstanceOf(AbstractResource.class, value);

                AbstractResource abstractResource = (AbstractResource) value;
                Set<String> updatedPropertyNames = abstractResource.getUpdatedPropertyNames();

                LinkedHashMap<String, Object> properties =
                    new LinkedHashMap<String, Object>(Collections.size(updatedPropertyNames));

                for (String updatedCustomPropertyName : updatedPropertyNames) {
                    Object object = abstractResource.getProperty(updatedCustomPropertyName);
                    properties.put(updatedCustomPropertyName, object);
                }

                value = properties;
            }

            return value;
        }

        if (value instanceof Map) {
            //Since defaultModel is a map, the DataStore thinks it is a Resource. This causes the code to crash later one as Resources
            //do need to have an href property
            if (resource instanceof ModeledEmailTemplate && propName.equals("defaultModel")) {
                return value;
            } else {
                //if the property is a reference, don't write the entire object - just the href will do:
                //TODO need to change this to write the entire object because this code defeats the purpose of entity expansion
                //     when this code gets called (returning the reference instead of the whole object that is returned from Stormpath)
                return this.referenceFactory.createReference(propName, (Map) value);
            }
        }

        if (value instanceof Resource) {
            return this.referenceFactory.createReference(propName, (Resource) value);
        }

        return value;
    }

    /**
     * @since 1.0.beta
     */
    private Response execute(Request request) throws ResourceException {

        applyDefaultRequestHeaders(request);

        Response response = this.requestExecutor.executeRequest(request);
        log.trace("Executed HTTP request.");

        if (response.isError()) {
            Map<String, Object> body = getBody(response);
            DefaultError error = new DefaultError(body);
            throw new ResourceException(error);
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getBody(Response response) {

        Assert.notNull(response, "response argument cannot be null.");

        Map<String, Object> out = null;

        if (response.hasBody()) {
            String bodyString = toString(response.getBody());
            log.trace("Obtained response body: \n{}", bodyString);
            if (Strings.hasText(bodyString)) {
                out = mapMarshaller.unmarshal(bodyString);
            }
        }

        return out;
    }

    protected void applyDefaultRequestHeaders(Request request) {
        request.getHeaders().setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        request.getHeaders().set("User-Agent", USER_AGENT_STRING);
        if (request.getBody() != null) {
            //this data store currently only deals with JSON messages:
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }
    }

    protected CanonicalUri canonicalize(String href, Map<String,?> queryParams) {
        href = ensureFullyQualified(href);
        return DefaultCanonicalUri.create(href, queryParams);
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

        if (href == null || href.length() < 5) {
            return false;
        }

        char c = href.charAt(0);
        if (c == 'h' || c == 'H') {
            c = href.charAt(1);
            if (c == 't' || c == 'T') {
                c = href.charAt(2);
                if (c == 't' || c == 'T') {
                    c = href.charAt(3);
                    if (c == 'p' || c == 'P') {
                        return true;
                    }
                }
            }
        }

        return false;
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
            log.trace("Response body input stream did not contain any content.", e);
            return null;
        }
    }

    /**
     * @since 1.0.RC
     */
    private boolean isApiKeyCollectionQuery(Class clazz, QueryString qs) {
        return ApiKeyList.class.isAssignableFrom(clazz) && qs != null && qs.containsKey(ID.getName());
    }

    private boolean isApiKeyCollectionQuery(ResourceDataRequest request) {
        return ApiKeyList.class.isAssignableFrom(request.getResourceClass()) &&
               request.getUri().hasQuery() && request.getUri().getQuery().containsKey(ID.getName());
    }

    /**
     * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/47. Data map is now shared among all Resource
     * instances referencing the same Href.
     *
     * @since 1.0.RC3
     */
    @SuppressWarnings({ "SuspiciousMethodCalls", "unchecked" })
    private Enlistment toEnlistment(final Map<String, ?> data) {

        Assert.notEmpty(data, "data cannot be null or empty.");
        String href = (String)data.get("href");
        Assert.hasText(href, "href cannot be null or empty.");

        Map modified = new LinkedHashMap<String, Object>(data.size());

        //since 1.0.RC4.3 - need to recursively add enlistments if the data is expanded:
        for(Object o : data.entrySet()) {
            Map.Entry entry = (Map.Entry)o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map && AbstractInstanceResource.isInstanceResource((Map<String, ?>) value)) {
                value = toEnlistment((Map<String, ?>)value);
            }
            modified.put(key, value);
        }

        Enlistment enlistment;
        if (this.hrefMapStore.containsKey(href)) {
            enlistment = this.hrefMapStore.get(href);
            enlistment.setProperties((Map<String, Object>) modified);
        } else {
            enlistment = new Enlistment((Map<String, Object>) modified);
            this.hrefMapStore.put(href, enlistment);
        }

        return enlistment;
    }
}
