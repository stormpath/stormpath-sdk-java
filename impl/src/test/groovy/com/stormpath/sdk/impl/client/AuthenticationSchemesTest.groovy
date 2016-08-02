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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.AuthenticationSchemes
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.SAuthc1RequestAuthenticator
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertTrue

class AuthenticationSchemesTest {

    @Test
    void testDefaultIsSAuthC1(){
        AuthenticationScheme authenticationScheme = AuthenticationSchemes.getAuthenticationScheme(null);
        assertTrue(authenticationScheme instanceof SAuthc1RequestAuthenticator)
        authenticationScheme = AuthenticationSchemes.getAuthenticationScheme("")
        assertTrue(authenticationScheme instanceof SAuthc1RequestAuthenticator)
    }

    @Test
    void testSAuthC1SchemeNameReturnsCorrectAuthenticator(){
        AuthenticationScheme authenticationScheme = AuthenticationSchemes.getAuthenticationScheme("SAUTHC1");
        assertTrue(authenticationScheme instanceof SAuthc1RequestAuthenticator)
    }

    @Test
    void testBasicSchemeNameReturnsCorrectAuthenticator(){
        AuthenticationScheme authenticationScheme = AuthenticationSchemes.getAuthenticationScheme("BASIC");
        assertTrue(authenticationScheme instanceof BasicRequestAuthenticator)
    }

}
