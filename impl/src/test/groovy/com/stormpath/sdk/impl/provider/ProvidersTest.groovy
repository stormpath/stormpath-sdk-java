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

import com.stormpath.sdk.provider.ProviderRequestFactory
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import static org.testng.Assert.assertTrue

/**
 * @since 1.0.beta
 */
class ProvidersTest {

    @Test
    void test() {
        def providerRequestFactory = Providers.FACEBOOK;
        assertTrue providerRequestFactory instanceof DefaultFacebookRequestFactory
        assertTrue(ProviderRequestFactory.isInstance(providerRequestFactory))

        providerRequestFactory = Providers.GOOGLE;
        assertTrue providerRequestFactory instanceof DefaultGoogleRequestFactory
        assertTrue(ProviderRequestFactory.isInstance(providerRequestFactory))

        //@since 1.0.0
        providerRequestFactory = Providers.GITHUB;
        assertTrue providerRequestFactory instanceof DefaultGithubRequestFactory

        providerRequestFactory = Providers.LINKEDIN;
        assertTrue providerRequestFactory instanceof DefaultLinkedInRequestFactory

        assertTrue(ProviderRequestFactory.isInstance(providerRequestFactory))
    }
}
