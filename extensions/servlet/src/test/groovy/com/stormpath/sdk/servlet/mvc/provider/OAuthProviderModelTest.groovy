package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.OAuthProvider
import org.testng.annotations.Test

import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals

class OAuthProviderModelTest {

    @Test
    void testInitialization() {

        OAuthProvider provider = createNiceMock(OAuthProvider)
        expect(provider.clientId).andStubReturn("1234")
        expect(provider.href).andStubReturn("http://api.stormpath.com/provider/uid")
        expect(provider.providerId).andStubReturn("fakey")
        expect(provider.scope).andStubReturn(["foo", "bar", "baz"])
        replay provider
        def model = new DefaultOAuthProviderModel(provider)
        assertEquals(model.clientId, "1234")
        assertEquals(model.href, "http://api.stormpath.com/provider/uid")
        assertEquals(model.providerId, "fakey")
        assertEquals(model.scope, ["foo", "bar", "baz"])
    }


}
