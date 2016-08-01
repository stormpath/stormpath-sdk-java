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
package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createMockBuilder
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertSame

/**
 * @since 1.0.alpha
 */
class AbstractLoginAttemptTest {

    @Test
    void testSetTypeNull() {
        def internalDataStore = createMock(InternalDataStore)
        def properties = new HashMap<String, Object>()

        AbstractLoginAttempt attempt = createMockBuilder(AbstractLoginAttempt.class)
                .withConstructor(internalDataStore, properties).createMock();

        attempt.setType(null)
        assertEquals(attempt.getType(), null)
    }

    @Test
    void testSetType() {
        String type = "basic"
        def internalDataStore = createMock(InternalDataStore)
        def properties = new HashMap<String, Object>()

        AbstractLoginAttempt attempt = createMockBuilder(AbstractLoginAttempt.class)
                .withConstructor(internalDataStore, properties).createMock();

        attempt.setType(type)
        assertEquals(attempt.getType(), type)
    }

    @Test
    void testSetAccountStoreNull() {
        def internalDataStore = createMock(InternalDataStore)
        AbstractLoginAttempt attempt = createMockBuilder(AbstractLoginAttempt.class).withConstructor(internalDataStore).createMock();

        attempt.setType(null)
        assertEquals(attempt.getProperty(AbstractLoginAttempt.ACCOUNT_STORE.name), null)
    }

    @Test
    void testSetAccountStore() {
        def accountStore = createMock(AccountStore)
        def internalDataStore = createMock(InternalDataStore)
        AbstractLoginAttempt attempt = createMockBuilder(AbstractLoginAttempt.class).withConstructor(internalDataStore).createMock();

        expect(accountStore.getHref()).andReturn("http://someurl.com/").times(2)

        replay accountStore

        assertEquals(attempt.getProperty(AbstractLoginAttempt.ACCOUNT_STORE.name), null)

        attempt.setAccountStore(accountStore)

        assertSame(attempt.getProperty(AbstractLoginAttempt.ACCOUNT_STORE.name).href, accountStore.href)

        verify accountStore
    }

}
