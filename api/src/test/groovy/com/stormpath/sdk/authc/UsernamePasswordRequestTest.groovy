package com.stormpath.sdk.authc

import com.stormpath.sdk.directory.AccountStore
import org.easymock.EasyMock
import org.junit.Test
import org.testng.Assert

/**
 * @since 0.9.4
 */
class UsernamePasswordRequestTest {

    @Test
    void testSetAccountStore() {
        UsernamePasswordRequest request = new UsernamePasswordRequest("username", "passwd");
        Assert.assertEquals(request.getAccountStore(), null)

        def accountStore = EasyMock.createMock(AccountStore)
        request.setAccountStore(accountStore)

        Assert.assertEquals(request.getAccountStore(), accountStore)
    }


}
