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
package com.stormpath.sdk.impl.provider

import com.stormpath.sdk.provider.ProviderData
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.beta
 */
class AbstractProviderAccountRequestTest {

    @Test
    void testNullProviderData() {
        try {
            new DefaultProviderAccountRequest(null) {}
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "providerData cannot be null.")
        }
    }

    @Test
    void testMissingProviderId() {
        def providerData = createStrictMock(ProviderData)

        expect(providerData.getProviderId()).andReturn(null)

        replay(providerData)

        try {
            new DefaultProviderAccountRequest(providerData) {}
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "providerId within ProviderData instance must be specified.")
        }

        verify(providerData)
    }

    @Test
    void test() {
        def providerData = createStrictMock(ProviderData)

        expect(providerData.getProviderId()).andReturn("stormpath")

        replay(providerData)

        def abstractProviderAccountRequest = new DefaultProviderAccountRequest(providerData) {}
        assertEquals(abstractProviderAccountRequest.getProviderData(), providerData)

        verify(providerData)

    }
}
