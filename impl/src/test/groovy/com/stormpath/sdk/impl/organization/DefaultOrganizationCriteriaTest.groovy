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
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.impl.http.QueryStringFactory
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 1.0.RC7
 */
class DefaultOrganizationCriteriaTest {

    private static final String AND = '&'
    private static final String COMMA = '%2C'
    private static final String OPEN_PAREN = '%28'
    private static final String CLOSE_PAREN = '%29'
    private static final String COLON = '%3A'

    @Test
    void testAllOptions() {

        def factory = new QueryStringFactory()

        //try an exhaustive query
        def c = Accounts
                .where(Accounts.email().eqIgnoreCase('a'))
                .and(Accounts.username().startsWithIgnoreCase('b'))
                .and(Accounts.givenName().endsWithIgnoreCase('c'))
                .and(Accounts.middleName().containsIgnoreCase('d'))
                .and(Accounts.surname().eqIgnoreCase('e'))
                .and(Accounts.status().eq(AccountStatus.ENABLED))
                .and(Accounts.createdAt().matches("[2015-01-01T00:00:00.000Z,2015-02-01T00:00:00.000Z)"))
                .orderByEmail()
                .orderByUsername().descending()
                .orderByGivenName()
                .orderByMiddleName().descending()
                .orderBySurname().ascending()
                .orderByStatus().descending()
                .withDirectory()
                .withGroupMemberships(20,30)
                .withGroupMemberships()
                .withGroupMemberships(20) // the REST API will take this last value
                .withGroups(40, 50)
                .withGroups(45)
                .withGroups() // the REST API will take this last value
                .withTenant()
                .withCustomData()
                .offsetBy(25)
                .limitTo(50)

        assertNotNull c
        assertTrue c instanceof DefaultAccountCriteria
        DefaultAccountCriteria dac = (DefaultAccountCriteria)c;

        def queryString = factory.createQueryString(dac);

        def expectedToString = 'email=a and ' +
                'username ilike b* and ' +
                'givenName ilike *c and ' +
                'middleName ilike *d* and ' +
                'surname=e and ' +
                'status=ENABLED and ' +
                'createdAt=[2015-01-01T00:00:00.000Z,2015-02-01T00:00:00.000Z) ' +
                'order by ' +
                'email asc, username desc, givenName asc, middleName desc, surname asc, status desc ' +
                'offset 25 ' +
                'limit 50 ' +
                'expand directory, groupMemberships(offset:30,limit:20), groupMemberships, groupMemberships(limit:20), ' +
                'groups(offset:50,limit:40), groups(limit:45), groups, tenant, customData'

        def expectedQueryString = 'createdAt=%5B2015-01-01T00%3A00%3A00.000Z%2C2015-02-01T00%3A00%3A00.000Z%29' + AND +
                'email=a' + AND +
                'expand=' +
                'directory' + COMMA +
                'groupMemberships' + OPEN_PAREN + 'offset' + COLON + 30 + COMMA + 'limit' + COLON + 20 + CLOSE_PAREN + COMMA +
                'groupMemberships' + COMMA +
                'groupMemberships' + OPEN_PAREN + 'limit' + COLON + 20 + CLOSE_PAREN + COMMA +
                'groups' + OPEN_PAREN + 'offset' + COLON + 50 + COMMA + 'limit' + COLON + 40 + CLOSE_PAREN + COMMA +
                'groups' + OPEN_PAREN + 'limit' + COLON + 45 + CLOSE_PAREN + COMMA +
                'groups' + COMMA +
                'tenant' + COMMA +
                'customData' + AND +
                'givenName=*c' + AND +
                'limit=50' + AND +
                'middleName=*d*' + AND +
                'offset=25' + AND +
                'orderBy=' +
                'email+asc' + COMMA +
                'username+desc' + COMMA +
                'givenName+asc' + COMMA +
                'middleName+desc' + COMMA +
                'surname+asc' + COMMA +
                'status+desc' + AND +
                'status=ENABLED' + AND +
                'surname=e' + AND +
                'username=b*'

        assertEquals c.toString(), expectedToString
        assertEquals queryString.toString(), expectedQueryString
    }
}
