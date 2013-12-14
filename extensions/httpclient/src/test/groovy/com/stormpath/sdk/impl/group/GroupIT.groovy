package com.stormpath.sdk.impl.group

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupMembership
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull


class GroupIT extends ClientIT {

    @Test
    void testAddAccount() {

        def app = createTempApp()

        //create a user group:
        def group = client.instantiate(Group)
        group.name = uniquify('Users')
        group = app.createGroup(group)
        deleteOnTeardown(group)

        //create a test account:
        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = app.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        //add the account to the group:
        GroupMembership membership = group.addAccount(acct);
        deleteOnTeardown(membership)

        assertNotNull membership
        assertEquals membership.getGroup().getHref(), group.getHref()
        assertEquals membership.getAccount().getHref(), acct.getHref()
        assertEquals group.getAccounts().iterator().next().getHref(), acct.getHref()
        assertEquals acct.getGroups().iterator().next().getHref(), group.getHref()
        assertEquals group.getAccountMemberships().iterator().next().getHref(), membership.getHref()
        assertEquals acct.getGroupMemberships().iterator().next().getHref(), membership.getHref()
    }

}
