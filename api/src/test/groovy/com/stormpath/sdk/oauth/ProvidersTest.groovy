package com.stormpath.sdk.oauth

import org.junit.Test

import static org.testng.Assert.assertTrue

/**
 * @since 1.0.alpha
 */
class ProvidersTest {

    @Test
    void test() {
        def providerRequest = Providers.FACEBOOK;
        assertTrue(providerRequest instanceof FacebookProviderRequest)
        assertTrue(ProviderRequest.isInstance(providerRequest))

        providerRequest = Providers.GOOGLE;
        assertTrue(providerRequest instanceof GoogleProviderRequest)
        assertTrue(ProviderRequest.isInstance(providerRequest))
    }
}
