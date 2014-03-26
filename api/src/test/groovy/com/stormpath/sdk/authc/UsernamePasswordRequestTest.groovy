/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.authc

import com.stormpath.sdk.directory.AccountStore
import org.junit.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 0.9.4
 */
class UsernamePasswordRequestTest {

    @Test
    void testAccountStore() {
        def accountStore = createMock(AccountStore)

        //Password String
        UsernamePasswordRequest request = new UsernamePasswordRequest("username", "passwd");
        assertEquals(request.getAccountStore(), null)

        request = new UsernamePasswordRequest("username", "passwd", "someHost");
        assertEquals(request.getAccountStore(), null)

        request = new UsernamePasswordRequest("username", "passwd", accountStore);
        assertSame(request.getAccountStore(), accountStore)

        request = new UsernamePasswordRequest("username", "passwd", null, accountStore);
        assertSame(request.getAccountStore(), accountStore)

        //Password char[]
        request = new UsernamePasswordRequest("username", "passwd".toCharArray());
        assertEquals(request.getAccountStore(), null)

        request = new UsernamePasswordRequest("username", "passwd".toCharArray(), "someHost");
        assertSame(request.getAccountStore(), null)

        request = new UsernamePasswordRequest("username", "passwd".toCharArray(), accountStore);
        assertSame(request.getAccountStore(), accountStore)

        request = new UsernamePasswordRequest("username", "passwd".toCharArray(), null, accountStore);
        assertSame(request.getAccountStore(), accountStore)
    }

    @Test
    void testClear() {
        def accountStore = createMock(AccountStore)

        UsernamePasswordRequest request = new UsernamePasswordRequest("username", "passwd", accountStore);
        assertEquals(request.getAccountStore(), accountStore)

        request.clear()

        assertEquals(request.getAccountStore(), null)
    }

}
