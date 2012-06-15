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
package com.stormpath.sdk.ds;

import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * @since 0.1
 */
public interface DataStore {

    /**
     * Instantiates and returns a new instance of the specified Resource type.  The instance is merely instantiated
     * and is not saved/synchronized with the server in any way.
     * <p/>
     * This method effectively replaces the {@code new} keyword that would have been used otherwise if the concrete
     * implementation was known (implementation classes are intentionally not exposed to SDK end-users).
     *
     * @param clazz the Resource class to instantiate.
     * @param <T>   the Resource sub-type
     * @return a new instance of the specified Resource.
     */
    <T extends Resource> T instantiate(Class<T> clazz);

    <T extends Resource> T instantiate(Class<T> clazz, Map<String,Object> properties);

    <T extends Resource> T load(String href, Class<T> clazz);

    <T extends Resource> T create(String parentHref, T resource);

    <T extends Resource, R extends Resource> R create(String parentHref, T resource, Class<? extends R> returnType);

}
