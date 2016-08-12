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
package com.stormpath.spring.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.0.RC5
 */

@Configuration
public class TwoAppTenantStormpathTestConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TwoAppTenantStormpathTestConfiguration.class);

    @Value("#{ @environment['stormpath.twoApp.apiKey.id.envName'] ?: 'STORMPATH_API_KEY_ID_TWO_APP' }")
    protected String twoAppApiKeyIdEnvName;

    @Value("#{ @environment['stormpath.twoApp.apiKey.secret.envName'] ?: 'STORMPATH_API_KEY_SECRET_TWO_APP' }")
    protected String twoAppApiKeySecretEnvName;

    @Value("#{ @environment['stormpath.client.baseUrl'] ?: 'https://api.stormpath.com/v1' }")
    protected String baseUrl;

    @Autowired
    CacheManager stormpathCacheManager;

    @Bean
    public ApiKey stormpathClientApiKey() {
        ApiKeyBuilder builder = ApiKeys.builder();

        String twoAppApiKeyId = System.getenv(twoAppApiKeyIdEnvName);
        String twoAppApiKeySecret = System.getenv(twoAppApiKeySecretEnvName);
        if (Strings.hasText(twoAppApiKeyId) && Strings.hasText(twoAppApiKeySecret)) {
            builder.setId(twoAppApiKeyId).setSecret(twoAppApiKeySecret);
        } else {
            log.warn(
                "Using TwoAppTenantStormpathConfiguration and " +
                "no " + twoAppApiKeyIdEnvName + " and/or " + twoAppApiKeySecretEnvName + " set"
            );
        }

        return builder.build();
    }

    @Bean
    public Client stormpathClient() {
        ClientBuilder builder = Clients.builder();

        builder.setCacheManager(stormpathCacheManager);
        builder.setApiKey(stormpathClientApiKey());
        builder.setBaseUrl(baseUrl);

        return builder.build();
    }
}
