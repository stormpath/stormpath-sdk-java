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

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.1.0
 */
class JsonFunctionTest {

    @Test
    void testScalar() {
        def fn = new JsonFunction()
        assertEquals fn.apply('foo'), '"foo"'
    }

    @Test
    void testMap() {
        def fn = new JsonFunction()
        assertEquals fn.apply([foo: 42]), '{"foo":42}'
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInvalidValue() {
        def fn = new JsonFunction()
        fn.apply(new Object())
    }
}
