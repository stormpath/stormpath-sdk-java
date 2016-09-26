package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountLinks
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/*
 * @since 1.1.0
 */
class DefaultAccountLinkOptionsTest {

    @Test
    void testDefault() {

        def options = AccountLinks.options();

        assertNotNull options
        assertTrue options instanceof DefaultAccountLinkOptions
        DefaultAccountLinkOptions accountLinkOptions = (DefaultAccountLinkOptions) options
        accountLinkOptions = accountLinkOptions.withLeftAccount()
        assertEquals(accountLinkOptions.expansions.size(), 1)
        accountLinkOptions = accountLinkOptions.withRightAccount()
        assertEquals(accountLinkOptions.expansions.size(), 2)
        assertEquals(accountLinkOptions.expansions.get(0).name, 'leftAccount')
        assertEquals(accountLinkOptions.expansions.get(1).name, 'rightAccount')
    }
}
