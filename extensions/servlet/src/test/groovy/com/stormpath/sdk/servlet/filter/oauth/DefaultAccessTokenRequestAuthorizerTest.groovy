package com.stormpath.sdk.servlet.filter.oauth

import com.stormpath.sdk.servlet.authz.RequestAuthorizer
import com.stormpath.sdk.servlet.http.Resolver
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.testng.PowerMockTestCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.isA
import static org.easymock.EasyMock.same
import static org.powermock.api.easymock.PowerMock.mockStatic
import static org.powermock.api.easymock.PowerMock.createMock
import static org.powermock.api.easymock.PowerMock.replay
import static org.powermock.api.easymock.PowerMock.reset
import static org.powermock.api.easymock.PowerMock.verify

/**
 * @since 1.0.RC9
 */
@PrepareForTest(LoggerFactory.class)
class DefaultAccessTokenRequestAuthorizerTest extends PowerMockTestCase {

    @Test
    void testAssertSecureWithInsecureRequestAndSecureRequired() {

        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        def reqAuthz = createMock(RequestAuthorizer.class)
        def resolver = createMock(Resolver.class)

        expect(request.isSecure()).andReturn(false)
        expect(resolver.get(same(request), same(response))).andReturn(true)

        def authorizer = new DefaultAccessTokenRequestAuthorizer(resolver, reqAuthz)

        replay request, response, reqAuthz, resolver

        try {
            authorizer.assertSecure(request, response)
        } catch (OAuthException expected) {
        } finally {
            verify request, response, reqAuthz, resolver
        }
    }

    /**
     * https://github.com/stormpath/stormpath-sdk-java/issues/409
     */
    @Test
    void testAssertSecureWithInsecureRequestAndSecureNotRequired() {

        mockStatic(LoggerFactory.class)
        Logger log = createMock(Logger.class)
        expect(LoggerFactory.getLogger(isA(Class.class))).andReturn(log)

        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)
        def reqAuthz = createMock(RequestAuthorizer.class)
        def resolver = createMock(Resolver.class)

        expect(request.isSecure()).andReturn(false)
        expect(resolver.get(same(request), same(response))).andReturn(false)
        expect(log.warn(isA(String.class)))

        replay LoggerFactory.class
        replay log, request, response, reqAuthz, resolver

        def authorizer = new DefaultAccessTokenRequestAuthorizer(resolver, reqAuthz)
        authorizer.assertSecure(request, response)

        verify LoggerFactory.class
        verify log, request, response, reqAuthz, resolver
        reset LoggerFactory.class
    }
}

