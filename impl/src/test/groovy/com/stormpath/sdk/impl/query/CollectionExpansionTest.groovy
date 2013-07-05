/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.query

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 0.8
 */
class CollectionExpansionTest {

    @Test
    void testDefault() {
        def e = new CollectionExpansion('foo', 2, 20)
        assertEquals 2, e.limit
        assertEquals 20, e.offset
        assertEquals e.toString(), 'foo(offset:20,limit:2)'
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullName() {
        new CollectionExpansion(null, 1, 1)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testZeroValues() {
        new CollectionExpansion('foo', 0, 0);
    }

}
