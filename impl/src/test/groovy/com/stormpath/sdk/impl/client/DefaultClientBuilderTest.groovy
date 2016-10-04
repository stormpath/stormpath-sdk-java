package com.stormpath.sdk.impl.client

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.impl.api.ClientApiKey
import com.stormpath.sdk.impl.api.DefaultApiKeyResolver
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials
import com.stormpath.sdk.impl.cache.DefaultCache
import com.stormpath.sdk.lang.Duration
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.AssertJUnit.fail

class DefaultClientBuilderTest {

    def builder, client

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
        assertEquals clientBuilder.clientConfiguration.connectionTimeout, 10
        assertEquals clientBuilder.clientConfiguration.authenticationScheme, AuthenticationScheme.BASIC
    }

    @Test
    void testConfigureProxy() {
        DefaultClientBuilder clientBuilder = (DefaultClientBuilder) builder
        assertEquals clientBuilder.clientConfiguration.proxyHost, "proxyyaml" // from yaml
        assertEquals clientBuilder.clientConfiguration.proxyPort, 9999 // from json
        assertEquals clientBuilder.clientConfiguration.proxyUsername, "fooyaml" // from yaml
        assertEquals clientBuilder.clientConfiguration.proxyPassword, "bar" // from properties
    }
}

class DefaultClientBuilderTestCustomCredentials{

    def builder, client, clientCredentials, id, secret

    @BeforeMethod
    void before() {

        id = UUID.randomUUID().toString()
        secret = UUID.randomUUID().toString()

        ApiKey apiKey = new ClientApiKey(id, secret)
        clientCredentials = new ApiKeyCredentials(apiKey)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(clientCredentials)
        client = builder.build()
    }

    @Test
    void testConfigureCredentials() {
        assertEquals client.dataStore.apiKey.id, id
        assertEquals client.dataStore.apiKey.secret, secret
    }

    /**
     * @since 1.1.0
     */
    @Test
    void testCustomClientCredentialsRequireApiKeyResolver(){
        def credentialsId = UUID.randomUUID().toString()
        def credentialsSecret = UUID.randomUUID().toString()

        ClientCredentials customCredentials = new ClientCredentials() {
            @Override
            String getId() {
                return credentialsId
            }

            @Override
            String getSecret() {
                return credentialsSecret
            }
        }


        builder = new DefaultClientBuilder()
        builder.setClientCredentials(customCredentials)

        try {
            client = builder.build()
            fail("Builder should require ApiKeyResolver if non-ApiKeyCredentials are supplied")
        }
        catch(Exception ex){
            assertTrue(ex.getMessage().contains("An ApiKeyResolver must be configured for ClientCredentials other than ApiKeyCredentials."))
        }

    }

    @Test
    void testCustomClientCredentialsAllowedWithApiKeyResolver(){
        def credentialsId = UUID.randomUUID().toString()
        def credentialsSecret = UUID.randomUUID().toString()

        ClientCredentials customCredentials = new ClientCredentials() {
            @Override
            String getId() {
                return credentialsId
            }

            @Override
            String getSecret() {
                return credentialsSecret
            }
        }

        def keyId = UUID.randomUUID().toString()
        def keySecret = UUID.randomUUID().toString()

        def apiKey = new ClientApiKey(keyId, keySecret)
        def apiKeyResolver = new DefaultApiKeyResolver(apiKey)

        builder = new DefaultClientBuilder()
        builder.setClientCredentials(customCredentials)
        builder.setApiKeyResolver(apiKeyResolver)
        def testClient = builder.build()

        assertEquals testClient.dataStore.apiKey.id, keyId
        assertEquals testClient.dataStore.apiKey.secret, keySecret
    }
}
