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
package com.stormpath.sdk.impl.application

import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultCreateApplicationRequestBuilderTest {

    @Test
    void testBuilder() {
        def app = new DefaultApplication(createStrictMock(InternalDataStore))

        def request = new DefaultCreateApplicationRequestBuilder(app)
        assertFalse(request.createDirectory)
        assertNull(request.directoryName)
        assertTrue(request.build() instanceof DefaultCreateApplicationRequest)

        request = new DefaultCreateApplicationRequestBuilder(app).createDirectoryNamed("My Directory")
        assertTrue(request.createDirectory)
        assertEquals(request.directoryName, "My Directory")
        assertTrue(request.build() instanceof DefaultCreateApplicationRequest)

        request = new DefaultCreateApplicationRequestBuilder(app).createDirectory()
        assertTrue(request.createDirectory)
        assertNull(request.directoryName)
        assertTrue(request.build() instanceof CreateApplicationAndDirectoryRequest)

    }

}
