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
package com.stormpath.sdk.impl.http

import com.stormpath.sdk.http.QueryString
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull

/**
 * @since 0.1
 */
class QueryStringTest {

    @Test
    void testUnpopulated() {
        assertEquals new QueryString().toString(), ""
    }

    @Test
    void testNoPairs() {
        def query = "testParam"
        def qs = QueryString.create(query);
        assertEquals qs.toString(), "testParam="
    }

    @Test
    void testTwoTokensOnePair() {
        def query = "foobar&test=alpha"
        def qs = QueryString.create(query);
        assertEquals qs.toString(), "foobar=&test=alpha"
    }

    @Test
    void testLexicographicalSorting() {
        def query = "test=value&foo=bar"
        def qs = QueryString.create(query);
        assertEquals qs.toString(), "foo=bar&test=value"

        query = "foo=bar&Test=value"
        qs = QueryString.create(query);
        assertEquals qs.toString(), "Test=value&foo=bar"
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testWithEmptySource() {
        def qs = new QueryString(new HashMap<String, Object>())
        assertEquals qs.toString(), ""
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testWithSourceNullValue() {
        def query = ["foo":null]
        def qs = new QueryString(query)
        assertEquals qs.toString(), "foo="
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testCreateWithEmptyQuery() {
        assertNull QueryString.create("")
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testCreateWithKeyOnly() {
        assertEquals QueryString.create("foo").toString(), "foo="
    }
}
