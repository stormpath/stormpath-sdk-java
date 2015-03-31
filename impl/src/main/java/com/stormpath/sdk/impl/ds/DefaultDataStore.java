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
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.QueryStringFactory;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.http.Response;
import com.stormpath.sdk.impl.http.support.DefaultRequest;
import com.stormpath.sdk.impl.http.support.UserAgent;
import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
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
import java.util.Arrays;
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

    private final RequestExecutor requestExecutor;
    private final ResourceFactory resourceFactory;
    private final MapMarshaller mapMarshaller;
    private volatile CacheManager cacheManager;
    private volatile CacheRegionNameResolver cacheRegionNameResolver;
    private final ApiKey apiKey;
    private final PropertiesFilterProcessor resourceDataFilterProcessor;
    private final PropertiesFilterProcessor queryStringFilterProcessor;
    private volatile Map<String, Enlistment> hrefMapStore;

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
        this.cacheManager = new DisabledCacheManager(); //disabled by default - end-user must explicitly configure caching
        this.cacheRegionNameResolver = new DefaultCacheRegionNameResolver();
        this.referenceFactory = new ReferenceFactory();
        this.hrefMapStore = new SoftHashMap<String, Enlistment>();
        this.apiKey = apiKey;
        this.cacheMapInitializer = new DefaultCacheMapInitializer();
        this.resourceDataFilterProcessor = new DefaultPropertiesFilterProcessor(Collections.toList(new ApiKeyCachePropertiesFilter(apiKey)));
        // Adding another processor for query strings because we don't want to mix
        // the processing (filtering) of the query strings with the processing of the resource properties,
        // even though they're both (resource properties and query string objects) Maps that might apply
        // to the be added to the same filter. This separation also improves requests performance.
        this.queryStringFilterProcessor = new DefaultPropertiesFilterProcessor(Collections.toList(new ApiKeyQueryPropertiesFilter()));
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

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

        Map<String, ?> data = retrieveResponseValue(href, clazz, qs);

        //@since 1.0.RC3
        if (!Collections.isEmpty(data) && !CollectionResource.class.isAssignableFrom(clazz) && data.get("href") != null) {
            data = toEnlistment(data);
        }

        if (CollectionResource.class.isAssignableFrom(clazz)) {
            //only collections can support a query string constructor argument:
            return this.resourceFactory.instantiate(clazz, data, qs);
        }
        //otherwise it must be an instance resource, so use the two-arg constructor:
        return this.resourceFactory.instantiate(clazz, data);
    }

    /**
     * This method provides the ability to instruct the DataStore how to decide which class of a resource hierarchy
     * will be instantiated. For example, nowadays three {@link ProviderData} resources exists (ProviderData, FacebookProviderData and
     * GoogleProviderData). The <code>childIdProperty</code> is the property that will be used in the response as the ID to seek
     * for the proper concrete ProviderData class in the <code>idClassMap</>.
     *
     * @param href the endpoint where the request will be targeted to.
     * @param parent the root class of the Resource hierarchy (helps to validate that the idClassMap contains subclasses of it).
     * @param childIdProperty the property whose value will be used to identify the specific class in the hierarchy that we need to instantiate.
     * @param idClassMap a mapping to be able to know which class corresponds to each <code>childIdProperty</code> value.
     * @param <T> the root of the hierarchy of the Resource we want to instantiate.
     * @param <R> the sub-class of the root Resource.
     * @return
     */
    @Override
    public <T extends Resource, R extends T> R getResource(String href, Class<T> parent, String childIdProperty, Map<String, Class<? extends R>> idClassMap) {
        Assert.hasText(href, "href argument cannot be null or empty.");
        Assert.notNull(parent, "parent class argument cannot be null.");
        Assert.hasText(childIdProperty, "childIdProperty cannot be null or empty.");
        Assert.notEmpty(idClassMap, "idClassMap cannot be null or empty.");

        SanitizedQuery sanitized = QuerySanitizer.sanitize(href, null);

        return getResource(sanitized.getHrefWithoutQuery(), parent, sanitized.getQuery(), childIdProperty, idClassMap);
    }

    private <T extends Resource, R extends T> R getResource(String href, Class<T> parent, QueryString qs, String childIdProperty, Map<String, Class<? extends R>> idClassMap) {
        //need to qualify the href it to ensure our cache lookups work as expected
        //(cache key = fully qualified href):
        href = ensureFullyQualified(href);

        Map<String, ?> data = retrieveResponseValue(href, parent, qs);

        //@since 1.0.RC3
        if (!Collections.isEmpty(data) && data.get("href") != null && !CollectionResource.class.isAssignableFrom(parent)) {
            data = toEnlistment(data);
        }

        if (Collections.isEmpty(data)) {
            throw new IllegalStateException(childIdProperty + " could not be found in: " + data + ".");
        }

        Object childClassName = data.get(childIdProperty);
        Class<? extends R> childClass = idClassMap.get(childClassName);

        if(childClass == null) {
            throw new IllegalStateException("No Class mapping could be found for " + childIdProperty + ".");
        }

        if (CollectionResource.class.isAssignableFrom(childClass)) {
            //only collections can support a query string constructor argument:
            return this.resourceFactory.instantiate(childClass, data, qs);
        }
        //otherwise it must be an instance resource, so use the two-arg constructor:
        return this.resourceFactory.instantiate(childClass, data);
    }

    private Map<String, ?> retrieveResponseValue(String href, Class clazz, QueryString qs) {

        QueryString filteredQs = (QueryString) queryStringFilterProcessor.process(clazz, qs);
        Map<String, ?> data = null;
        if (isCacheRetrievalEnabled(clazz) || isApiKeyCollectionQuery(clazz, filteredQs)) {

            if (isApiKeyCollectionQuery(clazz, filteredQs)) {
                String cacheHref = new String(baseUrl + "/apiKeys/" + filteredQs.get(ID.getName()));
                Class cacheClass = com.stormpath.sdk.api.ApiKey.class;

                Map apiKeyData = getCachedValue(cacheHref, cacheClass);

                if (!Collections.isEmpty(apiKeyData)) {
                    CollectionProperties.Builder builder = new CollectionProperties.Builder()
                            .setHref(href)
                            .setOffset(filteredQs.containsKey(OFFSET.getName()) ? Integer.valueOf(filteredQs.get(OFFSET.getName())) : 0)
                            .setLimit(filteredQs.containsKey(LIMIT.getName()) ? Integer.valueOf(filteredQs.get(LIMIT.getName())) : 25)
                            .setItemsMap(apiKeyData);
                    data = builder.build();
                }
            } else {
                data = getCachedValue(href, clazz);
            }
        }

        Map<String, ?> returnResponseBody = data;
        if (Collections.isEmpty(data)) {
            //not cached - execute a request:
            Request request = createRequest(HttpMethod.GET, href, filteredQs);
            data = executeRequest(request);

            if (!Collections.isEmpty(data) && isCacheUpdateEnabled(clazz)) {
                //cache for further use:
                cache(clazz, data, filteredQs);
            }

            // Adding the ApiKeyResourcePropertiesFilter here because if the resource was cached, the ApiKeyCachePropertiesFilter
            // already took care of decrypting the api key secret to return to the user.
            // Transitory filters serve the purpose of filtering the resource properties to return to the user,
            // based on the current request.
            // For example: decrypting the api key secret to return to the user
            // with the current request content (query strings, etc.); which is why they are transitory, because they cannot
            // be added when initializing the filter (they depend on the current request).
            List<PropertiesFilter> resourceDataFilters = resourceDataFilterProcessor.getFilters();
            List<PropertiesFilter> filters = new ArrayList<PropertiesFilter>(resourceDataFilters);
            filters.add(new ApiKeyResourcePropertiesFilter(apiKey, filteredQs));
            PropertiesFilterProcessor processor = new DefaultPropertiesFilterProcessor(filters);
            returnResponseBody = processor.process(clazz, data);
        }

        return returnResponseBody;
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

        //@since 1.0.RC3
        if (!Collections.isEmpty(props) && !CollectionResource.class.isAssignableFrom(clazz) && props.get("href") != null) {
            in.setProperties(toEnlistment(props));
        } else {
            in.setProperties(props);
        }

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

        //@since 1.0.RC3
        if (!Collections.isEmpty(props) && !CollectionResource.class.isAssignableFrom(clazz) && props.get("href") != null) {
            in.setProperties(toEnlistment(props));
        } else {
            in.setProperties(props);
        }

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

    private <T extends Resource, R extends Resource> R save(String href, T resource, Class<? extends R> returnType, QueryString qs) {
        Assert.notNull(resource, "resource argument cannot be null.");
        Assert.notNull(returnType, "returnType class cannot be null.");
        Assert.isInstanceOf(AbstractResource.class, resource);

        href = ensureFullyQualified(href);

        AbstractResource abstractResource = (AbstractResource) resource;

        LinkedHashMap<String, Object> props = toMap(abstractResource, true);

        String bodyString = mapMarshaller.marshal(props);

        StringInputStream body = new StringInputStream(bodyString);
        long length = body.available();

        QueryString filteredQs = (QueryString) queryStringFilterProcessor.process(returnType, qs);
        Request request = new DefaultRequest(HttpMethod.POST, href, filteredQs, null, body, length);

        Response response = executeRequestGetFullResponse(request);
        Map<String, Object> responseBody = getBodyFromSuccessfulResponse(response);

        //since 1.0.beta: provider's account creation status (whether it is new or not) is returned in the HTTP response
        //status. The resource factory does not provide a way to pass such information when instantiating a resource. Thus,
        //after the resource has been instantiated we are going to manipulate it before returning it in order to set the
        //"is new" status
        int responseStatus = response.getHttpStatus();
        if (ProviderAccountResult.class.isAssignableFrom(returnType) && (responseStatus == 200 || responseStatus == 201)) {
            if(responseStatus == 200) { //is not a new account
                responseBody.put("isNewAccount", false);
            } else {
                responseBody.put("isNewAccount", true);
            }
        }

        // Filter resource properties to return to the user, based on the current request.
        // For example: decrypting the api key secret to return to the user
        // with the current request content (query strings, etc.); which is why they are transitory, because they cannot
        // be added when initializing the filter (they depend on the current request).
        List<PropertiesFilter> resourceDataFilters = resourceDataFilterProcessor.getFilters();
        List<PropertiesFilter> filters = new ArrayList<PropertiesFilter>(resourceDataFilters);
        filters.add(new ApiKeyResourcePropertiesFilter(apiKey, filteredQs));
        PropertiesFilterProcessor processor = new DefaultPropertiesFilterProcessor(filters);
        Map<String,?> returnResponseBody = processor.process(returnType, responseBody);

        if (Collections.isEmpty(responseBody)) {
            return null;
        }

        //asserts invariant given that we should have returned if the responseBody is null or empty:
        assert responseBody != null && !responseBody.isEmpty() : "Response body must be non-empty.";

        //since 1.0.RC3 RC: emailVerification boolean hack. See: https://github.com/stormpath/stormpath-sdk-java/issues/60
        boolean emailVerification = resource instanceof EmailVerificationToken && returnType.equals(Account.class);
        //since 1.0.RC4 : fix for https://github.com/stormpath/stormpath-sdk-java/issues/140 where Account remains disabled after
        //successful verification due to an outdated `Account` state in the cache.
        if (emailVerification && isCachingEnabled()) {
            Cache cache = getCache(Account.class);
            String accountHref = (String) responseBody.get(HREF_PROP_NAME);
            if (Strings.hasText(accountHref)) {
                cache.remove(accountHref);
            }
        }

        if (isCacheUpdateEnabled(returnType) && !emailVerification) {
            //@since 1.0.RC3: Let's first check if the response is an actual Resource (meaning, that it has an href property)
            if (Strings.hasText((String)returnResponseBody.get(HREF_PROP_NAME))) {
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
        if (!Collections.isEmpty(returnResponseBody) && returnResponseBody.get("href") != null) {
            returnResponseBody = toEnlistment(returnResponseBody);
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
        Map<String, Object> customData = (Map<String, Object>) props.get(AbstractExtendableInstanceResource.CUSTOM_DATA.getName());

        if (customData != null) {
            customData.remove(AbstractResource.HREF_PROP_NAME); //we remove it here for ordering reasons (see below)
        }

        if (Collections.isEmpty(customData)) {
            return;
        }

        assert customData != null;

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
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data, QueryString queryString) {
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

        Map<String, Object> toCache = cacheMapInitializer.initialize(clazz, data, queryString);

        if (CustomData.class.isAssignableFrom(clazz)) {
            Cache cache = getCache(clazz);
            cache.put(href, toCache);
            return;
        }

        for (Map.Entry<String, ?> entry : data.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            boolean isDefaultModelMap = ModeledEmailTemplate.class.isAssignableFrom(clazz) && name.equals("defaultModel");
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
                        if (isMaterialized(referenceData)) {
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
    private boolean isMaterialized(Map<String, ?> props) {
        return props != null && props.get(AbstractResource.HREF_PROP_NAME) != null && props.size() > 1;
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
        Cache cache = getCache(resource.getClass());
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
    private Object toMapValue(final AbstractResource resource, final String propName, Object value, boolean partialUpdate) {
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

    private Request createRequest(HttpMethod method, String href, Map<String, ?> queryParams) {
        Assert.notNull(href, "href argument cannot be null.");
        href = ensureFullyQualified(href);
        QueryString qs = queryStringFactory.createQueryString(queryParams);
        return new DefaultRequest(method, href, qs);
    }

    /**
     * @since 1.0.beta
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> executeRequest(Request request) {
        Response response = executeRequestGetFullResponse(request);
        return getBodyFromSuccessfulResponse(response);
    }

    /**
     * @since 1.0.beta
     */
    private Response executeRequestGetFullResponse(Request request) {
        applyDefaultRequestHeaders(request);

        Response response = this.requestExecutor.executeRequest(request);
        log.trace("Executed HTTP request.");

        if (response.isError()) {
            String body;
            Map<String, Object> mapBody = null;
            if (response.hasBody()) {
                body = toString(response.getBody());
                log.trace("Obtained response body: \n{}", body);
                mapBody = mapMarshaller.unmarshal(body);
            }
            DefaultError error = new DefaultError(mapBody);
            throw new ResourceException(error);
        }

        return response;
    }

    /**
     * @since 1.0.beta
     */
    private Map<String, Object> getBodyFromSuccessfulResponse(Response response) {
        String body = null;

        if (response.hasBody()) {
            body = toString(response.getBody());
        }

        Map<String, Object> mapBody = null;

        if (body != null) {
            log.trace("Obtained response body: \n{}", body);
            mapBody = mapMarshaller.unmarshal(body);
        }

        return mapBody;
    }


    protected void applyDefaultRequestHeaders(Request request) {
        request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        request.getHeaders().set("User-Agent", USER_AGENT_STRING);
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

    /**
     *
     * @since 1.0.RC
     */
    private boolean isApiKeyCollectionQuery(Class clazz, QueryString qs) {
        return ApiKeyList.class.isAssignableFrom(clazz) && qs != null && qs.containsKey(ID.getName());
    }

    /**
     * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/47. Data map is now shared among all
     * Resource instances referencing the same Href.
     * @since 1.0.RC3
     */
    private Enlistment toEnlistment(Map data) {
        Enlistment enlistment;
        Object responseHref = data.get("href");
        if (this.hrefMapStore.containsKey(responseHref)) {
            enlistment = this.hrefMapStore.get(responseHref);
            enlistment.setProperties((Map<String, Object>) data);
        } else {
            enlistment = new Enlistment((Map<String, Object>) data);
            this.hrefMapStore.put((String) responseHref, enlistment);
        }
        return enlistment;
    }
}
