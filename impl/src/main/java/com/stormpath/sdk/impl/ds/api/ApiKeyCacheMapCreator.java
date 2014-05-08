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
package com.stormpath.sdk.impl.ds.api;

import com.stormpath.sdk.impl.ds.DefaultCacheMapCreator;
import com.stormpath.sdk.impl.http.QueryString;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;
import static com.stormpath.sdk.impl.ds.api.ApiKeyCacheParameter.API_KEY_META_DATA;

/**
 * @since 1.1.beta
 */
public class ApiKeyCacheMapCreator extends DefaultCacheMapCreator {

    private final QueryString queryString;

    public ApiKeyCacheMapCreator(Map<String, ?> data, QueryString queryString) {
        super(data);
        this.queryString = queryString;
    }

    @Override
    public Map<String, Object> create() {

        Map<String, Object> toCache = super.create();

        if (queryString != null
                && queryString.containsKey(ENCRYPT_SECRET.getName())
                && Boolean.valueOf(queryString.get(ENCRYPT_SECRET.getName()))
                && queryString.containsKey(ENCRYPTION_KEY_SALT.getName())) {

            toCache = new LinkedHashMap<String, Object>(getData().size() + 1);
            Map<String, Object> apiKeyMetaData = new LinkedHashMap<String, Object>();

            apiKeyMetaData.put(ENCRYPT_SECRET.getName(), true);

            if (queryString.containsKey(ENCRYPTION_KEY_SIZE.getName())) {

                apiKeyMetaData.put(ENCRYPTION_KEY_SIZE.getName(), Integer.valueOf(queryString.get(ENCRYPTION_KEY_SIZE.getName())));
            }

            if (queryString.containsKey(ENCRYPTION_KEY_ITERATIONS.getName())) {

                apiKeyMetaData.put(ENCRYPTION_KEY_ITERATIONS.getName(), Integer.valueOf(queryString.get(ENCRYPTION_KEY_ITERATIONS.getName())));
            }

            if (queryString.containsKey(ENCRYPTION_KEY_SALT.getName())) {

                apiKeyMetaData.put(ENCRYPTION_KEY_SALT.getName(), String.valueOf(queryString.get(ENCRYPTION_KEY_SALT.getName())));
            }

            toCache.put(API_KEY_META_DATA.toString(), apiKeyMetaData);

        }

        return toCache;
    }
}
