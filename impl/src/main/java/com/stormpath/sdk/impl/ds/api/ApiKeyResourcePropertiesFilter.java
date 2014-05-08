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

import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.impl.ds.ResourcePropertiesFilter;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;
import static com.stormpath.sdk.impl.resource.AbstractCollectionResource.ITEMS_PROPERTY_NAME;

/**
 * @since 1.1.beta
 */
public class ApiKeyResourcePropertiesFilter implements ResourcePropertiesFilter {

    private final ApiKey apiKey;

    private final QueryString queryString;

    public ApiKeyResourcePropertiesFilter(ApiKey apiKey, QueryString queryString) {
        this.apiKey = apiKey;
        this.queryString = queryString;
    }

    @Override
    public Map<String, ?> filter(Map<String, ?> resourceProperties) {

        if (queryString != null
                && queryString.containsKey(ENCRYPT_SECRET.getName())
                && Boolean.valueOf(queryString.get(ENCRYPT_SECRET.getName()))
                && queryString.containsKey(ENCRYPTION_KEY_SALT.getName())) {

            ApiKeySecretEncryptionService.Builder builder = new ApiKeySecretEncryptionService.Builder()
                    .setPassword(apiKey.getSecret().toCharArray())
                    .setIterations(Integer.valueOf(queryString.get(ENCRYPTION_KEY_ITERATIONS.getName())))
                    .setKeySize(Integer.valueOf(queryString.get(ENCRYPTION_KEY_SIZE.getName())))
                    .setBase64Salt(queryString.get(ENCRYPTION_KEY_SALT.getName()).getBytes());

            String itemsName = ITEMS_PROPERTY_NAME;
            String secretName = "secret";
            if (resourceProperties.containsKey(itemsName) && resourceProperties.get(itemsName) instanceof Collection) { // if we get here, we're working with a collection of api keys

                Collection apiKeys = (Collection) resourceProperties.get(itemsName);

                for (Object apiKeyObj : apiKeys) {

                    if (apiKeyObj instanceof Map) {

                        Map<String, Object> apiKeyMap =  (Map<String, Object>) apiKeyObj;

                        if (apiKeyMap.containsKey(secretName)) {

                            String unEncryptedSecret = builder.build().decryptBase64String((String) apiKeyMap.get(secretName));
                            apiKeyMap.put(secretName, unEncryptedSecret);
                            continue;

                        }
                    }
                }

                // returning the collection resource with its api keys secrets unencrypted
                return resourceProperties;
            }
            // if we get here, this is a single api key
            String unEncryptedSecret = builder.build().decryptBase64String((String) resourceProperties.get(secretName));

            Map<String, Object> apiKeyProperties = new LinkedHashMap<String, Object>(resourceProperties);
            apiKeyProperties.put(secretName, unEncryptedSecret);

            return apiKeyProperties;
        }

        return resourceProperties;
    }
}
