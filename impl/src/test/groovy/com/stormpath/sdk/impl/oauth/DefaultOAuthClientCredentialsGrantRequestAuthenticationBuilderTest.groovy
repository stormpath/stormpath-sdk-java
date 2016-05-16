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
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication
import org.testng.annotations.Test

import static org.easymock.EasyMock.createMock
import static org.testng.Assert.assertSame
import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.fail

/**
 * Test for ClientCredentialsGrantRequestBuilder class
 *
 * @since 1.0.0
 */
class DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilderTest {

    @Test
    void testBuildErrors() {

        def builder = new DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilder()

        try{
            builder.setApiKeyId("test").build()
            fail("Should have failed");
        } catch (IllegalStateException e){
            assertEquals(e.getMessage(), "apiKeySecret has not been set. It is a required attribute.")
        }

        builder = new DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilder()

        try{
            builder.setApiKeySecret("test").build()
            fail("Should have failed");
        } catch (IllegalStateException e){
            assertEquals e.getMessage(), "apiKeyId has not been set. It is a required attribute."
        }
    }

    @Test
    void testMethods(){
        def builder = new DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilder()

        def accountStore = createMock(AccountStore)

        builder.setApiKeyId("test_id")
        builder.setApiKeySecret("test_secret")
        builder.setAccountStore(accountStore)

        OAuthClientCredentialsGrantRequestAuthentication request = builder.build()

        assertSame request.getAccountStore(), accountStore
        assertEquals request.getApiKeyId(), "test_id"
        assertEquals request.getApiKeySecret(), "test_secret"
        assertEquals request.getGrantType(), "client_credentials"
    }
}
