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
package com.stormpath.sdk.servlet.csrf.config;

import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class CsrfTokenManagerFactory extends ConfigSingletonFactory<CsrfTokenManager> {

    public static final String CSRF_TOKEN_NAME = "stormpath.web.csrf.token.name";
    public static final String CSRF_TOKEN_TTL = "stormpath.web.csrf.token.ttl";
    public static final String NONCE_CACHE_NAME = "stormpath.web.nonce.cache.name";

    @Override
    protected CsrfTokenManager createInstance(ServletContext servletContext) throws Exception {

        String ttlString = getConfig().get(CSRF_TOKEN_TTL);
        long ttlMillis;
        try {
            ttlMillis = Long.parseLong(ttlString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(CSRF_TOKEN_TTL + " config value must be a long.", e);
        }

        String usedNonceCacheName = getConfig().get(NONCE_CACHE_NAME);
        Assert.hasText(usedNonceCacheName, NONCE_CACHE_NAME + " config value is required.");

        Client client = ClientResolver.INSTANCE.getClient(servletContext);
        CacheManager cacheManager = client.getCacheManager();
        Cache<String,String> usedNonceCache = cacheManager.getCache(usedNonceCacheName);

        String signingKey = client.getApiKey().getSecret();

        String tokenName = getConfig().get(CSRF_TOKEN_NAME);

        return new DefaultCsrfTokenManager(tokenName, usedNonceCache, signingKey, ttlMillis);
    }
}
