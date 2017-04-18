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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.0.RC4
 */
@Configuration
@Conditional(StormpathEnabled.class)
public class StormpathConfiguration extends AbstractStormpathConfiguration {

    @Bean
    public ApiKey stormpathClientApiKey() {
        return super.stormpathClientApiKey();
    }

    @Bean
    public Application stormpathApplication() {
        return super.stormpathApplication();
    }

    @Bean
    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {
        return super.stormpathCacheManager();
    }

    @Bean
    public Client stormpathClient() {
        return super.stormpathClient();
    }

    @Bean
    public String oktaAuthorizationServerId() {
        return super.oktaAuthorizationServerId();
    }
}
