package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.authc.UsernamePasswordRequests
import com.stormpath.sdk.directory.AccountStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC9
 */
class UsernamePasswordRequestsTest {

    @Test
    void testBuilder() {
        def builder = UsernamePasswordRequests.builder();
        assertTrue(builder instanceof DefaultUsernamePasswordRequestBuilder)
    }

    @Test
    void testOptions() {
        def options = UsernamePasswordRequests.options();
        assertTrue(options instanceof DefaultBasicAuthenticationOptions)
    }

    @Test
    void testAccountStore() {

        def accountStore = createMock(AccountStore)

        //Password String
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd").build()
        assertNull request.getAccountStore()

        request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd").setHost("someHost").build()
        assertEquals(request.getAccountStore(), null)

        request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd").inAccountStore(accountStore).build()
        assertSame(request.getAccountStore(), accountStore)

        //Password char[]
        request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd".toCharArray()).build()
        assertEquals(request.getAccountStore(), null)

        request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd".toCharArray()).setHost("someHost").build()
        assertSame(request.getAccountStore(), null)

        request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("passwd".toCharArray()).inAccountStore(accountStore).build()
        assertSame(request.getAccountStore(), accountStore)
    }

    @Test
    void testClear() {

        def accountStore = createMock(AccountStore)

        def request = UsernamePasswordRequests.builder().setUsernameOrEmail("username").setPassword("password").setHost("foo").inAccountStore(accountStore).build()

        request.clear()

        assertNull request.getAccountStore()
        assertNull request.getCredentials()
        assertNull request.getPrincipals()
        assertNull request.getHost()
    }
}
