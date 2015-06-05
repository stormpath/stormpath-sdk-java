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

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.ds.CacheMapInitializer;
import com.stormpath.sdk.impl.ds.DefaultCacheMapInitializer;
import com.stormpath.sdk.impl.ds.DefaultResourceFactory;
import com.stormpath.sdk.impl.ds.Filter;
import com.stormpath.sdk.impl.ds.FilterChain;
import com.stormpath.sdk.impl.ds.ResourceDataRequest;
import com.stormpath.sdk.impl.ds.ResourceDataResult;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ReferenceFactory;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.mail.ModeledEmailTemplate;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WriteCacheFilter implements Filter {

    private final ReferenceFactory referenceFactory;
    private final CacheResolver cacheResolver;
    private final CacheMapInitializer cacheMapInitializer;

    public WriteCacheFilter(CacheResolver cacheResolver, ReferenceFactory referenceFactory) {
        Assert.notNull(cacheResolver, "cacheResolver cannot be null.");
        Assert.notNull(referenceFactory, "referenceFactory cannot be null.");
        this.cacheResolver = cacheResolver;
        this.referenceFactory = referenceFactory;
        this.cacheMapInitializer = new DefaultCacheMapInitializer();
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = chain.filter(request);

        cache(result.getResourceClass(), result.getData(), result.getUri().getQuery());

        return result;
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

    private <T> Cache<String, Map<String, ?>> getCache(Class<T> clazz) {
        return this.cacheResolver.getCache(clazz);
    }

    /**
     * Quick fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>.
     *
     * @since 0.8.1
     */
    private boolean isDirectlyCacheable(Class<? extends Resource> clazz, Map<String, ?> data) {

        return !Collections.isEmpty(data) &&

               //Authentication results (currently) do not have an 'href' attribute, as it was not expected to support
               // GET requests.  This will be resolved within Stormpath, but this is a fix for the SDK for now (for
               // Issue #17).  They are not directly cacheable, but any materialized references they contain are:
               data.get(AbstractResource.HREF_PROP_NAME) != null &&

               //we don't cache collection resources at the moment (only the instances inside them):
               !CollectionResource.class.isAssignableFrom(clazz);
    }
}
