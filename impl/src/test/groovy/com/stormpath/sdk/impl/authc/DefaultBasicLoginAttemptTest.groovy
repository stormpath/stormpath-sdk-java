package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.junit.Test

import static org.testng.Assert.*
import static org.easymock.EasyMock.*

/**
 * @since 0.9.4
 */
class DefaultBasicLoginAttemptTest {

    @Test
    void testSetAccountStoreNull() {
        def internalDataStore = createMock(InternalDataStore)

        DefaultBasicLoginAttempt attempt = new DefaultBasicLoginAttempt(internalDataStore);
        attempt.setAccountStore(null)
        assertEquals(attempt.getAccountStore(), null)
    }

    @Test
    void testSetAccountStore() {
        def accountStore = createMock(AccountStore)
        def internalDataStore = createMock(InternalDataStore)

        DefaultBasicLoginAttempt attempt = new DefaultBasicLoginAttempt(internalDataStore);
        attempt.setAccountStore(accountStore)

        assertEquals(attempt.getAccountStore(), accountStore)
    }


}
