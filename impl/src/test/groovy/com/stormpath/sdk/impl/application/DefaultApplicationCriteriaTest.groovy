package com.stormpath.sdk.impl.application

import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.impl.http.QueryStringFactory
import com.stormpath.sdk.resource.Status
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 0.8
 */
class DefaultApplicationCriteriaTest {

    private static final String AND = '&'
    private static final String COMMA = '%2C'
    private static final String OPEN_PAREN = '%28'
    private static final String CLOSE_PAREN = '%29'
    private static final String COLON = '%3A'
    
    @Test
    void testDefault() {

        def factory = new QueryStringFactory()

        //try an exhaustive query
        def c = Applications
                .where(Applications.NAME.eqIgnoreCase('a'))
                .and(Applications.DESCRIPTION.startsWithIgnoreCase('b'))
                .and(Applications.STATUS.eq(Status.DISABLED))
                .orderByName()
                .orderByDescription().descending()
                .orderByStatus()
                .expandTenant()
                .expandAccounts(30, 50)
                .expandGroups(25, 100)
                .offsetBy(120)
                .limitTo(20)

        assertNotNull c
        assertTrue c instanceof DefaultApplicationCriteria
        DefaultApplicationCriteria dac = (DefaultApplicationCriteria)c;

        def queryString = factory.createQueryString(dac);

        def expectedToString = 'name=a and ' +
                'description ilike b* and ' +
                'status=DISABLED ' +
                'order by ' +
                'name asc, description desc, status asc ' +
                'offset 120 ' +
                'limit 20 ' +
                'expand tenant, accounts(offset:50,limit:30), groups(offset:100,limit:25)'

        def expectedQueryString = 'description=b*' + AND +
                'expand=' +
                'tenant' + COMMA +
                'accounts' + OPEN_PAREN + 'offset' + COLON + 50 + COMMA + 'limit' + COLON + 30 + CLOSE_PAREN + COMMA +
                'groups' + OPEN_PAREN + 'offset' + COLON + 100 + COMMA + 'limit' + COLON + 25 + CLOSE_PAREN + AND +
                'limit=20' + AND +
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
}
