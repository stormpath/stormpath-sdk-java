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
package com.stormpath.sdk.servlet.http.authc

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.*

class DefaultAuthorizationHeaderParserTest {

    DefaultAuthorizationHeaderParser parser;

    @BeforeMethod
    void setUp() {
        parser = new DefaultAuthorizationHeaderParser();
    }

    @Test
    void testNullArg() {
        assertNull parser.parse(null)
    }

    @Test
    void testEmptyArg() {
        try {
            assertNull parser.parse('')
            fail()
        } catch (IllegalArgumentException e) {
            assertEquals e.message, 'schemeName cannot be null, empty or whitespace only.'
        }
    }

    @Test
    void testWhitespaceArg() {
        try {
            assertNull parser.parse('      ')
            fail()
        } catch (IllegalArgumentException e) {
            assertEquals e.message, 'schemeName cannot be null, empty or whitespace only.'
        }
    }

    @Test
    void testDefault() {
        def creds = parser.parse('Basic foo:bar')
        assertNotNull creds
        assertEquals creds.schemeName, 'Basic'
        assertEquals creds.schemeValue, 'foo:bar'
    }

    @Test
    void testSchemeOnly() {
        def creds = parser.parse('Basic')
        assertNotNull creds
        assertEquals creds.schemeName, 'Basic'
        assertNull creds.schemeValue
    }

    @Test
    void testComplexExample() {
        def creds = parser.parse('Newauth    realm="apps", type=1, title="Login to \\"apps\\""   ')
        assertNotNull creds
        assertEquals creds.schemeName, 'Newauth'
        assertEquals creds.schemeValue, 'realm="apps", type=1, title="Login to \\"apps\\""'
    }
}
