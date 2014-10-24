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

import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.lang.Objects
import com.stormpath.sdk.provider.FacebookProviderData
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountResult
import com.stormpath.sdk.provider.ProviderData
import com.stormpath.sdk.resource.Resource
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.beta
 */
class ProviderAccountResolverTest {

    @Test
    void testNullDataStore() {
        try {
            new ProviderAccountResolver(null)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "dataStore cannot be null")
        }
    }

    @Test
    void testRequiredArguments() {
        def accountHref = "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj/accounts"
        def internalDataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(ProviderAccountRequest)

        try {
            new ProviderAccountResolver(internalDataStore).resolveProviderAccount(null, request)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "parentHref argument must be specified")
        }

        try {
            new ProviderAccountResolver(internalDataStore).resolveProviderAccount(accountHref, null)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "request argument cannot be null")
        }

        expect(request.getProviderData()).andReturn(null)

        try {
            new ProviderAccountResolver(internalDataStore).resolveProviderAccount(accountHref, request)
            fail("should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "request's providerData must be specified")
        }
    }

    @Test
    void testRequestAccess() {
        def internalDataStore = createStrictMock(InternalDataStore)
        def request = createStrictMock(ProviderAccountRequest)
        def providerData = createStrictMock(ProviderData)
        def providerAccountResult = createStrictMock(ProviderAccountResult)

        def href = "https://api.stormpath.com/v1/applications/jefoifj93riu23ioj"

        def providerAccountAccess = new DefaultProviderAccountAccess<FacebookProviderData>(internalDataStore);
        providerAccountAccess.setProviderData(providerData)

        expect(request.getProviderData()).andReturn(providerData) times 2
        expect(internalDataStore.create(eq(href + "/accounts"), (Resource) reportMatcher(new ProviderAccountAccessEquals(providerAccountAccess)), (Class)eq(ProviderAccountResult))).andReturn(providerAccountResult)

        replay(internalDataStore, request, providerData, providerAccountResult)

        new ProviderAccountResolver(internalDataStore).resolveProviderAccount(href, request)

        verify(internalDataStore, request, providerData, providerAccountResult)
    }

    static class ProviderAccountAccessEquals implements IArgumentMatcher {

        private ProviderAccountAccess expected

        ProviderAccountAccessEquals(ProviderAccountAccess providerAccountAccess) {
            expected = providerAccountAccess;

        }
        boolean matches(Object o) {
            if (o == null || ! ProviderAccountAccess.isInstance(o)) {
                return false;
            }
            ProviderAccountAccess actual = (ProviderAccountAccess) o
            return (Objects.nullSafeEquals(expected.providerData, actual.providerData))
        }

        void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append("providerData: " + expected.providerData.toString())
        }
    }

}
