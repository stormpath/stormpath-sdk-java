package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.provider.OAuth2ProviderData
import com.stormpath.sdk.provider.ProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*
/**
 * @since 1.3.0
 */
class DefaultOAuth2ProviderDataTest {

    @Test
    void testGetPropertyDescriptors() {

        def providerData = new DefaultOAuth2ProviderData(createStrictMock(InternalDataStore), "amazon")

        def propertyDescriptors = providerData.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 5)

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("accessToken") instanceof StringProperty)
        assertTrue(ProviderData.isInstance(providerData))
        assertTrue(OAuth2ProviderData.isInstance(providerData))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData",
                          providerId: "twitch",
                          createdAt: "2013-10-01T23:38:55.000Z",
                          modifiedAt: "2013-10-02T23:38:55.000Z",
                          accessToken: "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultOAuth2ProviderData(internalDataStore, properties)

        assertEquals(providerData.getHref(), "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData")
        assertNotNull(providerData.getProviderId())
        assertEquals(providerData.getProviderId(), "twitch")
        assertEquals(providerData.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(providerData.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")

        providerData.setAccessToken("AAAAAAAAAAAA")
        assertEquals(providerData.getAccessToken(), "AAAAAAAAAAAA")
    }

    @Test
    void testConstructor() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultOAuth2ProviderData(internalDataStore, "amazon")

        assertEquals(providerData.getProviderId(), "amazon")

        providerData = new DefaultOAuth2ProviderData(internalDataStore)
        providerData.setProviderId("instagram")

        assertEquals(providerData.getProviderId(), "instagram")
    }

}

