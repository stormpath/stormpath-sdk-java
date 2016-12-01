package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.CreateProviderRequestBuilder
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.provider.TwitterCreateProviderRequestBuilder
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class DefaultTwitterCreateProviderRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.TWITTER;
        def requestBuilder = providerRequest.builder();
        assertTrue(requestBuilder instanceof TwitterCreateProviderRequestBuilder)
        assertTrue(CreateProviderRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder
                .setClientId("999999911111111")
                .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0").build();
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.getProvider().getProviderId(), "twitter")
        def provider = request.getProvider()
        assertTrue(provider instanceof DefaultTwitterProvider)
        provider = (DefaultTwitterProvider) provider
        assertEquals(provider.getClientId(), "999999911111111")
        assertEquals(provider.getClientSecret(), "a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
    }

    @Test
    void testMissingAllProperties() {
        def requestBuilder = Providers.TWITTER.builder();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

    @Test
    void testMissingClientSecret() {
        def requestBuilder = Providers.TWITTER.builder();

        try {
            requestBuilder
                    .setClientId("999999911111111")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientSecret is a required property. It must be provided before building.")
        }
    }

    @Test
    void testMissingClientId() {
        def requestBuilder = Providers.TWITTER.builder();

        try {
            requestBuilder
                    .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

}
