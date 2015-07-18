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
package com.stormpath.sdk.impl.http.httpclient

import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.http.HttpRequestBuilder
import com.stormpath.sdk.http.HttpRequests
import org.testng.annotations.Test

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static junit.framework.Assert.assertTrue
import static org.testng.Assert.fail
import static org.testng.Assert.fail
import static org.testng.Assert.fail
import static org.testng.Assert.fail
import static org.testng.Assert.fail

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
            HttpRequests.header("name", null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("value argument is required")
        }

        try {
            HttpRequests.header(null, "value");
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("name argument is required")
        }

        try {
            HttpRequests.parameter("name", null);
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("value argument is required")
        }

        try {
            HttpRequests.parameter(null, "value");
            fail("Exception expected.");
        } catch (IllegalArgumentException expected) {
            String msg = expected.getMessage()
            assertTrue msg.startsWith("name argument is required")
        }
    }

    @Test
    void testConstructors(){
        def builder = HttpRequests.method(HttpMethod.POST)
        assertTrue builder instanceof HttpRequestBuilder

        String[] value = ['test']
        builder = HttpRequests.parameter("name", value)
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
                .header("name", headerValue).build()
        assertNotNull request
        assertEquals 1, request.headers.size()
        assertEquals 'testHeader', request.headers.get("name").getAt(0)

        String[] paramValue = ['testParam']
        request = HttpRequests
                .method(HttpMethod.POST)
                .parameter("paramName", paramValue).build()
        assertNotNull request
        assertEquals 1, request.parameters.size()
        assertEquals 'testParam', request.parameters.get("paramName").getAt(0)

        request = HttpRequests
                .method(HttpMethod.POST)
                .header("header", headerValue)
                .parameter("param", paramValue)
                .build()

        assertEquals 1, request.parameters.size()
        assertEquals 1, request.headers.size()
        assertEquals 'testHeader', request.headers.get("header").getAt(0)
        assertEquals 'testParam', request.parameters.get("param").getAt(0)
    }
}
