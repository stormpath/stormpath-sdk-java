package com.stormpath.zuul.filter

import com.netflix.zuul.context.RequestContext
import com.stormpath.sdk.servlet.http.Resolver
import org.powermock.core.classloader.annotations.PrepareForTest
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.same
import static org.powermock.api.easymock.PowerMock.*
import static org.testng.Assert.*

@PrepareForTest(RequestContext)
class AppliedRequestHeaderFilterTest {

    @Test
    void testFilterType() {
        def filter = new AppliedRequestHeaderFilter()
        assertEquals filter.filterType(), "pre"
        filter.setFilterType('foo')
        assertEquals filter.filterType(), 'foo'
    }

    @Test
    void testFilterOrder() {
        def filter = new AppliedRequestHeaderFilter()
        assertEquals filter.filterOrder(), 0
        filter.setFilterOrder(1)
        assertEquals filter.filterOrder(), 1
    }

    @Test
    void testSetValueResolver() {
        def filter = new AppliedRequestHeaderFilter()
        assertNull filter.getHeaderName()
        filter.setHeaderName('foo')
        assertEquals filter.getHeaderName(), 'foo'
    }

    @Test
    void testSetHeaderName() {
        def filter = new AppliedRequestHeaderFilter()
        def resolver = createMock(Resolver)
        assertNull filter.getValueResolver()
        filter.setValueResolver(resolver)
        assertSame filter.getValueResolver(), resolver
    }

    @Test
    void testShouldFilter() {
        def filter = new AppliedRequestHeaderFilter()
        assertFalse filter.shouldFilter()
        def resolver = createMock(Resolver)
        filter.setValueResolver(resolver)
        assertTrue filter.shouldFilter()
    }

    @Test
    void testRun() {

        def resolver = createMock(Resolver)
        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)

        RequestContext reqctx = new RequestContext()
        reqctx.setRequest(request)
        reqctx.setResponse(response)
        reqctx.testSetCurrentContext(reqctx)

        expect(resolver.get(same(request), same(response))).andReturn('bar')

        replayAll()

        def filter = new AppliedRequestHeaderFilter()
        filter.setHeaderName('foo')
        filter.setValueResolver(resolver);

        filter.run()

        assertEquals reqctx.getZuulRequestHeaders().get('foo'), 'bar'

        verifyAll()
    }
}