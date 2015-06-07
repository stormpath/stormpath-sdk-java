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
package com.stormpath.sdk.impl.ds.api;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.impl.api.ApiKeyParameter;
import com.stormpath.sdk.impl.api.DefaultApiKey;
import com.stormpath.sdk.impl.ds.DefaultResourceDataResult;
import com.stormpath.sdk.impl.ds.Filter;
import com.stormpath.sdk.impl.ds.FilterChain;
import com.stormpath.sdk.impl.ds.ResourceAction;
import com.stormpath.sdk.impl.ds.ResourceDataRequest;
import com.stormpath.sdk.impl.ds.ResourceDataResult;
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService;
import com.stormpath.sdk.impl.security.EncryptionService;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DecryptApiKeySecretFilter implements Filter {

    private static String ENCRYPTION_KEY_SALT = ApiKeyParameter.ENCRYPTION_KEY_SALT.getName();
    private static String ENCRYPTION_KEY_SIZE = ApiKeyParameter.ENCRYPTION_KEY_SIZE.getName();
    private static String ENCRYPTION_KEY_ITERATIONS = ApiKeyParameter.ENCRYPTION_KEY_ITERATIONS.getName();
    private static String ENCRYPTION_METADATA = ApiKeyParameter.ENCRYPTION_METADATA.getName();

    private final ApiKey clientApiKey;

    private final String SECRET_PROPERTY_NAME = DefaultApiKey.SECRET.getName();

    public DecryptApiKeySecretFilter(ApiKey clientApiKey) {
        Assert.notNull(clientApiKey);
        this.clientApiKey = clientApiKey;
    }

    public Map<String, ?> filter(Class clazz, Map<String, ?> resourceProperties) {

//        if (clazz != null && (ApiKey.class.isAssignableFrom(clazz) || ApiKeyList.class.isAssignableFrom(clazz)) &&
//                resourceProperties != null && queryString != null && queryString.containsKey(ENCRYPT_SECRET.getName()) &&
//                Boolean.valueOf(queryString.get(ENCRYPT_SECRET.getName())) &&
//                queryString.containsKey(ENCRYPTION_KEY_SALT.getName())) {
//
//            ApiKeySecretEncryptionService.Builder builder =
//                    new ApiKeySecretEncryptionService.Builder().setPassword(password).setIterations(
//                            Integer.valueOf(queryString.get(ENCRYPTION_KEY_ITERATIONS.getName()))).setKeySize(
//                            Integer.valueOf(queryString.get(ENCRYPTION_KEY_SIZE.getName()))).setBase64Salt(
//                            queryString.get(ENCRYPTION_KEY_SALT.getName()).getBytes());
//
//            String itemsName = ITEMS_PROPERTY_NAME;
//            String secretName = "secret";
//            if (resourceProperties.containsKey(itemsName) && resourceProperties
//                    .get(itemsName) instanceof Collection) { // if we get here, we're working with a collection of api keys
//
//                Collection apiKeys = (Collection) resourceProperties.get(itemsName);
//
//                for (Object apiKeyObj : apiKeys) {
//
//                    if (apiKeyObj instanceof Map) {
//
//                        Map<String, Object> apiKeyMap = (Map<String, Object>) apiKeyObj;
//
//                        if (apiKeyMap.containsKey(secretName)) {
//
//                            String unEncryptedSecret =
//                                    builder.build().decryptBase64String((String) apiKeyMap.get(secretName));
//                            apiKeyMap.put(secretName, unEncryptedSecret);
//                        }
//                    }
//                }
//
//                // returning the collection resource with its api keys secrets unencrypted
//                return resourceProperties;
//            }
//            // if we get here, this is a single api key
//            String unEncryptedSecret = builder.build().decryptBase64String((String) resourceProperties.get(secretName));
//
//            Map<String, Object> apiKeyProperties = new LinkedHashMap<String, Object>(resourceProperties);
//            apiKeyProperties.put(secretName, unEncryptedSecret);
//
//            return apiKeyProperties;
//        }
//
        return null;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = chain.filter(request);

        if (request.getAction() == ResourceAction.DELETE) {
            return result;
        }

        Class<? extends Resource> clazz = result.getResourceClass();

        if (!(ApiKey.class.isAssignableFrom(clazz) || ApiKeyList.class.isAssignableFrom(clazz))) {
            return result;
        }

        Map<String, Object> data = result.getData();

        if (!data.containsKey(ENCRYPTION_METADATA)) {
            return result;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) data.get(ENCRYPTION_METADATA);

        byte[] base64Salt = ((String) metadata.get(ENCRYPTION_KEY_SALT)).getBytes();
        Integer iterations = (Integer) metadata.get(ENCRYPTION_KEY_ITERATIONS);
        Integer size = (Integer) metadata.get(ENCRYPTION_KEY_SIZE);

        EncryptionService service = new ApiKeySecretEncryptionService.Builder().setPassword(clientApiKey.getSecret().toCharArray()).setKeySize(size)
                .setIterations(iterations).setBase64Salt(base64Salt).build();

        String encryptedSecret = (String) data.get(SECRET_PROPERTY_NAME);

        Map<String, Object> clonedData = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {

            String key = entry.getKey();

            if (key.equals(ENCRYPTION_METADATA)) {
                continue;
            }

            if (key.equals(SECRET_PROPERTY_NAME)) {
                clonedData.put(key, service.decryptBase64String(encryptedSecret));
                continue;
            }

            clonedData.put(key, entry.getValue());
        }

        return new DefaultResourceDataResult(result.getAction(), result.getUri(), clazz, clonedData);
    }

}
