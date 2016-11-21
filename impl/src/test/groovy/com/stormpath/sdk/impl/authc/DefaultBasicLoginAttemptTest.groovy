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

import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.alpha
 */
class DefaultBasicLoginAttemptTest {

    @Test
    void testSetValue() {
        String value = "fmxwtWNhcNQ6Q2hhbmdlbWDx"
        def internalDataStore = createMock(InternalDataStore)
        def properties = new HashMap<String, Object>()

        def attempt = new DefaultBasicLoginAttempt(internalDataStore, properties)
        attempt.setValue(value)
        assertEquals(attempt.getValue(), value)
    }

    @Test
    void testSetAccountStoreNull() {
        def internalDataStore = createMock(InternalDataStore)

        def attempt = new DefaultBasicLoginAttempt(internalDataStore)

        attempt.setValue(null)
        assertEquals(attempt.getValue(), null)
    }

    @Test
    void testGetPropertyDescriptors() {
        def internalDataStore = createMock(InternalDataStore)

        def attempt = new DefaultBasicLoginAttempt(internalDataStore)

        def map = attempt.getPropertyDescriptors()
        assertEquals(map.size(), 3)
        assertTrue(map.get("type").getType().getName().equals("java.lang.String"))
        assertTrue(map.get("value").getType().getName().equals("java.lang.String"))
        assertTrue(map.get("accountStore").getType().getName().equals("java.util.Map"))

    }

}
