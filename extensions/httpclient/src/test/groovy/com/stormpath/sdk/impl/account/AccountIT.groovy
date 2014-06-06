/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.impl.resource.AbstractResource
import org.junit.Assert
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.testng.Assert.*

/**
 * @since 0.9.3
 */
class AccountIT extends ClientIT {

    @Test
    void testIsMemberOf() {

        def app = createTempApp()

        //create a user group:
        def group = client.instantiate(Group)
        group.name = uniquify('Users')
        group.description = "Description for " + group.name
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

        assertTrue acct.isMemberOfGroup(group.name)
        assertTrue acct.isMemberOfGroup(group.name.toUpperCase())
        assertTrue acct.isMemberOfGroup(group.href)
        assertTrue acct.isMemberOfGroup(group.href.toLowerCase())
        assertFalse acct.isMemberOfGroup(group.name.substring(0, group.name.length() - 2) + "*")

    }

    //@since 1.0.beta
    @Test
    void testGetProviderData() {

        def app = createTempApp()

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

        def providerData = acct.getProviderData()

        assertEquals providerData.getHref(), acct.getHref() + "/providerData"
        assertEquals providerData.getProviderId(), "stormpath"
        assertNotNull providerData.getCreatedAt()
        assertNotNull providerData.getModifiedAt()
    }

    /**
     * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/47
     * @since 1.0.RC
     */
    @Test
    public void testResourceReferencesStayInSync() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        account = client.getResource(account.href, Account.class);

        def directory = client.getResource(account.getDirectory().getHref(), Directory.class);

        directory.setName(app.getName() + "XXXXX");
        directory.save();

        account.getDirectory().getName();
        Assert.assertEquals(directory.getName(), account.getDirectory().getName());

        directory.setName(app.getName() + "YYYYY");
        directory.save();

        //Before fixing issue #47, this assertion failed
        Assert.assertEquals(directory.getName(), account.getDirectory().getName());
    }

    // @since 1.0.RC
    @Test
    public void testCustomDataStayInSync() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        account = client.getResource(account.getHref(), Account.class);

        def customData = client.getResource(account.getHref() + "/customData", CustomData.class);

        customData.put("aKey", "aValue01");
        customData.save();

        //CustomData content obtained from Account is in sync with the content obtained from the CustomData Resource
        Assert.assertEquals("aValue01", customData.get("aKey"));
        Assert.assertEquals("aValue01", account.getCustomData().get("aKey"));

        customData.put("aKey", "aValue02");
        customData.save();

        //Before fixing issue #47, this assertion failed
        Assert.assertEquals(customData.get("aKey"), account.getCustomData().get("aKey")); // assertion fails
    }

    //@since 1.0.RC
    @Test
    public void testCustomDataSize() {
        def app = createTempApp()
        def account = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account.setEmail(account.getUsername() + "@nowhere.com")
        account = app.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account)

        def customData = client.getResource(account.getHref() + "/customData", CustomData.class);

        customData.put("aKey", "aValue02");
        assertEquals(customData.size(), account.getCustomData().size() + 3)
        account.getCustomData().get("anything")
        assertEquals(customData.size(), account.getCustomData().size() + 1)
        customData.save();
        assertEquals(customData.size(), account.getCustomData().size())
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testInternalProperties() {

        def app = createTempApp()
        def account01 = client.instantiate(Account)
                .setUsername(uniquify('Stormpath-SDK-Test-App-Acct1'))
                .setPassword("Changeme1!")
                .setGivenName("Joe")
                .setSurname("Smith")
        account01.setEmail(account01.getUsername() + "@nowhere.com")
        account01 = app.createAccount(Accounts.newCreateRequestFor(account01).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(account01)

        def account02 = client.getResource(account01.href, Account)

        Map properties01 = getValue(AbstractResource, account01, "properties")
        Map dirtyProperties01 = getValue(AbstractResource, account01, "dirtyProperties")
        Map properties02 = getValue(AbstractResource, account02, "properties")
        Map dirtyProperties02 = getValue(AbstractResource, account02, "dirtyProperties")

        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)
        assertSame(properties01, properties02)

        account01.setEmail("new@email.com")
        assertEquals(account01.getEmail(), "new@email.com")
        assertEquals(account02.getEmail(), account01.getUsername() + "@nowhere.com")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

        account01.save()

        assertSame(properties01, properties02)
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

        assertEquals(account01.getEmail(), account02.getEmail())

        account02.setMiddleName("New Middle Name for Account02")
        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)
        account01.save()
        assertEquals(account01.getMiddleName(), null)
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)

        account01.setMiddleName("New Middle Name for Account01")
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 1)
        account02.save()

        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account02")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 1)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)
        account01.save()
        assertEquals(account01.getMiddleName(), "New Middle Name for Account01")
        assertEquals(account02.getMiddleName(), "New Middle Name for Account01")
        assertEquals(properties01.size(), 16)
        assertEquals(dirtyProperties01.size(), 0)
        assertEquals(properties02.size(), 16)
        assertEquals(dirtyProperties02.size(), 0)

    }

    //@since 1.0.RC
    private Object getValue(Class clazz, Object object, String fieldName){
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

}
