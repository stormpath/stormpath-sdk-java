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
import com.stormpath.sdk.impl.api.ApiKeyCredentials;
import com.stormpath.sdk.impl.api.ApiKeyParameter;
import com.stormpath.sdk.impl.api.DefaultApiKey;
import com.stormpath.sdk.impl.api.DefaultApiKeyList;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DecryptApiKeySecretFilter implements Filter {

    private static String ENCRYPTION_KEY_SALT = ApiKeyParameter.ENCRYPTION_KEY_SALT.getName();
    private static String ENCRYPTION_KEY_SIZE = ApiKeyParameter.ENCRYPTION_KEY_SIZE.getName();
    private static String ENCRYPTION_KEY_ITERATIONS = ApiKeyParameter.ENCRYPTION_KEY_ITERATIONS.getName();
    private static String ENCRYPTION_METADATA = ApiKeyParameter.ENCRYPTION_METADATA.getName();

    private final ApiKeyCredentials apiKeyCredentials;

    private final String SECRET_PROPERTY_NAME = DefaultApiKey.SECRET.getName();

    public DecryptApiKeySecretFilter(ApiKeyCredentials apiKeyCredentials) {
        Assert.notNull(apiKeyCredentials);
        this.apiKeyCredentials = apiKeyCredentials;
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

        if (DefaultApiKeyList.isCollectionResource(data)) {

            @SuppressWarnings("unchecked")
            Collection<Map<String, Object>> items = (Collection<Map<String, Object>>) data.get(DefaultApiKeyList.ITEMS_PROPERTY_NAME);

            if (items.isEmpty()) {
                return result;
            }

            List<Map<String, Object>> clonedItems = new ArrayList<Map<String, Object>>(items.size());

            for (Map<String, Object> item : items) {
                clonedItems.add(clone(item));
            }

            data.put(DefaultApiKeyList.ITEMS_PROPERTY_NAME, clonedItems);

            return result;
        }

        return new DefaultResourceDataResult(result.getAction(), result.getUri(), clazz, clone(data));
    }

    private Map<String, Object> clone(Map<String, Object> input) {

        if (!input.containsKey(ENCRYPTION_METADATA)) {
            return input;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) input.get(ENCRYPTION_METADATA);

        byte[] base64Salt = ((String) metadata.get(ENCRYPTION_KEY_SALT)).getBytes();
        Integer iterations = (Integer) metadata.get(ENCRYPTION_KEY_ITERATIONS);
        Integer size = (Integer) metadata.get(ENCRYPTION_KEY_SIZE);

        EncryptionService service = new ApiKeySecretEncryptionService.Builder().setPassword(apiKeyCredentials.getSecret().toCharArray()).setKeySize(size)
                .setIterations(iterations).setBase64Salt(base64Salt).build();

        String encryptedSecret = (String) input.get(SECRET_PROPERTY_NAME);

        Map<String, Object> clonedData = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, Object> entry : input.entrySet()) {

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

        return clonedData;
    }

}
