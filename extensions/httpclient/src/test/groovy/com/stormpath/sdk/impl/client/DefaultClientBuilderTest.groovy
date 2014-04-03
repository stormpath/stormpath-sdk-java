/*
 * Copyright 2014 Stormpath, Inc. and contributors.
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

import com.stormpath.sdk.cache.Cache
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.client.DefaultApiKey
import com.stormpath.sdk.impl.cache.DefaultCacheManager
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator
import com.stormpath.sdk.impl.util.StringInputStream
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 1.0.alpha
 */
class DefaultClientBuilderTest {

    @Test
    void testDefault() {

        Client client = Clients.builder().setApiKey(new DefaultApiKey("fakeId", "fakeSecret")).build()

        //caching disabled by default (end-user must explicilty enable it based on their caching preferences):
        assertTrue client.dataStore.cacheManager instanceof DisabledCacheManager
    }

    @Test
    void testSetCustomCacheManager() {

        //dummy implementation:
        def cacheManager = new CacheManager() {
            def <K, V> Cache<K, V> getCache(String name) {
                return null
            }
        }

        Client client = Clients.builder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .setCacheManager(cacheManager)
                .build()

        assertSame client.dataStore.cacheManager, cacheManager
    }

    @Test
    void testDefaultCacheManager() {

        Client client = Clients.builder()
            .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
            .setCacheManager(Caches.newCacheManager().build())
            .build()

        def cm = client.dataStore.cacheManager

        assertTrue cm instanceof DefaultCacheManager
    }

    @Test
    void testRequestAuthenticatorNotSet() {

        Client client = Clients.builder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationSchemeNull() {

        Client client = Clients.builder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .setAuthenticationScheme(null)
                .build()

        def requestAuthenticator = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue requestAuthenticator instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testAuthenticationScheme() {

        Client client = Clients.builder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .setAuthenticationScheme(AuthenticationScheme.BASIC)
                .build()

        def authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue authenticationScheme instanceof BasicRequestAuthenticator

        client = Clients.builder()
                .setApiKey(new DefaultApiKey("fakeId", "fakeSecret"))
                .setAuthenticationScheme(AuthenticationScheme.SAUTHC1)
                .build()

        authenticationScheme = client.dataStore.requestExecutor.requestAuthenticator

        assertTrue authenticationScheme instanceof SAuthc1RequestAuthenticator
    }

    @Test
    void testSetApiKey() {
        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"
        def builder = Clients.builder().setApiKey(apiKeyId, apiKeySecret)
        assertEquals(builder.apiKey.getId(), apiKeyId)
        assertEquals(builder.apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyProperties() {
        def apiKeyIdPropertyName = "myPropertyId"
        def apiKeySecretPropertyName = "myPropertySecret"
        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"
        def properties = new Properties()

        properties.setProperty(apiKeyIdPropertyName, apiKeyId)
        properties.setProperty(apiKeySecretPropertyName, apiKeySecret)

        def builder = Clients.builder()
                .setApiKeyIdPropertyName(apiKeyIdPropertyName)
                .setApiKeySecretPropertyName(apiKeySecretPropertyName)
                .setApiKeyProperties(properties)

        assertEquals(builder.apiKeyProperties.get(apiKeyIdPropertyName), apiKeyId)
        assertEquals(builder.apiKeyProperties.get(apiKeySecretPropertyName), apiKeySecret)

        def requestExecutor = builder.build().dataStore.requestExecutor

        assertEquals(requestExecutor.apiKey.getId(), apiKeyId)
        assertEquals(requestExecutor.apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyReader() {

        def apiKeyIdPropertyName = "apiKey.id"
        def apiKeySecretPropertyName = "apiKey.secret"

        def apiKeyId = "fooId"
        def apiKeySecret = "barSecret"

        def reader = new StringReader(apiKeyIdPropertyName + "=" + apiKeyId + "\n" +
                apiKeySecretPropertyName + "=" + apiKeySecret)

        def builder = Clients.builder().setApiKeyReader(reader)

        def requestExecutor = builder.build().dataStore.requestExecutor

        assertEquals(requestExecutor.apiKey.getId(), apiKeyId)
        assertEquals(requestExecutor.apiKey.getSecret(), apiKeySecret)
    }

    @Test
    void testApiKeyReaderNull() {

        def builder = Clients.builder().setApiKeyReader(null)

        try {
            builder.build()
            fail("Should have thrown because of null Reader.")
        } catch (IllegalArgumentException e) {
            //Expected exception
        }
    }

    @Test
    void testEmptyPropertyValue() {

        def apiKeyIdPropertyName = "apiKey.id"
        def apiKeySecretPropertyName = "apiKey.secret"

        def apiKeyId = ""
        def apiKeySecret = "barSecret"

        def is = new StringInputStream(apiKeyIdPropertyName + "=" + apiKeyId + "\n" +
                apiKeySecretPropertyName + "=" + apiKeySecret)


        try {
            Clients.builder()
                    .setApiKeyInputStream(is)
                    .build()
            fail("Should have thrown due to empty apiKey.id value.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "There is no 'apiKey.id' property in the configured apiKey properties.  " +
                    "You can either specify that property or configure the apiKeyIdPropertyName value on the ClientBuilder " +
                    "to specify a custom property name.")
        }

    }

    @Test
    void testInvalidApiKeyFileLocation() {
        try {
            Clients.builder()
                    .setApiKeyFileLocation("/tmp/someUnexistentApiKeyPropertiesFile.properties")
                    .setApiKeyInputStream(null)
                    .build()
            fail("Should have thrown due to invalid ApiKeyFileLocation.")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Unable to load API Key using apiKeyFileLocation '/tmp/someUnexistentApiKeyPropertiesFile.properties'.  " +
                    "Please check and ensure that file exists or use the 'setApiKeyFileLocation' method to specify a valid location.")
        }
    }

    @Test
    void testEmptyBuilder() {
        try {
            Clients.builder().build()
            fail("Should have thrown due to invalid builder state.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "No API Key properties could be found or loaded from a file location.  " +
                    "Please configure the 'apiKeyFileLocation' property or alternatively configure a Properties, Reader or InputStream instance.")
        }
    }

    @Test
    void testSetProxy() {
        def proxy = new com.stormpath.sdk.client.Proxy("localhost", 8900)
        def builder = Clients.builder().setProxy(proxy)
        assertEquals(builder.proxy, proxy)
    }

    @Test
    void testSetNullProxy() {
        try {
            Clients.builder().setProxy(null)
            fail("Should have thrown due to null proxy.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "proxy argument cannot be null.")
        }
    }

    @Test
    void testSetBaseUrl() {
        def baseUrl = "http://localhost:8080/v1"
        def builder = ((DefaultClientBuilder)Clients.builder()).setBaseUrl(baseUrl)
        assertSame(builder.baseUrl, baseUrl)
    }

    @Test
    void testSetNullBaseUrl() {
        try {
            ((DefaultClientBuilder)Clients.builder()).setBaseUrl(null)
            fail("Should have thrown due to null baseUrl.")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "baseUrl argument cannot be null.")
        }
    }
}
