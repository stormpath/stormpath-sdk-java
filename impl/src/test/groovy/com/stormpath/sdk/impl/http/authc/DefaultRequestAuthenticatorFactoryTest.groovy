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
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.lang.reflect.Modifier

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class DefaultRequestAuthenticatorFactoryTest {

    @Test
    public void test() {
        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();

        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        assertTrue(requestAuthenticatorFactory.create(AuthenticationScheme.BASIC, apiKeyCredentials) instanceof BasicRequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(AuthenticationScheme.SAUTHC1, apiKeyCredentials) instanceof SAuthc1RequestAuthenticator)
        assertTrue(requestAuthenticatorFactory.create(null, apiKeyCredentials) instanceof SAuthc1RequestAuthenticator)
    }

    @Test
    public void testException() {
        def authenticationScheme = AuthenticationScheme.BASIC

        //Let's remove the final modifier for requestAuthenticatorClassName attribute
        setNewValue(authenticationScheme, "requestAuthenticatorClassName", "this.package.does.not.exist.FooClass")

        def requestAuthenticatorFactory = new DefaultRequestAuthenticatorFactory();
        def apiKeyCredentials = createStrictMock(ApiKeyCredentials)

        try {
            requestAuthenticatorFactory.create(authenticationScheme, apiKeyCredentials) instanceof BasicRequestAuthenticator
            fail("Should have thrown RuntimeException")
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "There was an error instantiating this.package.does.not.exist.FooClass")
        }
    }

    //Set new value to final field
    private void setNewValue(Object object, String fieldName, Object value){
        Field field = object.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        int modifiers = field.getModifiers();
        Field modifierField = field.getClass().getDeclaredField("modifiers");
        modifiers = modifiers & ~Modifier.FINAL;
        modifierField.setAccessible(true);
        modifierField.setInt(field, modifiers);
        field.set(object, value);
    }

}
