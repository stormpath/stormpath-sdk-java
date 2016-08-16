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
package com.stormpath.sdk.impl.ds.cache;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.api.ApiKeyParameter;
import com.stormpath.sdk.impl.ds.CacheMapInitializer;
import com.stormpath.sdk.impl.ds.DefaultCacheMapInitializer;
import com.stormpath.sdk.impl.ds.DefaultResourceFactory;
import com.stormpath.sdk.impl.ds.FilterChain;
import com.stormpath.sdk.impl.ds.ResourceAction;
import com.stormpath.sdk.impl.ds.ResourceDataRequest;
import com.stormpath.sdk.impl.ds.ResourceDataResult;
import com.stormpath.sdk.http.QueryString;
import com.stormpath.sdk.impl.resource.AbstractExtendableInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ReferenceFactory;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.mail.ModeledEmailTemplate;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.ID;
import static com.stormpath.sdk.impl.resource.AbstractResource.HREF_PROP_NAME;

public class WriteCacheFilter extends AbstractCacheFilter {

    private final ReferenceFactory referenceFactory;
    private final CacheMapInitializer cacheMapInitializer;

    public WriteCacheFilter(CacheResolver cacheResolver, boolean collectionCachingEnabled, ReferenceFactory referenceFactory) {
        super(cacheResolver, collectionCachingEnabled);
        Assert.notNull(referenceFactory, "referenceFactory cannot be null.");
        this.referenceFactory = referenceFactory;
        this.cacheMapInitializer = new DefaultCacheMapInitializer();
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        if (request.getAction() == ResourceAction.DELETE) {
            String key = getCacheKey(request);
            uncache(key, request.getResourceClass());
        }

        ResourceDataResult result = chain.filter(request);

        if (isCacheable(request, result)) {
            cache(result.getResourceClass(), result.getData(), result.getUri().getQuery());
        }

        //since 0.9.2: custom data quick fix for https://github.com/stormpath/stormpath-sdk-java/issues/30
        //If the resource saved has nested custom data, and any custom data was specified when saving the resource,
        //we need to ensure that the custom data is cached properly since it won't be returned by the server by
        //default.  There is probably a much cleaner way of doing this, but it wasn't worth it at impl time to
        //find a smoother way.  Helper methods have been marked as private to indicate that this shouldn't be used as
        //a dependency in case we choose to implement a cleaner way later.
        if (AbstractExtendableInstanceResource.isExtendableInstanceResource(result.getData())) {
            cacheNestedCustomData(request.getUri().getAbsolutePath(), request.getData());
        }

        return result;
    }

    private boolean isCacheable(ResourceDataRequest request, ResourceDataResult result) {

        if (Collections.isEmpty(result.getData())) {
            return false;
        }

        Class<? extends Resource> clazz = result.getResourceClass();

        //since 1.0.RC3 RC: emailVerification boolean hack. See: https://github.com/stormpath/stormpath-sdk-java/issues/60
        boolean emailVerification = EmailVerificationToken.class.isAssignableFrom(request.getResourceClass()) &&
                                    Account.class.isAssignableFrom(clazz);

        //since 1.0.RC4 : fix for https://github.com/stormpath/stormpath-sdk-java/issues/140 where Account remains disabled after
        //successful verification due to an outdated `Account` state in the cache.
        if (emailVerification) {
            String accountHref = (String) result.getData().get(HREF_PROP_NAME);
            if (Strings.hasText(accountHref)) {
                Cache<String, ?> cache = getCache(Account.class);
                cache.remove(accountHref);
            }
        }

        return

            //since 1.0.RC4: uncaching boolean hack. PasswordResetToken. See: https://github.com/stormpath/stormpath-sdk-java/issues/132
            !PasswordResetToken.class.isAssignableFrom(clazz) &&

            //@since 1.0.RC3: ProviderAccountResult is both a Resource and has an href property, but it must not be cached
            !ProviderAccountResult.class.isAssignableFrom(clazz) &&

            //@since 1.0.RC3: Check if the response is an actual Resource (meaning, that it has an href property)
            AbstractResource.isMaterialized(result.getData()) &&

            //@since 1.0.RC7: Let's not cache Access Tokens
            !AccessToken.class.isAssignableFrom(clazz);
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

        //we pass 'null' in as the querystring param because the querystring is only valid for
        //the top-most item being cached - we don't want to propagate it for nested resources because the nested
        //resource wasn't acquired w/ that query string.
        cache(CustomData.class, customDataToCache, null);
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private void cache(Class<? extends Resource> clazz, Map<String, ?> data, QueryString queryString) {

        Assert.notEmpty(data, "Resource data cannot be null or empty.");
        String href = (String) data.get(AbstractResource.HREF_PROP_NAME);

        if (isDirectlyCacheable(clazz, data)) {
            Assert.notNull(href, "Resource data must contain an '" + AbstractResource.HREF_PROP_NAME + "' attribute.");
            Assert.isTrue(data.size() > 1, "Resource data must be materialized to be cached (need more than just an '" +
                                           AbstractResource.HREF_PROP_NAME + "' attribute).");
        }

        //create a map to reflect the resource's canonical representation - this is what will be cached:
        Map<String, Object> cacheValue = cacheMapInitializer.initialize(clazz, data, queryString);

        if (CustomData.class.isAssignableFrom(clazz)) {
            Cache cache = getCache(clazz);
            cache.put(href, cacheValue);
            return;
        }

        for (Map.Entry<String, ?> entry : data.entrySet()) {

            String name = entry.getKey();
            Object value = entry.getValue();

            boolean isDefaultModelMap =
                ModeledEmailTemplate.class.isAssignableFrom(clazz) && name.equals("defaultModel");

            boolean isTokenDataMap = (AccessToken.class.isAssignableFrom(clazz) || RefreshToken.class.isAssignableFrom(clazz)) && name.equals("expandedJwt");

            boolean isApiEncryptionMetadata = ApiKey.class.isAssignableFrom(clazz) && name.equals(ApiKeyParameter.ENCRYPTION_METADATA.getName());

            // Since defaultModel and Grant Authentication tokens are maps, the DataStore thinks they are Resources. This causes the code to crash later on as Resources
            // do need to have an href property
            if (isDefaultModelMap || isTokenDataMap) {
                value = new LinkedHashMap<String, Object>((Map) value);
            }

            if (value instanceof Map && !isDefaultModelMap && !isTokenDataMap && !isApiEncryptionMetadata) {
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
                    //we pass 'null' in as the querystring param because the querystring is only valid for
                    //the top-most item being cached - we don't want to propagate it for nested resources because the nested
                    //resource wasn't acquired w/ that query string.
                    cache(property.getType(), nested, null);

                    //Because the materialized reference has now been cached, we don't need to store
                    //all of its properties again in the 'toCache' instance.  Instead, we just want to store
                    //an unmaterialized reference (a Map with just the 'href' attribute).
                    //If the a caller attempts to materialize the reference, we will hit the cached version and
                    //use that data instead of issuing a request.
                    value = toCanonicalReference(name, nested);
                }
            } else if (value instanceof Collection && name.equals("items") && data.get("href") != null) { //array property, i.e. the 'items' collection resource property
                Collection c = (Collection) value;
                //Create a new collection that has only references, recursively caching any materialized references:
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
                            //we pass 'null' in as the querystring param because the querystring is only valid for
                            //the top-most item being cached - we don't want to propagate it for nested resources because the nested
                            //resource wasn't acquired w/ that query string.
                            cache(itemType, referenceData, null);
                            element = toCanonicalReference(null, referenceData);
                        }
                    }
                    list.add(element);
                }

                value = list;
            }

            if (!DefaultAccount.PASSWORD.getName().equals(name)) { //don't cache sensitive data
                cacheValue.put(name, value);
            }
        }

        if (isDirectlyCacheable(clazz, cacheValue)) {
            Cache cache = getCache(clazz);
            String cacheKey = getCacheKey(href, queryString, clazz);
            cache.put(cacheKey, cacheValue);
        }
    }


    private Map<String,?> toCanonicalReference(String name, Map<String,?> resourceData) {

        //If the resource data reflects a materialized instance resource (not a collection resource), we can convert it
        //to a link since it will cached in shared cache.  This way any time the link is resolved (across any
        //collection), the same shared cache instance data will be returned, instead of potentially having different
        //representations of the same resource in different collections.
        if (AbstractInstanceResource.isInstanceResource(resourceData)) {
            return this.referenceFactory.createReference(name, resourceData);
        }

        //Collections are not yet placed in the shared cache due to the significant challenge of coherency, so we
        // don't want to 'lose' the fidelity of the collection's properties by converting it to just a link.  So
        // we return the actual collection:
        return resourceData;
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
     * Quick fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>.
     *
     * @since 0.8.1
     */
    private boolean isDirectlyCacheable(Class<? extends Resource> clazz, Map<String, ?> data) {

        return AbstractResource.isMaterialized(data) &&

               (!CollectionResource.class.isAssignableFrom(clazz) ||
                (CollectionResource.class.isAssignableFrom(clazz) && isCollectionCachingEnabled()));
    }

    /**
     * @since 0.8
     */
    @SuppressWarnings("unchecked")
    private void uncache(String cacheKey, Class<? extends Resource> resourceType) {
        Assert.hasText(cacheKey, "cacheKey cannot be null or empty.");
        Assert.notNull(resourceType, "resourceType cannot be null.");
        Cache<String, Map<String, ?>> cache = getCache(resourceType);
        cache.remove(cacheKey);
    }

    private boolean isApiKeyCollectionQuery(ResourceDataRequest request) {
        return ApiKeyList.class.isAssignableFrom(request.getResourceClass()) &&
                request.getUri().hasQuery() && request.getUri().getQuery().containsKey(ID.getName());
    }

}
