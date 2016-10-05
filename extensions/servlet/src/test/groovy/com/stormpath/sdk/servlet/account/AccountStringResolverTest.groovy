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
package com.stormpath.sdk.servlet.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.lang.Function
import com.stormpath.sdk.servlet.json.ResourceJsonFunction
import com.stormpath.sdk.servlet.mvc.ResourceMapFunction
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.same
import static org.easymock.EasyMock.verify
import static org.testng.Assert.*

/**
 * @since 1.1.0
 */
class AccountStringResolverTest {

    @Test
    void testDefaults() {
        def resolver = new AccountStringResolver()
        assertTrue resolver.accountResolver instanceof DefaultAccountResolver
        assertTrue resolver.accountStringFunction instanceof ResourceJsonFunction
        assertTrue resolver.accountStringFunction.mapFunction instanceof ResourceMapFunction
    }

    @Test
    void testSetAccountStringFunction() {
        def resolver = new AccountStringResolver()
        def fn = createMock(Function)
        resolver.setAccountStringFunction(fn)
        assertSame resolver.accountStringFunction, fn
    }

    @Test
    void testSetAccountResolver() {
        def resolver = new AccountStringResolver()
        def delegate = createMock(AccountResolver)
        resolver.setAccountResolver(delegate)
        assertSame resolver.accountResolver, delegate
    }

    @Test
    void testGetWithoutAccount() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def acctResolver = createMock(AccountResolver)

        expect(acctResolver.getAccount(same(request))).andReturn(null)
        replay acctResolver, request

        def resolver = new AccountStringResolver()
        resolver.setAccountResolver(acctResolver)

        assertNull resolver.get(request, response)

        verify acctResolver, request
    }

    @Test
    void testGetWithAccount() {

        def request = createMock(HttpServletRequest)
        def response = createMock(HttpServletResponse)
        def acctResolver = createMock(AccountResolver)
        def fn = createMock(Function)
        def account = createMock(Account)

        expect(acctResolver.getAccount(same(request))).andReturn(account)
        expect(fn.apply(same(account))).andReturn('foo')
        replay acctResolver, request, account, fn

        def resolver = new AccountStringResolver()
        resolver.setAccountResolver(acctResolver)
        resolver.setAccountStringFunction(fn)

        assertEquals resolver.get(request, response), 'foo'

        verify acctResolver, request, account, fn
    }
}
