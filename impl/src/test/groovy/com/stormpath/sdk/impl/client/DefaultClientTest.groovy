package com.stormpath.sdk.impl.client

import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.impl.api.ApiKeyResolver
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.stormpath.sdk.impl.util.BaseUrlResolver
import com.stormpath.sdk.lang.Classes
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.testng.annotations.Test

import static org.easymock.EasyMock.expect
import static org.powermock.api.easymock.PowerMock.createStrictMock
import static org.powermock.api.easymock.PowerMock.mockStatic
import static org.powermock.api.easymock.PowerMock.replayAll
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
@PrepareForTest(Classes)
class DefaultClientTest extends PowerMockTestCase {

    @Test
    void testCreateRequestExecutor() {

        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)
        def apiKeyResolver = createStrictMock(ApiKeyResolver)
        def cacheManager = createStrictMock(CacheManager)
        def requestAuthenticatorFactory = createStrictMock(RequestAuthenticatorFactory)
        def baseUrlResolver = createStrictMock(BaseUrlResolver)

        def className = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor"

        mockStatic(Classes)
        expect(Classes.isAvailable(className)).andReturn(false)

        replayAll()

        try {
            new DefaultClient(apiKeyCredentials, apiKeyResolver, baseUrlResolver, null, cacheManager, AuthenticationScheme.BASIC, requestAuthenticatorFactory, 3600)
            fail("shouldn't be here")
        } catch (Exception e) {
            assertEquals e.getMessage(), "Unable to find the '" + className +
                "' implementation on the classpath.  Please ensure you " +
                "have added the stormpath-sdk-httpclient .jar file to your runtime classpath."
        }
    }
}
