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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.impl.http.QueryStringFactory
import com.stormpath.sdk.tenant.Tenants
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.RC4.6
 */
class DefaultTenantCriteriaTest {

    private static final String COMMA = '%2C'
    private static final String OPEN_PAREN = '%28'
    private static final String CLOSE_PAREN = '%29'
    private static final String COLON = '%3A'

    @Test
    void testAll() {

        def factory = new QueryStringFactory()

        def c = Tenants.options()
                .withApplications()
                .withDirectories()
                .withAccounts()
                .withGroups()
                .withCustomData()

        assertNotNull c
        assertTrue c instanceof DefaultTenantOptions

        def queryString = factory.createQueryString((DefaultTenantOptions)c);

        def expectedQueryString = 'expand=' +
                'applications' + COMMA + 'directories' + COMMA + 'accounts' + COMMA + 'groups' + COMMA + 'customData'

        assertEquals queryString.toString(), expectedQueryString
    }

    @Test
    void testLimitAndOffset() {

        def factory = new QueryStringFactory()

        def c = Tenants.options()
                .withApplications(20, 30)
                .withApplications()
                .withApplications(20)
                .withDirectories(2, 14)
                .withDirectories()
                .withDirectories(5)
                .withGroups(2, 14)
                .withGroups()
                .withGroups(5)
                .withAccounts(2, 14)
                .withAccounts()
                .withAccounts(5)

        assertNotNull c
        assertTrue c instanceof DefaultTenantOptions

        def queryString = factory.createQueryString((DefaultTenantOptions)c);

        def expectedQueryString =
        'expand=' +
            'applications' + OPEN_PAREN + 'offset' + COLON + 30 + COMMA + 'limit' + COLON + 20 + CLOSE_PAREN + COMMA +
            'applications' + COMMA +
            'applications' + OPEN_PAREN + 'limit' + COLON + 20 + CLOSE_PAREN + COMMA +
            'directories' + OPEN_PAREN + 'offset' + COLON + 14 + COMMA + 'limit' + COLON + 2 + CLOSE_PAREN + COMMA +
            'directories' + COMMA +
            'directories' + OPEN_PAREN + 'limit' + COLON + 5 + CLOSE_PAREN + COMMA +
            'groups' + OPEN_PAREN + 'offset' + COLON + 14 + COMMA + 'limit' + COLON + 2 + CLOSE_PAREN + COMMA +
            'groups' + COMMA +
            'groups' + OPEN_PAREN + 'limit' + COLON + 5 + CLOSE_PAREN + COMMA +
            'accounts' + OPEN_PAREN + 'offset' + COLON + 14 + COMMA + 'limit' + COLON + 2 + CLOSE_PAREN + COMMA +
            'accounts' + COMMA +
            'accounts' + OPEN_PAREN + 'limit' + COLON + 5 + CLOSE_PAREN

        assertEquals queryString.toString(), expectedQueryString
    }
}
