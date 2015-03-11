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
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import com.stormpath.spring.cache.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @since 1.0.RC4
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = "stormpath.enabled", matchIfMissing = true)
@EnableConfigurationProperties(
    { StormpathProperties.class, StormpathApplicationProperties.class, StormpathClientApiKeyProperties.class,
        StormpathClientAuthenticationProperties.class, StormpathClientProxyProperties.class,
        StormpathCacheProperties.class })
public class StormpathAutoConfiguration {

    @Autowired
    protected StormpathProperties stormpathProperties;

    @Autowired
    protected StormpathClientApiKeyProperties apiKeyProperties;

    @Autowired
    protected StormpathApplicationProperties applicationProperties;

    @Autowired
    protected StormpathClientAuthenticationProperties authenticationProperties;

    @Autowired
    protected StormpathClientProxyProperties proxyProperties;

    @Autowired
    protected StormpathCacheProperties cacheProperties;

    @Autowired(required = false)
    protected CacheManager cacheManager;

    @Bean
    @ConditionalOnMissingBean(name = "stormpathClientApiKey")
    public ApiKey stormpathClientApiKey() {
        return apiKeyProperties.resolveApiKey();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathApplication")
    public Application stormpathApplication(Client client) {
        return applicationProperties.resolveApplication(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {

        if (!cacheProperties.isEnabled()) {
            return new DisabledCacheManager();
        }

        if (cacheManager != null) {
            return new SpringCacheManager(cacheManager);
        }

        //otherwise no Spring CacheManager - create a default:
        return Caches.newCacheManager()
                     .withDefaultTimeToLive(1, TimeUnit.HOURS)
                     .withDefaultTimeToIdle(1, TimeUnit.HOURS).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Client stormpathClient(@Qualifier("stormpathClientApiKey") ApiKey apiKey,
                                  com.stormpath.sdk.cache.CacheManager cacheManager) {

        ClientBuilder builder = Clients.builder().setBaseUrl(stormpathProperties.getBaseUrl())
                                       .setAuthenticationScheme(authenticationProperties.getScheme()).setApiKey(apiKey)
                                       .setCacheManager(cacheManager);

        Proxy proxy = proxyProperties.resolveProxy();
        if (proxy != null) {
            builder.setProxy(proxy);
        }

        return builder.build();
    }

}
