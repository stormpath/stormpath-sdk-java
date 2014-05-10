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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.*

/**
 *
 * @since 1.0.alpha
 */
class DefaultClientBuilderTest {

    @Test
    void testIllegalBuildState() {
        try {
            Clients.builder().build()
            fail("Should have thrown because of missing ApiKey.")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "No ApiKey has been set. It is required to properly build the Client. See 'setApiKey(ApiKey)'.")
        }
    }

    @Test
    void testDefault() {

        ApiKey apiKey = createMock(ApiKey)

        Client client = Clients.builder().setApiKey(apiKey).build()

        //caching disabled by default (end-user must explicitly enable it based on their caching preferences):
        assertTrue client.dataStore.cacheManager instanceof DisabledCacheManager
    }

    @Test
    void testSetCustomCacheManager() {

        ApiKey apiKey = createMock(ApiKey)

        //dummy implementation:
        def cacheManager = new CacheManager() {
            def <K, V> Cache<K, V> getCache(String name) {
                return null
            }
        }

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .setCacheManager(cacheManager)
                .build()

        assertSame client.dataStore.cacheManager, cacheManager
    }

    @Test
    void testDefaultCacheManager() {

        ApiKey apiKey = createMock(ApiKey)

        Client client = Clients.builder()
            .setApiKey(apiKey)
            .setCacheManager(Caches.newCacheManager().build())
            .build()

        def cm = client.dataStore.cacheManager

        assertTrue cm instanceof DefaultCacheManager
    }

    @Test
    void testRequestAuthenticatorNotSet() {

        ApiKey apiKey = createMock(ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationSchemeNull() {

        ApiKey apiKey = createMock(ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .setAuthenticationScheme(null)
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationScheme() {

        ApiKey apiKey = createMock(ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .setAuthenticationScheme(AuthenticationScheme.BASIC)
                .build()

        def authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue authenticationScheme instanceof BasicRequestAuthenticator

        client = Clients.builder()
                .setApiKey(apiKey)
                .setAuthenticationScheme(AuthenticationScheme.SAUTHC1)
                .build()

        authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue authenticationScheme instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testSetApiKey() {
        ApiKey apiKey = createMock(ApiKey)

        def builder = Clients.builder().setApiKey(apiKey)

        assertEquals(builder.apiKey, apiKey)
    }

    @Test
    void testApiKeyNull() {
        try {
            Clients.builder().setApiKey(null)
            fail("Should have thrown because of null ApiKey.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "apiKey cannot be null.")
        }
    }

    @Test
    void testSetProxy() {
        ApiKey apiKey = createMock(ApiKey)
        def proxy = new com.stormpath.sdk.client.Proxy("localhost", 8900)
        def builder = Clients.builder().setApiKey(apiKey).setProxy(proxy)
        assertEquals(builder.proxy, proxy)
    }

    @Test
    void testSetNullProxy() {
        ApiKey apiKey = createMock(ApiKey)
        try {
            Clients.builder().setApiKey(apiKey).setProxy(null)
            fail("Should have thrown due to null proxy.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "proxy argument cannot be null.")
        }
    }
}
