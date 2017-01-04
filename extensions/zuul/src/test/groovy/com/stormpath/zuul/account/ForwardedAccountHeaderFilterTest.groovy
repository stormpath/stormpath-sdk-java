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
package com.stormpath.zuul.account

import com.netflix.zuul.context.RequestContext
import com.stormpath.sdk.servlet.account.AccountResolver
import com.stormpath.sdk.servlet.account.AccountStringResolver
import com.stormpath.sdk.servlet.account.DefaultAccountResolver
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.same
import static org.powermock.api.easymock.PowerMock.*
import static org.testng.Assert.*

/**
 * @since 1.1.0
 */
class ForwardedAccountHeaderFilterTest {

    @Test
    void testDefaults() {
        def filter = new ForwardedAccountHeaderFilter()
        assertEquals filter.getHeaderName(), 'X-Forwarded-Account'
        assertTrue filter.accountResolver instanceof DefaultAccountResolver
        assertTrue filter.getValueResolver() instanceof AccountStringResolver
    }

    @Test
    void testSetAccountResolver() {
        def filter = new ForwardedAccountHeaderFilter()
        def resolver = createMock(AccountResolver)
        filter.setAccountResolver(resolver)
        assertSame filter.accountResolver, resolver
    }

    @Test
    void testShouldFilterWhenAccountExists() {

        def resolver = createMock(AccountResolver)
        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)

        RequestContext reqctx = new RequestContext()
        reqctx.setRequest(request)
        reqctx.setResponse(response)
        reqctx.testSetCurrentContext(reqctx)

        expect(resolver.hasAccount(same(request))).andReturn(true)

        replayAll()

        def filter = new ForwardedAccountHeaderFilter()
        filter.setAccountResolver(resolver)

        assertTrue filter.shouldFilter()

        verifyAll()
    }

    @Test
    void testShouldFilterWhenAccountDoesNotExist() {

        def resolver = createMock(AccountResolver)
        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)

        RequestContext reqctx = new RequestContext()
        reqctx.setRequest(request)
        reqctx.setResponse(response)
        reqctx.testSetCurrentContext(reqctx)

        expect(resolver.hasAccount(same(request))).andReturn(false)

        replayAll()

        def filter = new ForwardedAccountHeaderFilter()
        filter.setAccountResolver(resolver)

        assertFalse filter.shouldFilter()

        verifyAll()
    }
}
