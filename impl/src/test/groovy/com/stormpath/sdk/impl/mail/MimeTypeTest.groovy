/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.mail

import com.stormpath.sdk.mail.MimeType
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC4
 */
class MimeTypeTest {

    @Test
    void testValue() {

        MimeType.PLAIN_TEXT.value().equals("text/plain")
        MimeType.HTML.value().equals("text/html")
        MimeType.values().size().equals(2)

    }

    @Test
    void testFromString() {
        assertSame(MimeType.fromString("text/plain"), MimeType.PLAIN_TEXT)
        assertSame(MimeType.fromString("text/html"), MimeType.HTML)
        assertNull(MimeType.fromString("text/xml"))

        try {
            MimeType.fromString(null)
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
        }

        try {
            MimeType.fromString("")
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
        }
    }
}
