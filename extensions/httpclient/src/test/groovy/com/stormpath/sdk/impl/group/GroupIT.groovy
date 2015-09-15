package com.stormpath.sdk.impl.group

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupMembership
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue
import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.fail

/**
 * @since 1.0.RC5
 */
class GroupIT extends ClientIT {

    /**
     * @since 1.0.RC5
     */
    @Test
    public void testAddAccountError() {

        def app = createTempApp()

        //create a group:
        def group = client.instantiate(Group)
        group.name = uniquify('JSDK: testAddAccountError')
        group = app.createGroup(group)
        deleteOnTeardown(group)

        def result = group.addAccount("SuperInvalid")
        assertNull result
        assertEquals 0, group.getAccounts().size
    }

    /**
     * @since 1.0.RC5
     */
    @Test
    void testAddAndRemoveAccount() {

        def app = createTempApp()

        //create a user group:
        def group = client.instantiate(Group)
        group.name = uniquify('JSDK: testAddAndRemoveAccount_Group')
        group = app.createGroup(group)
        deleteOnTeardown(group)

        //create a first test account
        def acct1 = client.instantiate(Account)
        acct1.username = uniquify('JSDK-testAddAndRemoveAccount-1')
        acct1.password = 'Changeme1!'
        acct1.email = 'usr1@nowhere.com'
        acct1.givenName = 'Joe'
        acct1.surname = 'Smith'
        acct1 = app.createAccount(Accounts.newCreateRequestFor(acct1).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct1)

        //create a second test account
        def acct2 = client.instantiate(Account)
        acct2.username = uniquify('JSDK-testAddAndRemoveAccount-2')
        acct2.password = 'Changeme1!'
        acct2.email = 'usr2@nowhere.com'
        acct2.givenName = 'Mary'
        acct2.surname = 'Smith'
        acct2 = app.createAccount(Accounts.newCreateRequestFor(acct2).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct2)

        //create a third test account
        def acct3 = client.instantiate(Account)
        acct3.username = uniquify('JSDK-testAddAndRemoveAccount-3')
        acct3.password = 'Changeme1!'
        acct3.email = 'usr3@nowhere.com'
        acct3.givenName = 'David'
        acct3.surname = 'Smith'
        acct3 = app.createAccount(Accounts.newCreateRequestFor(acct3).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct3)

        //create a forth test account
        def acct4 = client.instantiate(Account)
        acct4.username = uniquify('JSDK-testAddAndRemoveAccount-4')
        acct4.password = 'Changeme1!'
        acct4.email = 'usr4@nowhere.com'
        acct4.givenName = 'John'
        acct4.surname = 'Smith'
        acct4 = app.createAccount(Accounts.newCreateRequestFor(acct4).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct4)

        //add the first account to the group using object
        GroupMembership membership = group.addAccount(acct1);
        assertNotNull membership
        assertEquals group.getAccounts().size, 1
        assertEquals membership.group.href, group.getHref()
        assertEquals membership.account.href, acct1.getHref()

        //add the second account to the group using href
        membership = group.addAccount(acct2.href);
        assertNotNull membership
        assertEquals group.getAccounts().size, 2
        assertEquals membership.group.href, group.getHref()
        assertEquals membership.account.href, acct2.getHref()
        
        //add the third account to the group using email
        membership = group.addAccount(acct3.email);
        assertNotNull membership
        assertEquals group.getAccounts().size, 3
        assertEquals membership.group.href, group.getHref()
        assertEquals membership.account.href, acct3.getHref()

        //add the third account to the group using username
        membership = group.addAccount(acct4.username);
        assertNotNull membership
        assertEquals group.getAccounts().size, 4
        assertEquals membership.group.href, group.getHref()
        assertEquals membership.account.href, acct4.getHref()
        
        //Remove account using object
        group = group.removeAccount(acct3)
        assertEquals 3, group.getAccounts().size

        //Remove account using href
        group = group.removeAccount(acct4.href)
        assertEquals 2, group.getAccounts().size

        //Remove account using email
        group = group.removeAccount(acct1.email)
        assertEquals 1, group.getAccounts().size

        //Remove account using username
        group = group.removeAccount(acct2.username)
        assertEquals 0, group.getAccounts().size

        // Test remove account error

        def acct5 = client.instantiate(Account)
        acct5.username = uniquify('JSDK-testAddAndRemoveAccount-5')
        acct5.password = 'Changeme1!'
        acct5.email = 'usr5test@nowhere.com'
        acct5.givenName = 'John'
        acct5.surname = 'Smith'
        acct5 = app.createAccount(Accounts.newCreateRequestFor(acct5).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct5)

        try {
            group.removeAccount(acct5)
            fail ("Should have failed due to account not present in group")
        } catch (Exception e){
            assertTrue e instanceof IllegalStateException
            assertEquals "The specified account does not belong to this Group.", e.getMessage()
        }

        try {
            group.removeAccount("invalid href or username")
            fail ("Should have failed due to account not present in group")
        } catch (Exception e){
            assertTrue e instanceof IllegalStateException
            assertEquals "The specified account does not belong to this Group.", e.getMessage()
        }
    }
}
