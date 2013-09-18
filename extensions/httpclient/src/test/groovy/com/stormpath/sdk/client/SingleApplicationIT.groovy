/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.client

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.junit.Assert.assertEquals

/**
 * @since 0.8.1
 */
class SingleApplicationIT extends ClientIT {

    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/17">Issue #17</a>
     */
    @Test
    void testLoginWithCachingEnabled() {

        def appName = 'Spaceballs'
        def username = 'lonestarr'
        def password = 'vespa'

        //we could use the parent class's Client instance, but we re-define it here just in case:
        //if we ever turn off caching in the parent class config, we can't let that affect this test:
        def client = new ClientBuilder()
                .setApiKeyFileLocation(apiKeyFileLocation)
                .setCacheManager(Caches.newCacheManager().build()) //enable caching - required to test Issue #17
                .build()

        Tenant tenant = client.getCurrentTenant();

        ApplicationList applications = tenant.getApplications(Applications.where(
                Applications.name().containsIgnoreCase(appName)))

        Application spaceballs = applications.iterator().next()

        def request = new UsernamePasswordRequest(username, password)

        def result = spaceballs.authenticateAccount(request)

        def account = result.getAccount()

        assertEquals(username, account.username)
    }
}
