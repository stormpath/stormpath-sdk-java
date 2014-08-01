/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.client

import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.impl.api.ClientApiKey
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.client.DefaultClientBuilder
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator
import com.stormpath.sdk.impl.util.Duration
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.*

/**
 * @since 1.0.alpha
 */
public class ClientsTest {

    @Test
    void testBuilder() {
        def builder = Clients.builder();
        assertTrue(builder instanceof DefaultClientBuilder)
    }

    @Test
    void testDefaultBuild() {

        def client = Clients.builder().setApiKey(new ClientApiKey('foo', 'bar')).build()

        assertNotNull client
        assertEquals client.dataStore.apiKey.id, 'foo'
        assertEquals client.dataStore.apiKey.secret, 'bar'

        //caching is enabled by default in 1.0.0:
        assertTrue client.dataStore.cacheManager instanceof DefaultCacheManager

        //ensure these defaults match what is documented in the Clients class-level JavaDoc:
        assertEquals client.dataStore.cacheManager.defaultTimeToLive, new Duration(1, TimeUnit.HOURS)
        assertEquals client.dataStore.cacheManager.defaultTimeToIdle, new Duration(1, TimeUnit.HOURS)
    }

    @Test
    void testSetCustomCacheManager() {

        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

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

        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .setCacheManager(Caches.newCacheManager().build())
                .build()

        def cm = client.dataStore.cacheManager

        assertTrue cm instanceof DefaultCacheManager
    }

    @Test
    void testRequestAuthenticatorNotSet() {

        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationSchemeNull() {

        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

        Client client = Clients.builder()
                .setApiKey(apiKey)
                .setAuthenticationScheme(null)
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationScheme() {

        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

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
        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)

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
        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)
        def proxy = new com.stormpath.sdk.client.Proxy("localhost", 8900)
        def builder = Clients.builder().setApiKey(apiKey).setProxy(proxy)
        assertEquals(builder.proxy, proxy)
    }

    @Test
    void testSetNullProxy() {
        com.stormpath.sdk.api.ApiKey apiKey = createMock(com.stormpath.sdk.api.ApiKey)
        try {
            Clients.builder().setApiKey(apiKey).setProxy(null)
            fail("Should have thrown due to null proxy.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "proxy argument cannot be null.")
        }
    }

}
