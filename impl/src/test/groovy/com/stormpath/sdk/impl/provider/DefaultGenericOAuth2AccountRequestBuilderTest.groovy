package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.GenericOAuth2ProviderAccountRequestBuilder
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountRequestBuilder
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.3.0
 */
class DefaultGenericOAuth2AccountRequestBuilderTest {

    @Test
    void testWithAccessToken() {
        def providerRequest = Providers.OAUTH2;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof GenericOAuth2ProviderAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.setProviderId("instagram").
                setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
        assertTrue(request instanceof ProviderAccountRequest)
        assertEquals(request.getProviderData().getProviderId(), "instagram")
        def providerData = request.getProviderData()
        assertTrue(providerData instanceof DefaultGenericOAuth2ProviderData)
        providerData = (DefaultGenericOAuth2ProviderData) providerData
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
    }

    @Test
    void testWithAuthorizationCode() {
        def providerRequest = Providers.OAUTH2;
        def requestBuilder = providerRequest.account();
        assertTrue(requestBuilder instanceof GenericOAuth2ProviderAccountRequestBuilder)
        assertTrue(ProviderAccountRequestBuilder.isInstance(requestBuilder))
        def request = requestBuilder.setProviderId("amazon").setCode("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
        assertTrue(request instanceof ProviderAccountRequest)
        assertEquals(request.getProviderData().getProviderId(), "amazon")
        def providerData = request.getProviderData()
        assertTrue(providerData instanceof DefaultGenericOAuth2ProviderData)
        providerData = (DefaultGenericOAuth2ProviderData) providerData
        assertEquals(providerData.getProperty('code'), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
    }

    @Test
    void testMissingProviderId() {
        def requestBuilder = Providers.OAUTH2.account();

        try {
            requestBuilder.setAccessToken("CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD").build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "The providerId property is missing.")
        }
    }

    @Test
    void testMissingAccessTokenAndCode() {
        def requestBuilder = Providers.OAUTH2.account();

        try {
            requestBuilder.setProviderId("amazon").build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either accessToken or code must be provided before building.")
        }
    }

    @Test
    void testAddingBothAccessTokenAndCode() {
        def requestBuilder = Providers.OAUTH2.account();

        try {
            requestBuilder.setAccessToken("abc").setCode("sdc").setProviderId("amazon").build();
            fail("Should have failed")
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), "Either accessToken or code must be provided before building.")
        }
    }
}
