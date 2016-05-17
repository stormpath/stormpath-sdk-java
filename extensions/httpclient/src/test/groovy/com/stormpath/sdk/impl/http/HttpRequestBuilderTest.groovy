/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.http

import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequestBuilder
import com.stormpath.sdk.http.HttpRequests
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * Test for the {@link HttpRequestBuilder} class that uses utility methods in {@link HttpRequests} class
 *
 * @since 1.0.RC4.6
 */
class HttpRequestBuilderTest {

    @Test
    void TestErrors(){

        try {
            HttpRequests.method(null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("method argument is required")
        }

        try {
            HttpRequests.method(HttpMethod.GET).addHeader("name", null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("value argument is required")
        }

        try {
            HttpRequests.method(HttpMethod.GET).addHeader(null, "value");
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("key argument is required")
        }

        try {
            HttpRequests.method(HttpMethod.GET).addParameter("name", null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("value argument is required")
        }

        try {
            HttpRequests.method(HttpMethod.GET).addParameter(null, "value");
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("key argument is required")
        }
    }

    @Test
    void testConstructors(){
        def builder = HttpRequests.method(HttpMethod.POST)
        assertTrue builder instanceof HttpRequestBuilder

        String[] value = ['test']
        builder = HttpRequests.method(HttpMethod.GET).addParameter("name", value)
        assertTrue builder instanceof HttpRequestBuilder
    }

    @Test
    void testDefault(){
        def request = HttpRequests.method(HttpMethod.POST).build()
        assertNotNull request
        assert request.getMethod().equals(HttpMethod.POST)

        String[] headerValue = ['testHeader']
        request = HttpRequests
                .method(HttpMethod.POST)
                .addHeader("name", headerValue).build()
        assertNotNull request
        assertEquals request.headers.size(), 1
        assertEquals request.headers.get("name").getAt(0), 'testHeader'

        String[] paramValue = ['testParam']
        request = HttpRequests
                .method(HttpMethod.POST)
                .addParameter("paramName", paramValue).build()
        assertNotNull request
        assertEquals request.parameters.size(), 1
        assertEquals request.parameters.get("paramName").getAt(0), 'testParam'

        request = HttpRequests
                .method(HttpMethod.POST)
                .addHeader("addHeader", headerValue)
                .addParameter("param", paramValue)
                .build()

        assertEquals request.parameters.size(), 1
        assertEquals request.headers.size(), 1
        assertEquals request.headers.get("addHeader").getAt(0), 'testHeader'
        assertEquals request.parameters.get("param").getAt(0), 'testParam'
    }
}
