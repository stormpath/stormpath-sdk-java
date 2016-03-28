package com.stormpath.sdk.impl.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.Organizations
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

/**
 * @since 1.0.RC9
 */
class DefaultClientIT extends ClientIT {

    @Test
    void testGetOrganizationsByCriteria() {

        def orgName = setupOrgs()

        def orgs = client.getOrganizations(Organizations.where(Organizations.name().eqIgnoreCase(orgName.toLowerCase())))

        assertEquals orgs.size, 1
    }

    @Test
    void  testGetOrganizationsByQueryParams() {

        def orgName = setupOrgs()

        def orgs = client.getOrganizations(["name":orgName])

        assertEquals orgs.size, 1
    }

    @Test
    void testGetAccounts() {

        def email = setupAccounts()

        def accounts = client.getAccounts()

        assertNotNull accounts.find { it.email == email }
    }

    @Test
    void testGetAccountsByCriteria() {

        def email = setupAccounts()

        def accounts = client.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email)))

        assertEquals accounts.size, 1
    }

    @Test
    void testGetAccountsByQueryParams() {

        def email = setupAccounts()

        def accounts = client.getAccounts(["email":email])

        assertEquals accounts.size, 1
    }

    @Test
    void testGetGroups() {

        def groupName = setupGroups()

        def groups = client.getGroups()

        assertNotNull groups.find { it.name = groupName }
    }

    @Test
    void testGetGroupsByCriteria() {

        def groupName = setupGroups()

        def groups = client.getGroups(Groups.where(Groups.name().eqIgnoreCase(groupName)))

        assertEquals groups.size, 1
    }

    @Test
    void testGetGroupsByQueryParams() {

        def groupName = setupGroups()

        def groups = client.getGroups(["name":groupName])

        assertEquals groups.size, 1
    }

    def String setupOrgs() {

        def orgName = ""

        2.times {
            orgName = uniquify("Java_SDK_DefaultClientIT_testGetOrganizations")

            def org = client.instantiate(Organization)
            org.nameKey = uniquify("my-org")
            org.name = orgName
            org.description = org.name + "-Description"
            org = client.currentTenant.createOrganization(org)
            deleteOnTeardown(org)
        }

        return orgName
    }

    def String setupAccounts() {

        def app = createTempApp()

        def email = ""

        2.times {
            email = uniquify('deleteme') + '@stormpath.com'

            Account account = client.instantiate(Account)
            account.givenName = 'John'
            account.surname = 'DELETEME'
            account.email = email
            account.password = 'Changeme1!'

            app.createAccount(account)
            deleteOnTeardown(account)
        }

        return email
    }

    def String setupGroups() {

        def app = createTempApp()

        def groupName = ""

        2.times {
            groupName = uniquify("Java_SDK_DefaultClientIT_testGetGroups")

            def group = client.instantiate(Group)
            group.name = groupName
            group.description = group.name + "-Description"

            app.createGroup(group)
        }

        return groupName
    }
}
