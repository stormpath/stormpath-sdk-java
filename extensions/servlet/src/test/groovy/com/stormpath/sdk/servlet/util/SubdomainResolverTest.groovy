package com.stormpath.sdk.servlet.util

import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC5
 */
class SubdomainResolverTest {

    @Test
    void testApexDomain() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('foo.com')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testApexDomainWithPort() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('foo.com:443')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testSubdomainWithoutSpecifyingBaseDomain() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('bar.foo.com')
        replay(request)
        assertEquals(['bar'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testMultipleSubdomainsWithoutSpecifyingBaseDomain() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('baz.bar.foo.com')
        replay(request)
        assertEquals(['baz', 'bar'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testApexBaseDomainWithImmediateSubdomain() {

        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('foo.com')

        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('bar.foo.com:443')
        replay(request)
        assertEquals( ['bar'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testApexBaseDomainWithMultipleSubdomains() {

        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('foo.com')

        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('baz.bar.foo.com:443')
        replay(request)
        assertEquals(['baz', 'bar'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testExtendedBaseDomainWithImmediateSubdomain() {

        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('w.x.y.z')

        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('v.w.x.y.z')
        replay(request)
        assertEquals( ['v'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testExtendedBaseDomainWithMultipleSubdomains() {
        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('w.x.y.z')
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('t.u.v.w.x.y.z')
        replay(request)
        assertEquals(['t', 'u', 'v'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testApexDomainWithMissingHostHeader() { //tests http 1.0
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn(null)
        expect(request.getServerName()).andStubReturn('foo.com')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testMismatchDomainNames() {
        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('foo.com')
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('bar.test.com')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testLocalhost() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('localhost')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testLocalhostWithProductionBaseDomain() {
        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('foo.com')
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('localhost')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testLocalhostLocaldomain() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('localhost.localdomain')
        replay(request)
        assertEquals(['localhost'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testLocalhostLocaldomainWithProductionBaseDomain() {
        def resolver = new SubdomainResolver();
        resolver.setBaseDomainName('foo.com')
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('localhost.localdomain')
        replay(request)
        assertEquals(['localhost'], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testIpv4Host() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('192.168.1.1')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testFullIpv6Host() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('FE80:0000:0000:0000:0202:B3FF:FE1E:8329')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testCollapsedIpv6Host() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('FE80::0202:B3FF:FE1E:8329')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }

    @Test
    void testIpv6HostWithPort() {
        def resolver = new SubdomainResolver();
        def request = createMock(HttpServletRequest)
        expect(request.getHeader(eq('Host'))).andStubReturn('[2001:db8:0:1]:443')
        replay(request)
        assertEquals([], resolver.get(request, null))
        verify(request)
    }
}
