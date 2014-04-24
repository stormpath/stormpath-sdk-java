package com.stormpath.sdk.oauth

import com.stormpath.sdk.lang.UnknownClassException
import org.junit.Test

import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.alpha
 */
class GoogleAccountRequestBuilderTest {

    @Test
    void test() {
        def providerRequest = Providers.GOOGLE;
        try{
            providerRequest.accountRequest();
            fail("should have thrown since the implementation is in the impl package")
        } catch (UnknownClassException e) {
            assertTrue(e.getMessage().contains("Unable to load class named [com.stormpath.sdk.impl.oauth.DefaultGoogleAccountRequest\$Builder]"))
        }
    }
}
