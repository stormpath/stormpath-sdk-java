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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.tenant.Tenants
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 1.0.0
 */
class DefaultTenantOptionsTest {

    @Test
    void testDefault() {

        def options = Tenants.options();

        assertNotNull options
        assertTrue options instanceof DefaultTenantOptions
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testTenantOptions() {

        def options = Tenants.options();

        options.withApplications()
                .withGroups()
                .withAccounts()
                .withDirectories()
                .withCustomData()
                .withOrganizations()


        assertTrue options instanceof DefaultTenantOptions
        assertEquals(options.expansions.size(), 6)
    }
}
