package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.0
 */
class DefaultCreateOAuthClientCredentialsGrantAuthenticationAttemptTest {
    @Test
    void testGetPropertyDescriptors() {

        def defaultCreateGrantAuthAttempt = new DefaultOAuthClientCredentialsGrantAuthenticationAttempt(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultCreateGrantAuthAttempt.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 3)

        assertTrue(propertyDescriptors.get("apiKeyId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("apiKeySecret") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("grant_type") instanceof StringProperty)
    }

    @Test
    void testMethods() {

        def properties = [
                apiKeyId    : "XXXYYY",
                apiKeySecret: "VVVNNN",
                grant_type  : "client_credentials"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)

        def attempt = new DefaultOAuthClientCredentialsGrantAuthenticationAttempt(internalDataStore, properties)

        assertEquals(attempt.getApiKeyId(), properties.apiKeyId)
        assertEquals(attempt.getApiKeySecret(), properties.apiKeySecret)
        assertEquals(attempt.getGrantType(), properties.grant_type)
    }
}
