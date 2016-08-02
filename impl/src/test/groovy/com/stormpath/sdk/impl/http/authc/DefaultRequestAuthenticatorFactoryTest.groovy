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
package com.stormpath.sdk.impl.http.authc

import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.AuthenticationSchemes
import com.stormpath.sdk.client.BasicAuthenticationScheme
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class DefaultRequestAuthenticatorFactoryTest {

    @Test
    public void test() {
        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();

        assertTrue(requestAuthenticatorFactory.create(AuthenticationSchemes.BASIC) instanceof BasicRequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(AuthenticationSchemes.SAUTHC1) instanceof SAuthc1RequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(null) instanceof SAuthc1RequestAuthenticator)
    }

    @Test
    public void testException() {

        def someUnexistentAuthenticationScheme = new AuthenticationScheme() {
            @Override
            String getRequestAuthenticatorClassName() {
                return "this.package.does.not.exist.FooClass";
            }
        }

        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();

        try {
            requestAuthenticatorFactory.create(someUnexistentAuthenticationScheme) instanceof BasicRequestAuthenticator
            fail("Should have thrown RuntimeException")
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "There was an error instantiating this.package.does.not.exist.FooClass")
        }
    }
}
