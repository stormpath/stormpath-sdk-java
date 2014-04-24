package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.oauth.GoogleAccountRequestBuilder
import com.stormpath.sdk.oauth.ProviderAccountRequest
import com.stormpath.sdk.oauth.ProviderAccountRequestBuilder
import com.stormpath.sdk.oauth.Providers
import org.junit.Test

import static org.testng.Assert.*

/**
 * @since 1.0.alpha
 */
class GoogleAccountRequestBuilderTest {

    @Test
    void testWithAccessToken() {
        def providerRequest = Providers.GOOGLE;
        def requestBuilder = providerRequest.accountRequest();
        assertTrue(requestBuilder instanceof GoogleAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setAccessToken("y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "google")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultGoogleProviderData)
        providerData = (DefaultGoogleProviderData) providerData
        assertEquals(providerData.getAccessToken(), "y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
        assertNull(providerData.getCode())
    }

    @Test
    void testWithCode() {
        def providerRequest = Providers.GOOGLE;
        def requestBuilder = providerRequest.accountRequest();
        assertTrue(requestBuilder instanceof GoogleAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def providerAccountRequest = requestBuilder.setCode("4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0").build();
        assertTrue(providerAccountRequest instanceof ProviderAccountRequest)
        assertEquals(providerAccountRequest.getProviderData().getProviderId(), "google")
        def providerData = providerAccountRequest.getProviderData()
        assertTrue(providerData instanceof DefaultGoogleProviderData)
        providerData = (DefaultGoogleProviderData) providerData
        assertEquals(providerData.getCode(), "4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
        assertNull(providerData.getAccessToken())
    }

    @Test
    void testInvalidStateProperties() {
        def requestBuilder = Providers.GOOGLE.accountRequest();

        try {
            requestBuilder.build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a Google account request.")
        }

        try {
            requestBuilder.setCode("4/2Dz0r7r9oNBE9dFD-_JUb.suCu7uj8TEnp6UAPm0")
                    .setAccessToken("y29.1.AADN_Xo2hxQflWwsgCSK-WjSw1mNfZiv4")
                    .build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either 'code' or 'accessToken' properties must exist in a Google account request.")
        }
    }
}
