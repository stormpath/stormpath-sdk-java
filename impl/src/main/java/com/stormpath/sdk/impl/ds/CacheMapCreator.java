/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.impl.ds.api.ApiKeyCacheMapCreator;
import com.stormpath.sdk.impl.ds.directory.CustomDataCacheMapCreator;

import java.util.Map;

/**
 * <p>
 *     This interface defines the method used to create a cache map.
 * </p>
 * @since 1.1.beta
 * @see ApiKeyCacheMapCreator
 * @see CustomDataCacheMapCreator
 * @see DefaultCacheMapCreator
 */
public interface CacheMapCreator {

    /**
     * Creates a cache map.
     *
     * @return the created cache map.
     *
     * @see ApiKeyCacheMapCreator#create()
     * @see CustomDataCacheMapCreator#create()
     * @see DefaultCacheMapCreator#create()
     */
    Map<String, Object> create();
}
