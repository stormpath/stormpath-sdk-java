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
package com.stormpath.sdk.convert

import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.3.0
 */
class ConversionTest {

    @Test
    void testCoverage() {
        new Conversions(); //only for code coverage
    }

    @Test
    void testName() {
        String name = 'foo'
        def c = new Conversion(name: name)
        assertEquals c.getName(), name
    }

    @Test
    void testStrategyString() {
        String s = 'all'
        def c = new Conversion()
        assertSame c.getStrategy(), ConversionStrategyName.SCALARS //default
        c.setStrategy(s)
        assertSame c.getStrategy(), ConversionStrategyName.ALL
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testInvalidStrategyString() {
        new Conversion().setStrategy('foo')
    }

    @Test
    void testSetFields() {
        def fields = ['foo': new Conversion()]
        def c = new Conversion(fields: fields)
        assertSame c.getFields(), fields
    }

    @Test
    void testNullFieldsSetsEmptyMap() {
        def c = new Conversion(fields: null)
        assertNotNull c.getFields()
        assertTrue c.getFields().isEmpty()
    }
}
