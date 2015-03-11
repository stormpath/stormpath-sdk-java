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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.config.AbstractStormpathConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.0.RC4
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.enabled", matchIfMissing = true)
public class StormpathAutoConfiguration extends AbstractStormpathConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "stormpathClientApiKey")
    public ApiKey stormpathClientApiKey() {
        return super.stormpathClientApiKey();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathApplication")
    public Application stormpathApplication(Client client) {
        return super.stormpathApplication(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {
        return super.stormpathCacheManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public Client stormpathClient(@Qualifier("stormpathClientApiKey") ApiKey apiKey,
                                  com.stormpath.sdk.cache.CacheManager cacheManager) {
        return super.stormpathClient(apiKey, cacheManager);
    }

}
