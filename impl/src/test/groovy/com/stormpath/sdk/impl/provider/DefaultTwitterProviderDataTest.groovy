package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.DateProperty
import com.stormpath.sdk.impl.resource.StringProperty
import com.stormpath.sdk.provider.ProviderData
import com.stormpath.sdk.provider.TwitterProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
/**
 * @since 1.3.0
 */
public class DefaultTwitterProviderDataTest {

    @Test
    void testGetPropertyDescriptors() {

        def providerData = new DefaultTwitterProviderData(createStrictMock(InternalDataStore))

        def propertyDescriptors = providerData.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 5, propertyDescriptors.toString())

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("createdAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("modifiedAt") instanceof DateProperty)
        assertTrue(propertyDescriptors.get("accessToken") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("accessTokenSecret") instanceof StringProperty)
        assertTrue(ProviderData.isInstance(providerData))
        assertTrue(TwitterProviderData.isInstance(providerData))
    }

    @Test
    void testMethods() {

        def properties = [href: "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData",
                createdAt: "2013-10-01T23:38:55.000Z",
                modifiedAt: "2013-10-02T23:38:55.000Z",
                accessToken: "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD",
                accessTokenSecret: "SAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsCRET"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultTwitterProviderData(internalDataStore, properties)

        assertEquals(providerData.getHref(), "https://api.stormpath.com/v1/accounts/iouertnw48ufsjnsDFSf/providerData")
        assertEquals(providerData.getProviderId(), "twitter")
        assertEquals(providerData.getCreatedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")), "2013-10-01T23:38:55.000Z")
        assertEquals(providerData.getModifiedAt().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT")) , "2013-10-02T23:38:55.000Z")
        assertEquals(providerData.getAccessToken(), "CAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsHUZD")
        assertEquals(providerData.getAccessTokenSecret(), "SAAHUbqIB55EH1MmLxJJLGRPXVknFt0aA36spMcFQXIzTdsCRET")

        providerData.setAccessToken("AAAAAAAAAAAA")
        assertEquals(providerData.getAccessToken(), "AAAAAAAAAAAA")
        providerData.setAccessTokenSecret("SAAAAAAAAAAA")
        assertEquals(providerData.getAccessTokenSecret(), "SAAAAAAAAAAA")
    }

    @Test
    void testConstructor() {

        def internalDataStore = createStrictMock(InternalDataStore)
        def providerData = new DefaultFacebookProviderData(internalDataStore)

        assertEquals(providerData.getProviderId(), "facebook")
    }
}
