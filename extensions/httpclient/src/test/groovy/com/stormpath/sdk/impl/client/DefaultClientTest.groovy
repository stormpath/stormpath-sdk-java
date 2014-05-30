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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 *
 * @since 1.0.alpha
 */
class DefaultClientTest {

    @Test
    void testConstructorOK() {

        def apiKey = createStrictMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def proxy = createStrictMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createStrictMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1

        expect(proxy.getHost()).andReturn("192.168.2.110")
        expect(proxy.getPort()).andReturn(777)
        expect(proxy.isAuthenticationRequired()).andReturn(false)

        replay(apiKey, proxy, cacheManager)

        Client client = new DefaultClient(apiKey, baseUrl, proxy, cacheManager, authcScheme)

        assertEquals(client.dataStore.requestExecutor.apiKey, apiKey)

        verify(apiKey, proxy, cacheManager)
    }

    @Test
    void testConstructorApiKeyNull() {

        String baseUrl = "http://localhost:8080/v1"
        def proxy = createMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1

        try {
            new DefaultClient(null, baseUrl, proxy, cacheManager, authcScheme)
            fail("Should have thrown due to null ApiKey")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "apiKey argument cannot be null.")
        }
    }

/*    @Test
    // commenting out this test since the data store implementation changed and Integration Tests are passing with the changes
    void testGetCurrentTenant() {

        def apiKey = createStrictMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def proxy = createStrictMock(com.stormpath.sdk.client.Proxy)
        def cacheManager = createStrictMock(CacheManager)
        def authcScheme = AuthenticationScheme.SAUTHC1
        def cache = createStrictMock(Cache)
        def map = createStrictMock(Map)
        def set = createStrictMock(Set)
        def iterator = createNiceMock(Iterator)

        expect(proxy.getHost()).andReturn("192.168.2.110")
        expect(proxy.getPort()).andReturn(777)
        expect(proxy.isAuthenticationRequired()).andReturn(false)
        expect(cacheManager.getCache("com.stormpath.sdk.tenant.Tenant")).andReturn(cache)
        expect(cache.get("http://localhost:8080/v1/tenants/current")).andReturn(map)
        expect(map.isEmpty()).andReturn(false).times(2)
        expect(map.size()).andReturn(1)
        expect(map.entrySet()).andReturn(set)
        expect(set.iterator()).andReturn(iterator)

        replay(apiKey, proxy, cacheManager, cache, map, set, iterator)

        Client client = new DefaultClient(apiKey, baseUrl, proxy, cacheManager, authcScheme)
        client.getCurrentTenant()

        verify(apiKey, proxy, cacheManager, cache, map, set, iterator)
    }*/

    @Test
    void testGetDataStore() {

        def apiKey = createNiceMock(ApiKey)
        String baseUrl = "http://localhost:8080/v1"
        def authcScheme = AuthenticationScheme.SAUTHC1

        Client client = new DefaultClient(apiKey, baseUrl, null, null, authcScheme)

        assertNotNull(client.getDataStore())
        assertEquals(client.getDataStore().baseUrl, baseUrl)
    }


}
