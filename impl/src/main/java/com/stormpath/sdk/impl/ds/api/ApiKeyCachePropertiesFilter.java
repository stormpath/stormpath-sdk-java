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
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.api.ApiKeyParameter.*;
import static com.stormpath.sdk.impl.ds.api.ApiKeyCacheParameter.API_KEY_META_DATA;

/**
 * @since 1.1.beta
 */
public class ApiKeyCachePropertiesFilter implements ResourcePropertiesFilter {

    private final ApiKey apiKey;


    public ApiKeyCachePropertiesFilter(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Map<String, ?> filter(Map<String, ?> resourceProperties) {

        String apiKeyMetaDataName = API_KEY_META_DATA.toString();
        if (resourceProperties != null
                && resourceProperties.containsKey(apiKeyMetaDataName)
                && resourceProperties.get(apiKeyMetaDataName) != null
                && Boolean.valueOf((Boolean) ((Map) resourceProperties.get(apiKeyMetaDataName)).get(ENCRYPT_SECRET.getName()))
                && ((Map) resourceProperties.get(apiKeyMetaDataName)).containsKey(ENCRYPTION_KEY_SALT.getName())) {

            Map<String, Object> apiKeyCache = new LinkedHashMap<String, Object>(resourceProperties);
            apiKeyCache.remove(apiKeyMetaDataName);

            Map<String, ?> apiKeyMetaData = (Map<String, ?>) resourceProperties.get(apiKeyMetaDataName);

            ApiKeySecretEncryptionService.Builder builder = new ApiKeySecretEncryptionService.Builder()
                    .setPassword(apiKey.getSecret().toCharArray())
                    .setIterations((Integer) apiKeyMetaData.get(ENCRYPTION_KEY_ITERATIONS.getName()))
                    .setKeySize((Integer) apiKeyMetaData.get(ENCRYPTION_KEY_SIZE.getName()))
                    .setBase64Salt(((String) apiKeyMetaData.get(ENCRYPTION_KEY_SALT.getName())).getBytes());

            String unEncryptedSecret = builder.build().decryptBase64String((String) resourceProperties.get("secret"));

            apiKeyCache.put("secret", unEncryptedSecret);

            return apiKeyCache;
        }

        return resourceProperties;
    }

}
