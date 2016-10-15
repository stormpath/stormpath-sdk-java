package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.impl.resource.AbstractResource
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class DefaultProviderAccountRequestResolverTest {

    DefaultProviderAccountRequestResolver resolverUT = new DefaultProviderAccountRequestResolver()

    @Test(dataProvider = "providerIds")
    void testResolveProviderAccountRequest(String providerId) {
        def actual = resolverUT.getProviderAccountRequest(providerId, "1234567", "http://cool-app.stormpath.io/authorize/callback")
        assertThat(actual.providerData.providerId, is(providerId))
        def providerData = actual.providerData as AbstractResource
        assertThat(actual.redirectUri, is("http://cool-app.stormpath.io/authorize/callback"))
        assertThat(providerData.getProperty('code') as String, is("1234567"))
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testResolveUnknownProviderAccountRequest() {
        resolverUT.getProviderAccountRequest("fake", "1234567", "http://cool-app.stormpath.io/authorize/callback")
    }

    @DataProvider(name="providerIds")
    Object[][] createProvidersData() {
        Object[][] data = [["facebook"], ["github"], ["google"], ["linkedin"]] as Object[][]
        return data
    }
}
