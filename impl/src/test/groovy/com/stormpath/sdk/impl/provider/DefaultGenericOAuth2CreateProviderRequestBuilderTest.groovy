package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.CreateProviderRequest
import com.stormpath.sdk.provider.CreateProviderRequestBuilder
import com.stormpath.sdk.provider.GenericOAuth2CreateProviderRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class DefaultGenericOAuth2CreateProviderRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.OAUTH2;
        def requestBuilder = providerRequest.builder();
        assertTrue(requestBuilder instanceof GenericOAuth2CreateProviderRequestBuilder)
        assertTrue(CreateProviderRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.setProviderId("amazon")
                .setClientId("999999911111111")
                .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0").build();
        assertTrue(request instanceof CreateProviderRequest)
        assertEquals(request.getProvider().getProviderId(), "amazon")
        def provider = request.getProvider()
        assertTrue(provider instanceof DefaultGenericOAuth2Provider)
        provider = (DefaultGenericOAuth2Provider) provider
        assertEquals(provider.getClientId(), "999999911111111")
        assertEquals(provider.getClientSecret(), "a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
    }

    @Test
    void testMissingAllProperties() {
        def requestBuilder = Providers.OAUTH2.builder();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

    @Test
    void testMissingClientSecret() {
        def requestBuilder = Providers.OAUTH2.builder();

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
        def requestBuilder = Providers.OAUTH2.builder();

        try {
            requestBuilder
                    .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "clientId is a required property. It must be provided before building.")
        }
    }

    @Test
    void testMissingProviderId() {
        def requestBuilder = Providers.OAUTH2.builder();

        try {
            requestBuilder
                    .setClientId("999999911111111")
                    .setClientSecret("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "The providerId property is missing.")
        }
    }

}
