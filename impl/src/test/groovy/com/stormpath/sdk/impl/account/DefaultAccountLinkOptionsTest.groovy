package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.AccountLinks
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue
/**
 *
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
        accountLinkOptions = accountLinkOptions.withRightAccount()
    }
}
