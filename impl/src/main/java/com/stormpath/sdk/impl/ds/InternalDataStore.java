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

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.ds.cache.CacheResolver;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.query.Options;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.Map;

/**
 * Internal DataStore used for implementation purposes only.  Not intended to be called by SDK end users!
 * <p/>
 * <b>WARNING: This API CAN CHANGE AT ANY TIME, WITHOUT NOTICE.  DO NOT DEPEND ON IT.</b>
 *
 * @since 0.2
 */
public interface InternalDataStore extends DataStore {

    <T extends Resource> T instantiate(Class<T> clazz, Map<String,Object> properties);

    /**
     * Instantiates and returns a new instance of the specified Resource type. The instance is merely instantiated and
     * is not saved/synchronized with the server in any way. This operation allows
     * the <code>href</code> to be a fragment, where the <code>baseUrl</code> can be missing and will be added automatically.
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @param properties the properties the instantiated resource will have
     * @param hrefFragment when <code>true</code>, the baseUrl will be appended to the value found in the href key of the properties map.
     *                     If <code>false</code> the href will not be altered and will be kept as-is.
     * @return a resource instance corresponding to the specified clazz.
     * @since 1.0.RC9
     */
    public <T extends Resource> T instantiate(Class<T> clazz, Map<String, Object> properties, boolean hrefFragment);

    <T extends Resource> T create(String parentHref, T resource);

    <T extends Resource> T create(String parentHref, T resource, Options options);

    <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType);

    /** @since 1.0.RC7 */
    <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType, HttpHeaders customHeaders);

    /** @since 1.0.RC5 */
    <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType, Options options);

    <T extends Resource & Saveable> void save(T resource);

    <T extends Resource & Saveable> void save(T resource, Options options);

    <T extends Resource & Saveable, R extends Resource> R save(T resource, Class<? extends R> returnType);

    <T extends Resource> void delete(T resource);

    <T extends Resource> void deleteResourceProperty(T resource, String propertyName);

    <T extends Resource> T getResource(String href, Class<T> clazz, Map<String,Object> queryParameters);

    <T extends Resource> T getResource(String href, Class<T> clazz, Criteria criteria);

    /**
     * @since 1.0.beta
     */
    <T extends Resource, R extends T> R getResource(String href, Class<T> parent, String childIdProperty, Map<String, Class<? extends R>> stringClassMap);

    CacheResolver getCacheResolver();

}
