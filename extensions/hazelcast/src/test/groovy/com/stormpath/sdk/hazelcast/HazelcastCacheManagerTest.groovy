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
package com.stormpath.sdk.hazelcast

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

class HazelcastCacheManagerTest {

    @Test
    void testDefault() {
        def instance = new HazelcastCacheManager();
        assertNull instance.hazelcastInstance
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullCtorArg() {
        new HazelcastCacheManager(null);
    }

    @Test
    void testValidCtorArg() {
        def hz = createStrictMock(HazelcastInstance)
        def instance = new HazelcastCacheManager(hz)
        assertSame instance.hazelcastInstance, hz
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testSetHazelcastInstanceWithNullArg() {
        def cm = new HazelcastCacheManager();
        cm.setHazelcastInstance(null)
    }

    @Test
    void testSetHazelcastInstanceWithValidArg() {
        def hz = createStrictMock(HazelcastInstance)
        def instance = new HazelcastCacheManager()
        instance.setHazelcastInstance(hz)
        assertSame instance.hazelcastInstance, hz
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetCacheWithNullName() {
        def cm = new HazelcastCacheManager()
        cm.getCache(null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetCacheWithEmptyName() {
        def cm = new HazelcastCacheManager()
        cm.getCache('')
    }

    @Test
    void testGetCacheWithValidName() {
        def hz = createStrictMock(HazelcastInstance)
        def imap = createStrictMock(IMap)
        def cm = new HazelcastCacheManager(hz)

        expect(hz.getMap(eq('foo'))).andReturn(imap)

        replay hz, imap

        def cache = cm.getCache('foo')

        assertNotNull cache
        assertTrue cache instanceof HazelcastCache

        verify hz, imap
    }
}
