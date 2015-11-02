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
package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.oauth.JwtAuthenticationRequest
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * Test for JwtAuthenticationRequestBuilder class
 *
 * @since 1.0.RC6
 */
class JwtAuthenticationRequestBuilderTest {

    @Test
    void testError(){
        def builder = new DefaultJwtAuthenticationRequestBuilder()

        try {
            builder.build()
            fail("Should have failed")
        } catch (IllegalArgumentException e){
            assertEquals(e.getMessage(), "jwt has not been set. It is a required value.")
        }
    }

    @Test
    void testMethods(){
        def builder = new DefaultJwtAuthenticationRequestBuilder()

        builder.forLocalValidation()
        builder.setJwt("test_JwtAuthenticationRequestBuilder")
        JwtAuthenticationRequest request = builder.build()

        assertEquals request.getJwt(), "test_JwtAuthenticationRequestBuilder"
        assertTrue request.getForLocalValidation()
    }
}
