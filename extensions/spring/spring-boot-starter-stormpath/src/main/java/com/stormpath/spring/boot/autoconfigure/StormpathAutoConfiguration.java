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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
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
@EnableConfigurationProperties({ StormpathProperties.class,
                                   StormpathApplicationProperties.class, StormpathClientApiKeyProperties.class,
                                   StormpathClientAuthenticationProperties.class,
                                   StormpathClientProxyProperties.class })
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

    @Bean
    @ConditionalOnMissingBean(name = "stormpathClientApiKey")
    public ApiKey stormpathClientApiKey() {
        return apiKeyProperties.resolveApiKey();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathApplication")
    public Application stormpathApplication(Client client) {
        return applicationProperties.resolveApplication(client);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Configuration
    @ConditionalOnMissingBean(type={"org.springframework.cache.CacheManager", "com.stormpath.sdk.cache.CacheManager"})
    protected static class DefaultStormpathCacheManagerConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {
            return Caches.newCacheManager()
                         .withDefaultTimeToIdle(1, TimeUnit.HOURS)
                         .withDefaultTimeToIdle(1, TimeUnit.HOURS)
                         .build();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Configuration
    @ConditionalOnBean(CacheManager.class)
    @ConditionalOnMissingBean(com.stormpath.sdk.cache.CacheManager.class)
    protected static class SpringCachingStormpathCacheManagerConfiguration {

        @Autowired
        private CacheManager cacheManager;

        @Bean
        @ConditionalOnMissingBean
        public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {
            return new SpringStormpathCacheManager(cacheManager);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public Client stormpathClient(@Qualifier("stormpathClientApiKey") ApiKey apiKey,
                                  com.stormpath.sdk.cache.CacheManager cacheManager) {

        ClientBuilder builder = Clients.builder()
            .setBaseUrl(stormpathProperties.getBaseUrl())
            .setAuthenticationScheme(authenticationProperties.getScheme())
            .setApiKey(apiKey)
            .setCacheManager(cacheManager);

        Proxy proxy = proxyProperties.resolveProxy();
        if (proxy != null) {
            builder.setProxy(proxy);
        }

        return builder.build();
    }


    @SuppressWarnings("unchecked")
    protected static class SpringStormpathCacheManager implements com.stormpath.sdk.cache.CacheManager {

        private CacheManager springCacheManager;

        public SpringStormpathCacheManager(CacheManager springCacheManager) {
            this.springCacheManager = springCacheManager;
        }

        @Override
        public <K, V> com.stormpath.sdk.cache.Cache<K, V> getCache(String name) {

            final Cache cache = this.springCacheManager.getCache(name);

            return new com.stormpath.sdk.cache.Cache<K,V>() {
                @Override
                public V get(K key) {
                    Cache.ValueWrapper vw = cache.get(key);
                    if (vw == null) {
                        return null;
                    }
                    return (V)vw.get();
                }

                @Override
                public V put(K key, V value) {
                    Cache.ValueWrapper vw = cache.putIfAbsent(key, value);
                    if (vw == null) {
                        return null;
                    }
                    return (V)vw.get();
                }

                @Override
                public V remove(K key) {
                    V v = get(key);
                    cache.evict(key);
                    return v;
                }
            };
        }
    }
}
