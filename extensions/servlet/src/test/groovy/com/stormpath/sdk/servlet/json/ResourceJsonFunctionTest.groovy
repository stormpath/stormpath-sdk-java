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
package com.stormpath.sdk.servlet.json

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.lang.Function
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.same
import static org.easymock.EasyMock.verify
import static org.testng.Assert.*

/**
 * @since 1.1.0
 */
class ResourceJsonFunctionTest {

    @Test
    void testApplyWithNull() {

        def mapFn = createMock(Function)
        def jsonFn = createMock(Function)

        replay mapFn, jsonFn

        def fn = new ResourceJsonFunction(mapFn, jsonFn)

        assertNull fn.apply(null)

        verify mapFn, jsonFn
    }

    @Test
    void testApply() {

        def mapFn = createMock(Function)
        def jsonFn = createMock(Function)
        def map = createMock(Map)
        def account = createMock(Account)

        expect(mapFn.apply(same(account))).andReturn(map)
        expect(jsonFn.apply(same(map))).andReturn('foo')

        replay mapFn, jsonFn, map, account

        def fn = new ResourceJsonFunction(mapFn, jsonFn)

        assertEquals fn.apply(account), 'foo'

        verify mapFn, jsonFn, map, account
    }
}
