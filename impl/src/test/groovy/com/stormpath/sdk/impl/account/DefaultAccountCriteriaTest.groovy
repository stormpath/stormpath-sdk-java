package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.impl.http.QueryStringFactory
import com.stormpath.sdk.resource.Status
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 *
 * @since 0.8
 */
class DefaultAccountCriteriaTest {

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
                .where(Accounts.EMAIL.eqIgnoreCase('a'))
                .and(Accounts.USERNAME.startsWithIgnoreCase('b'))
                .and(Accounts.GIVEN_NAME.endsWithIgnoreCase('c'))
                .and(Accounts.MIDDLE_NAME.containsIgnoreCase('d'))
                .and(Accounts.SURNAME.eqIgnoreCase('e'))
                .and(Accounts.STATUS.eq(Status.ENABLED))
                .orderByEmail()
                .orderByUsername().descending()
                .orderByGivenName()
                .orderByMiddleName().descending()
                .orderBySurname().ascending()
                .orderByStatus().descending()
                .expandDirectory()
                .expandGroupMemberships(20,30)
                .expandGroups(40, 50)
                .expandTenant()
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
                'status=ENABLED ' +
                'order by ' +
                'email asc, username desc, givenName asc, middleName desc, surname asc, status desc ' +
                'offset 25 ' +
                'limit 50 ' +
                'expand directory, groupMemberships(offset:30,limit:20), groups(offset:50,limit:40), tenant'

        def expectedQueryString = 'email=a' + AND +
                'expand=' +
                    'directory' + COMMA +
                    'groupMemberships' + OPEN_PAREN + 'offset' + COLON + 30 + COMMA + 'limit' + COLON + 20 + CLOSE_PAREN + COMMA +
                    'groups' + OPEN_PAREN + 'offset' + COLON + 50 + COMMA + 'limit' + COLON + 40 + CLOSE_PAREN + COMMA +
                    'tenant' + AND +
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
