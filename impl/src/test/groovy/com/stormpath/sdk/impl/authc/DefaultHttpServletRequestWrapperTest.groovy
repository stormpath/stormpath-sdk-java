package com.stormpath.sdk.impl.authc

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.testng.Assert.assertEquals

/**
 * @aince 1.0.RC9
 */
class DefaultHttpServletRequestWrapperTest {

    def httpServletRequest
    def httpServletRequestWrapper
    def KEY = "key"
    def VALUE = "value"

    @BeforeMethod
    void setup() {

        httpServletRequest = createStrictMock(HttpServletRequest)
        httpServletRequestWrapper = new DefaultHttpServletRequestWrapper(httpServletRequest)
    }

    @Test
    void testGetHttpServletRequestClass() {

        assertEquals httpServletRequestWrapper.httpServletRequestClass, HttpServletRequest
    }

    @Test
    void testGetHttpServletRequest() {

        assertEquals httpServletRequestWrapper.httpServletRequest, httpServletRequest
    }

    @Test
    void testGetHeader() {

        expect(httpServletRequest.getHeader(KEY)).andReturn(VALUE)

        replay httpServletRequest

        assertEquals httpServletRequestWrapper.getHeader(KEY), VALUE
    }

    @Test
    void testGetMethod() {

        expect(httpServletRequest.getMethod()).andReturn("GET")

        replay httpServletRequest

        assertEquals httpServletRequestWrapper.getMethod(), "GET"
    }

    @Test
    void testGetParameter() {

        expect(httpServletRequest.getParameter(KEY)).andReturn(VALUE)

        replay httpServletRequest

        assertEquals httpServletRequestWrapper.getParameter(KEY), VALUE
    }

    @Test
    void testGetParameterMap() {

        def map = [KEY: [VALUE].toArray()]

        expect(httpServletRequest.getParameterMap()).andReturn(map)

        replay httpServletRequest

        assertEquals httpServletRequestWrapper.getParameterMap(), map
    }
}
