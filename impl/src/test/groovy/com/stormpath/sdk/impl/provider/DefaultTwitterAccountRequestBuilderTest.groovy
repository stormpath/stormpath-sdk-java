package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.*
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class DefaultTwitterAccountRequestBuilderTest {

    @Test
    void testWithAccessToken() {
        def providerRequest = Providers.TWITTER;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof TwitterAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.
                setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
                .setAccessTokenSecret("SASHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsCRET").build();
        assertTrue(request instanceof ProviderAccountRequest)
        assertEquals(request.getProviderData().getProviderId(), "twitter")
        def providerData = request.getProviderData()
        assertTrue(providerData instanceof DefaultTwitterProviderData)
        providerData = (DefaultTwitterProviderData) providerData
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
        assertEquals(providerData.getAccessTokenSecret(), "SASHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsCRET")
    }

    @Test
    void testMissingAccessTokenSecret() {
        def requestBuilder = Providers.TWITTER.account();

        try {
            requestBuilder.setAccessToken("abc")build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Both accessToken and accessTokenSecret must be provided before building.")
        }
    }

    @Test
    void testMissingAccessToken() {
        def requestBuilder = Providers.TWITTER.account();

        try {
            requestBuilder.setAccessTokenSecret("def").build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Both accessToken and accessTokenSecret must be provided before building.")
        }
    }
}
