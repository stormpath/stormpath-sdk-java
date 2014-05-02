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
package com.stormpath.sdk.provider

import com.stormpath.sdk.lang.UnknownClassException
import org.testng.annotations.Test

import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.0.beta
 */
class GoogleAccountRequestBuilderTest {

    @Test
    void testAccountRequest() {
        def providerRequest = Providers.GOOGLE;
        try{
            providerRequest.accountRequest();
            fail("should have thrown since the implementation is in the impl package")
        } catch (UnknownClassException e) {
            assertTrue(e.getMessage().contains("Unable to load class named [com.stormpath.sdk.impl.provider.DefaultGoogleAccountRequestBuilder]"))
        }
    }

    @Test
    void testCreateProviderRequest() {
        def providerRequest = Providers.GOOGLE;
        try{
            providerRequest.createProviderRequest();
            fail("should have thrown since the implementation is in the impl package")
        } catch (UnknownClassException e) {
            assertTrue(e.getMessage().contains("Unable to load class named [com.stormpath.sdk.impl.provider.DefaultGoogleCreateProviderRequestBuilder]"))
        }
    }
}
