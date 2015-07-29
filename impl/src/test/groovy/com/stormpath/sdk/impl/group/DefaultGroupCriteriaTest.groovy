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
package com.stormpath.sdk.impl.group

import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.impl.http.QueryStringFactory
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 0.8
 */
class DefaultGroupCriteriaTest {

    private static final String AND = '&'
    private static final String COMMA = '%2C'
    private static final String OPEN_PAREN = '%28'
    private static final String CLOSE_PAREN = '%29'
    private static final String COLON = '%3A'

    @Test
    void testDefault() {

        def factory = new QueryStringFactory()

        //try an exhaustive query
        def c = Groups
                .where(Groups.name().eqIgnoreCase('a'))
                .and(Groups.description().startsWithIgnoreCase('b'))
                .and(Groups.status().eq(GroupStatus.DISABLED))
                .and(Groups.modifiedAt().matches("[2015-01-01T00:00:00.000Z]"))
                .orderByName()
                .orderByDescription().descending()
                .orderByStatus()
                .withDirectory()
                .withTenant()
                .withAccounts(30, 50)
                .withAccountMemberships(25, 100)
                .offsetBy(120)
                .limitTo(20)

        assertNotNull c
        assertTrue c instanceof DefaultGroupCriteria

        def queryString = factory.createQueryString((DefaultGroupCriteria)c);

        def expectedToString = 'name=a and ' +
                'description ilike b* and ' +
                'status=DISABLED and ' +
                'modifiedAt=[2015-01-01T00:00:00.000Z] ' +
                'order by ' +
                'name asc, description desc, status asc ' +
                'offset 120 ' +
                'limit 20 ' +
                'expand directory, tenant, accounts(offset:50,limit:30), accountMemberships(offset:100,limit:25)'

        def expectedQueryString =
                'description=b*' + AND +
                'expand=' +
                'directory' + COMMA +
                'tenant' + COMMA +
                'accounts' + OPEN_PAREN + 'offset' + COLON + 50 + COMMA + 'limit' + COLON + 30 + CLOSE_PAREN + COMMA +
                'accountMemberships' + OPEN_PAREN + 'offset' + COLON + 100 + COMMA + 'limit' + COLON + 25 + CLOSE_PAREN + AND +
                'limit=20' + AND +
                'modifiedAt=%5B2015-01-01T00%3A00%3A00.000Z%5D' + AND +
                'name=a' + AND +
                'offset=120' + AND +
                'orderBy=' +
                'name+asc' + COMMA +
                'description+desc' + COMMA +
                'status+asc' + AND +
                'status=DISABLED'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }

    @Test
    void testWithAccountsAndAccountMemberships() {

        def factory = new QueryStringFactory()

        def c = Groups
                .where(Groups.name().eqIgnoreCase('a'))
                .withAccounts()
                .withAccountMemberships()

        assertNotNull c
        assertTrue c instanceof DefaultGroupCriteria

        def queryString = factory.createQueryString((DefaultGroupCriteria)c);

        def expectedToString = 'name=a expand accounts, accountMemberships'

        def expectedQueryString = 'expand=' +
                'accounts' + COMMA +
                'accountMemberships' + AND +
                'name=a'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }

    @Test
    void testWithLimittedAccountsAndAccountMemberships() {

        def factory = new QueryStringFactory()

        def c = Groups
                .where(Groups.name().eqIgnoreCase('a'))
                .withAccounts(10)
                .withAccountMemberships(20)

        assertNotNull c
        assertTrue c instanceof DefaultGroupCriteria

        def queryString = factory.createQueryString((DefaultGroupCriteria)c);

        def expectedToString = 'name=a expand accounts(limit:10), accountMemberships(limit:20)'

        def expectedQueryString = 'expand=' +
                'accounts' + OPEN_PAREN + 'limit' + COLON + 10 + CLOSE_PAREN + COMMA +
                'accountMemberships' + OPEN_PAREN + 'limit' + COLON + 20 + CLOSE_PAREN + AND +
                'name=a'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }
}
