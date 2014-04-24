package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.oauth.FacebookAccountRequestBuilder
import com.stormpath.sdk.oauth.ProviderAccountRequest
import com.stormpath.sdk.oauth.ProviderAccountRequestBuilder
import com.stormpath.sdk.oauth.Providers
import org.junit.Test

import static org.testng.Assert.*

/**
 * @since 1.0.alpha
 */
class FacebookAccountRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.FACEBOOK;
        def requestBuilder = providerRequest.accountRequest();
        assertTrue(requestBuilder instanceof FacebookAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "facebook")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultFacebookProviderData)
        providerData = (DefaultFacebookProviderData) providerData
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
    }

    @Test
    void testMissingAccessToken() {
        def requestBuilder = Providers.FACEBOOK.accountRequest();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "accessToken is a required property. It must be provided before building.")
        }
    }
}
