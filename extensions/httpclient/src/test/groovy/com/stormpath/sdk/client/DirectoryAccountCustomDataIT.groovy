package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.directory.Directory
import org.testng.annotations.Test

import static org.junit.Assert.assertEquals

/**
 * @since 0.9
 */
class DirectoryAccountCustomDataIT extends AbstractCustomDataIT {

    Directory directory

    @Test
    void testCreateAccountWithCustomData() {

        directory = retrieveDirectory()

        assertEquals(directoryHref, directory.href)

        def postedCustomData = createComplexData()
        def account1 = createAccount(postedCustomData, false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), true)

        postedCustomData = createComplexData()
        def account2 = createAccount(postedCustomData, true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account3 = createAccount(postedCustomData, false)
        updateAccount(account3, postedCustomData, [:], false)
        updateAccount(account3, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account4 = createAccount(postedCustomData, true)
        updateAccount(account4, postedCustomData, [:], true)
        updateAccount(account4, postedCustomData, createDataForUpdate(), true)

    }

    def Account createAccount(Map postedCustomData, boolean expand) {
        def account = newAccountData()

        account.customData.putAll(postedCustomData)

        def builder = Accounts.newCreateRequestFor(account)

        builder = expand ? builder.withCustomData() : builder

        directory.createAccount(builder.build());

        assertValidCustomData(account.href + "/customData", postedCustomData, account.customData, expand)

        deleteOnTeardown(account)

        return account
    }

}
