package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.EnumProperty
import com.stormpath.sdk.impl.resource.ListProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.provider.AccessTokenType
import com.stormpath.sdk.provider.OAuth2Provider
import com.stormpath.sdk.provider.Provider
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class DefaultOAuth2ProviderTest {

    @Test
    void testGetPropertyDescriptors() {

        def provider = new DefaultOAuth2Provider(createStrictMock(InternalDataStore), "amazon")

        def propertyDescriptors = provider.getPropertyDescriptors()

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("clientId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("clientSecret") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("authorizationEndpoint") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("tokenEndpoint") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("resourceEndpoint") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("accessTokenType") instanceof EnumProperty<AccessTokenType>)
        assertTrue(propertyDescriptors.get("scope") instanceof ListProperty)
        assertEquals(propertyDescriptors.size(), 11)

        assertTrue(Provider.isInstance(provider))
        assertTrue(OAuth2Provider.isInstance(provider))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider",
                          createdAt: "2013-10-01T23:38:55.000Z",
                          modifiedAt: "2013-10-02T23:38:55.000Z",
                          providerId: "imgur",
                          clientId:"c2f39aa71a0cfdb",
                          clientSecret:"7dca1bdfe33dbb6e1efc601b33786e08f53d4ce4",
                          redirectUri:"https://myapp.com/home",
                          authorizationEndpoint : "https://api.imgur.com/oauth2/authorize",
                          tokenEndpoint : "https://api.imgur.com/oauth2/token",
                          resourceEndpoint : "https://api.imgur.com/3/account/me",
                          accessTokenType : "bearer",
                          "scope": ["foo", "bar"]
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def provider = new DefaultOAuth2Provider(internalDataStore, properties)

        assertEquals(provider.getHref(), "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider")
        assertEquals(provider.getProviderId(), "imgur")
        assertEquals(provider.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(provider.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(provider.getClientId(), "c2f39aa71a0cfdb")
        assertEquals(provider.getClientSecret(), "7dca1bdfe33dbb6e1efc601b33786e08f53d4ce4")
        assertEquals(provider.getScope(), ["foo", "bar"])

        provider.setClientId("999999999999")
        assertEquals(provider.getClientId(), "999999999999")
        provider.setClientSecret("AAAAA9999999")
        assertEquals(provider.getClientSecret(), "AAAAA9999999")
    }

    @Test
    void testConstructor() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultOAuth2Provider(internalDataStore, "amazon")

        assertEquals(providerData.getProviderId(), "amazon")

        providerData = new DefaultOAuth2Provider(internalDataStore)
        providerData.setProviderId("instagram")

        assertEquals(providerData.getProviderId(), "instagram")
    }
}
