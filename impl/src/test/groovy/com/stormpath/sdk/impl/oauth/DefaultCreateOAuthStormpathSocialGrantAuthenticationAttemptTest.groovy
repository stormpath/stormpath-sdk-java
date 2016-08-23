package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.StringProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.1.0
 */
class DefaultCreateOAuthStormpathSocialGrantAuthenticationAttemptTest {

    @Test
    void testGetPropertyDescriptors() {

        def defaultCreateGrantAuthAttempt = new DefaultOAuthStormpathSocialGrantAuthenticationAttempt(createStrictMock(InternalDataStore))

        def propertyDescriptors = defaultCreateGrantAuthAttempt.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 4)

        assertTrue(propertyDescriptors.get("providerId") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("accessToken") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("code") instanceof StringProperty)
        assertTrue(propertyDescriptors.get("grant_type") instanceof StringProperty)
    }

    @Test
    void testAuthentication() {

        def properties = [
                providerId  : "facebook",
                accessToken : "VVVNNN",
                grant_type  : "stormpath_social"
        ]

        def internalDataStore = createStrictMock(InternalDataStore)

        def attempt = new DefaultOAuthStormpathSocialGrantAuthenticationAttempt(internalDataStore, properties)

        assertEquals(attempt.getProviderId(), properties.providerId)
        assertEquals(attempt.getAccessToken(), properties.accessToken)
        assertEquals(attempt.getGrantType(), properties.grant_type)
    }
}
