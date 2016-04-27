package com.stormpath.sdk.impl.client

import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.impl.cache.DefaultCache
import com.stormpath.sdk.lang.Duration
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class DefaultClientBuilderTest {

    def builder, client
    File propsFile

    @BeforeClass
    void setup() {
        Properties props = new Properties()
        props.setProperty('client.apiKey.file', 'classpath:key.txt')
        props.setProperty('client.apiKey.id', '12')
        props.setProperty('client.apiKey.secret', '13')
        props.setProperty('client.cacheManager.defaultTtl', '10000')
        props.setProperty('client.cacheManager.defaultTti', '10000')
        props.setProperty('client.cacheManager.caches.com.stormpath.sdk.impl.client.Account.tti', '1000')
        props.setProperty('client.cacheManager.caches.com.stormpath.sdk.impl.client.Account.ttl', '1500')
        props.setProperty('client.baseUrl', 'https://api.stormpath.com/v42')
        props.setProperty('client.connectionTimeout', '10')
        props.setProperty('client.authenticationScheme', 'BASIC')
        props.setProperty('client.proxy.port', '9000')
        props.setProperty('client.proxy.host', 'localhost')
        props.setProperty('client.proxy.username', 'foo')
        props.setProperty('client.proxy.password', 'bar')
        props.setProperty('application.name', 'appName')
        props.setProperty('application.href', 'appHref')
        propsFile = new File('src/test/resources/stormpath.properties')
        props.store(propsFile.newWriter(), null)
    }

    @AfterClass
    public void clean() {
        propsFile.delete()
    }

    @BeforeMethod
    void before() {
        builder = Clients.builder()
        client = builder.build()
    }

    @Test
    void testBuilder() {
        assertTrue(builder instanceof DefaultClientBuilder)
    }

    @Test
    void testConfigureCacheManager() {
        assertEquals client.dataStore.cacheManager.defaultTimeToLive, new Duration(10000, TimeUnit.SECONDS)
        assertEquals client.dataStore.cacheManager.defaultTimeToIdle, new Duration(10000, TimeUnit.SECONDS)
        DefaultCache cache = (DefaultCache) client.dataStore.cacheManager.getCache(Account.class.getName())
        assertEquals cache.timeToIdle, new Duration(1000, TimeUnit.SECONDS)
        assertEquals cache.timeToLive, new Duration(1500, TimeUnit.SECONDS)
    }

    @Test
    void testConfigureApiKey() {
        // remove key.txt from src/test/resources and this test will fail
        assertEquals client.dataStore.apiKey.id, "12"
        assertEquals client.dataStore.apiKey.secret, "13"
    }

    @Test
    void testConfigureBaseProperties() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.baseUrl, "https://api.stormpath.com/v42"
        assertEquals clientBuilder.clientConfiguration.connectionTimeout, 10 * 1000
        assertEquals clientBuilder.clientConfiguration.authenticationScheme, AuthenticationScheme.BASIC
    }

    @Test
    void testConfigureProxy() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.proxyHost, "localhost"
        assertEquals clientBuilder.clientConfiguration.proxyPort, 9000
        assertEquals clientBuilder.clientConfiguration.proxyUsername, "foo"
        assertEquals clientBuilder.clientConfiguration.proxyPassword, "bar"
    }

    @Test
    void testConfigureApplication() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.applicationName, "appName"
        assertEquals clientBuilder.clientConfiguration.applicationHref, "appHref"
    }
}
