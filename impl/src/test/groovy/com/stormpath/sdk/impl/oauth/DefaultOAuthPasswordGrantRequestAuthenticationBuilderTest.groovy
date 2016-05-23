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

import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.assertSame
import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.fail

/**
 * Test for PasswordGrantRequestBuilder class
 *
 * @since 1.0.RC7
 */
class DefaultOAuthPasswordGrantRequestAuthenticationBuilderTest {

    @Test
    void testBuildErrors() {

        def builder = new DefaultOAuthPasswordGrantRequestAuthenticationBuilder()

        try{
            builder.setLogin("test").build()
            fail("Should have failed");
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "password has not been set. It is a required attribute.")
        }

        builder = new DefaultOAuthPasswordGrantRequestAuthenticationBuilder()

        try{
            builder.setPassword("test").build()
            fail("Should have failed");
        } catch (IllegalStateException e){
            assertEquals e.getMessage(), "login has not been set. It is a required attribute."
        }
    }

    @Test
    void testMethods(){
        def builder = new DefaultOAuthPasswordGrantRequestAuthenticationBuilder()

        def accountStore = createMock(AccountStore)

        builder.setLogin("test_login")
        builder.setPassword("test_pwd")
        builder.setAccountStore(accountStore)

        OAuthPasswordGrantRequestAuthentication request = builder.build()

        assertSame request.getAccountStore(), accountStore
        assertEquals request.getLogin(), "test_login"
        assertEquals request.getPassword(), "test_pwd"
        assertEquals request.getGrantType(), "password"
    }
}
