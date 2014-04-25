package com.stormpath.sdk.oauth

import org.junit.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail


/**
 * @since 1.0.beta
 */
class AbstractProviderAccountRequestTest {

    @Test
    void testNullProviderData() {
        try {
            new AbstractProviderAccountRequest(null) {}
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "providerData cannot be null")
        }
    }

    @Test
    void testMissingProviderId() {
        def providerData = createStrictMock(ProviderData)

        expect(providerData.getProviderId()).andReturn(null)

        replay(providerData)

        try {
            new AbstractProviderAccountRequest(providerData) {}
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "providerId within ProviderData instance must be specified")
        }

        verify(providerData)
    }

    @Test
    void test() {
        def providerData = createStrictMock(ProviderData)

        expect(providerData.getProviderId()).andReturn("stormpath")

        replay(providerData)

        def abstractProviderAccountRequest = new AbstractProviderAccountRequest(providerData) {}
        assertEquals(abstractProviderAccountRequest.getProviderData(), providerData)

        verify(providerData)

    }
}
