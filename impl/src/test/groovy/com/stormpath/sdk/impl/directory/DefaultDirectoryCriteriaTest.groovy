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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.DirectoryStatus
import com.stormpath.sdk.impl.http.QueryStringFactory
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 0.8
 */
class DefaultDirectoryCriteriaTest {

    private static final String AND = '&'
    private static final String COMMA = '%2C'
    private static final String OPEN_PAREN = '%28'
    private static final String CLOSE_PAREN = '%29'
    private static final String COLON = '%3A'

    @Test
    void testDefault() {

        def factory = new QueryStringFactory()

        //try an exhaustive query
        def c = Directories
                .where(Directories.name().eqIgnoreCase('a'))
                .and(Directories.description().startsWithIgnoreCase('b'))
                .and(Directories.status().eq(DirectoryStatus.DISABLED))
                .and(Directories.modifiedAt().matches("[2015-01-01T00:00:00.000Z]"))
                .orderByName()
                .orderByDescription().descending()
                .orderByStatus()
                .withTenant()
                .withCustomData()
                .withAccounts(30, 50)
                .withGroups(25, 100)
                .offsetBy(120)
                .limitTo(20)

        assertNotNull c
        assertTrue c instanceof DefaultDirectoryCriteria

        def queryString = factory.createQueryString((DefaultDirectoryCriteria)c);

        def expectedToString = 'name=a and ' +
                'description ilike b* and ' +
                'status=DISABLED and ' +
                'modifiedAt=[2015-01-01T00:00:00.000Z] ' +
                'order by ' +
                'name asc, description desc, status asc ' +
                'offset 120 ' +
                'limit 20 ' +
                'expand tenant, customData, accounts(offset:50,limit:30), groups(offset:100,limit:25)'

        def expectedQueryString =
                'description=b*' + AND +
                'expand=' +
                'tenant' + COMMA +
                'customData' + COMMA +
                'accounts' + OPEN_PAREN + 'offset' + COLON + 50 + COMMA + 'limit' + COLON + 30 + CLOSE_PAREN + COMMA +
                'groups' + OPEN_PAREN + 'offset' + COLON + 100 + COMMA + 'limit' + COLON + 25 + CLOSE_PAREN + AND +
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
    void testWithAccountsAndGroups() {

        def factory = new QueryStringFactory()

        def c = Directories
                .where(Directories.name().eqIgnoreCase('a'))
                .withAccounts()
                .withGroups()

        assertNotNull c
        assertTrue c instanceof DefaultDirectoryCriteria

        def queryString = factory.createQueryString((DefaultDirectoryCriteria)c);

        def expectedToString = 'name=a expand accounts, groups'

        def expectedQueryString = 'expand=' +
                'accounts' + COMMA +
                'groups' + AND +
                'name=a'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }

    @Test
    void testWithLimittedAccountsAndGroups() {

        def factory = new QueryStringFactory()

        def c = Directories
                .where(Directories.name().eqIgnoreCase('a'))
                .withAccounts(10)
                .withGroups(20)

        assertNotNull c
        assertTrue c instanceof DefaultDirectoryCriteria

        def queryString = factory.createQueryString((DefaultDirectoryCriteria)c);

        def expectedToString = 'name=a expand accounts(limit:10), groups(limit:20)'

        def expectedQueryString = 'expand=' +
                'accounts' + OPEN_PAREN + 'limit' + COLON + 10 + CLOSE_PAREN + COMMA +
                'groups' + OPEN_PAREN + 'limit' + COLON + 20 + CLOSE_PAREN + AND +
                'name=a'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }
}
