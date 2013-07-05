package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Accounts
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 *
 * @since 0.8
 */
class DefaultAccountOptionsTest {

    @Test
    void testDefault() {

        def options = Accounts.options();

        assertNotNull options
        assertTrue options instanceof DefaultAccountOptions
    }
}
