/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.config

import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent
import com.stormpath.sdk.servlet.event.RequestEvent
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.mvc.ErrorModel
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory
import com.stormpath.sdk.servlet.mvc.FormController
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.RedirectStrategy
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

import static com.stormpath.sdk.servlet.mvc.Controller.NEXT_QUERY_PARAM
import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.reset
import static org.easymock.EasyMock.verify

/**
 * @since 1.0.0
 */
class StormpathAuthenticationFailureHandlerTest {

    static final String ME_URI = "/me"

    static final String NEXT_VALUE = "/restricted"

    StormpathAuthenticationFailureHandler failureHandler

    Publisher<RequestEvent> publisher

    ErrorModelFactory errorModelFactory

    RedirectStrategy redirectStrategy

    ContentNegotiationResolver contentNegotiationResolver

    AuthenticationException authenticationException = new BadCredentialsException("bad bad bad")

    @BeforeMethod
    void setup() {
        publisher = createMock(Publisher)
        errorModelFactory = createMock(ErrorModelFactory)
        redirectStrategy = createMock(RedirectStrategy)
        contentNegotiationResolver = createMock(ContentNegotiationResolver)

        failureHandler = new StormpathAuthenticationFailureHandler(
                "/login",
                publisher,
                errorModelFactory,
                "application/json,text/html"
        )

        failureHandler.setRedirectStrategy(redirectStrategy)
        failureHandler.setContentNegotiationResolver(contentNegotiationResolver)
        failureHandler.setMeUri(ME_URI)
    }

    @Test
    void testOnAuthenticationFailureJsonResponse() {
        HttpServletRequest mockRequest = createMock(HttpServletRequest)
        HttpServletResponse mockResponse = createMock(HttpServletResponse)
        RequestDispatcher mockRequestDispatcher = createMock(RequestDispatcher)

        expect(contentNegotiationResolver.getContentType(mockRequest, mockResponse, [MediaType.APPLICATION_JSON, MediaType.TEXT_HTML])).andReturn(MediaType.APPLICATION_JSON)
        expect(mockRequest.getRequestURI()).andReturn(ME_URI)
        expect(mockRequest.getRequestDispatcher(ME_URI)).andReturn mockRequestDispatcher
        expect(mockRequestDispatcher.forward(mockRequest, mockResponse))
        expect(publisher.publish(anyObject(FailedAuthenticationRequestEvent)))

        replay mockRequest, mockResponse, mockRequestDispatcher, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver

        failureHandler.onAuthenticationFailure(mockRequest, mockResponse, authenticationException)

        verify mockRequest, mockResponse, mockRequestDispatcher, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver
    }

    @Test
    void testOnAuthenticationFailureHtmlResponseWithNextParam() {
        HttpServletRequest mockRequest = createMock(HttpServletRequest)
        HttpServletResponse mockResponse = createMock(HttpServletResponse)
        RequestDispatcher mockRequestDispatcher = createMock(RequestDispatcher)
        HttpSession mockSession = createMock(HttpSession)
        ErrorModel errorModel = ErrorModel.builder().build()

        expect(contentNegotiationResolver.getContentType(mockRequest, mockResponse, [MediaType.APPLICATION_JSON, MediaType.TEXT_HTML])).andReturn(MediaType.TEXT_HTML)
        expect(mockRequest.getSession()).andReturn(mockSession)
        expect(errorModelFactory.toError(mockRequest, authenticationException)).andReturn errorModel
        expect(mockSession.setAttribute(FormController.SPRING_SECURITY_AUTHENTICATION_FAILED_KEY, errorModel))
        expect(mockRequest.getParameter(NEXT_QUERY_PARAM)).andReturn(NEXT_VALUE)
        expect(mockRequest.getRequestDispatcher("/login?next=%2Frestricted")).andReturn(mockRequestDispatcher)
        expect(publisher.publish(anyObject(FailedAuthenticationRequestEvent)))

        replay mockRequest, mockResponse, mockSession, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver


        failureHandler.onAuthenticationFailure(mockRequest, mockResponse, authenticationException)

        verify mockRequest, mockResponse, mockSession, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver
    }

    @Test
    void testOnAuthenticationFailureHtmlResponseWithoutNextParam() {
        HttpServletRequest mockRequest = createMock(HttpServletRequest)
        HttpServletResponse mockResponse = createMock(HttpServletResponse)
        RequestDispatcher mockRequestDispatcher = createMock(RequestDispatcher)
        HttpSession mockSession = createMock(HttpSession)
        ErrorModel errorModel = ErrorModel.builder().build()

        //Empty next values
        [null, ""].each { nextValue ->
            expect(contentNegotiationResolver.getContentType(mockRequest, mockResponse, [MediaType.APPLICATION_JSON, MediaType.TEXT_HTML])).andReturn(MediaType.TEXT_HTML)
            expect(mockRequest.getSession()).andReturn(mockSession)
            expect(errorModelFactory.toError(mockRequest, authenticationException)).andReturn errorModel
            expect(mockSession.setAttribute(FormController.SPRING_SECURITY_AUTHENTICATION_FAILED_KEY, errorModel))
            expect(mockRequest.getParameter(NEXT_QUERY_PARAM)).andReturn(nextValue)
            expect(mockRequest.getRequestDispatcher("/login")).andReturn(mockRequestDispatcher)
            expect(publisher.publish(anyObject(FailedAuthenticationRequestEvent)))

            replay mockRequest, mockResponse, mockSession, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver

            failureHandler.onAuthenticationFailure(mockRequest, mockResponse, authenticationException)

            verify mockRequest, mockResponse, mockSession, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver

            reset  mockRequest, mockResponse, mockSession, publisher, errorModelFactory, redirectStrategy, contentNegotiationResolver
        }


    }
}
