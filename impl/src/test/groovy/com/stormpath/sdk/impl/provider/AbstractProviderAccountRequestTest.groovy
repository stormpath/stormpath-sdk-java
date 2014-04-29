package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.ProviderData
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
            new DefaultProviderAccountRequest(null) {}
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
            new DefaultProviderAccountRequest(providerData) {}
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

        def abstractProviderAccountRequest = new DefaultProviderAccountRequest(providerData) {}
        assertEquals(abstractProviderAccountRequest.getProviderData(), providerData)

        verify(providerData)

    }
}
