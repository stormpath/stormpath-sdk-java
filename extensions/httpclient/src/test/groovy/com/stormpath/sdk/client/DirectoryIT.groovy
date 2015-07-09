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
package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.directory.AccountCreationPolicy
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryOptions
import com.stormpath.sdk.directory.PasswordPolicy
import com.stormpath.sdk.impl.resource.AbstractCollectionResource
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.mail.EmailStatus
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.Providers
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.testng.Assert.*

/**
 *
 * @since 0.8.1
 */
class DirectoryIT extends ClientIT{

    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/pull/22">Pull Request 22</a>.
     */
    @Test
    void testCreateAndDeleteDirectory() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)

        assertNotNull dir.href
    }


    /**
     * Asserts fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/12">Issue #12</a>
     */
    @Test
    void testDeleteAccount() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDeleteAccount")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def email = 'johndeleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
            .setSurname('DELETEME')
            .setEmail(email)
            .setPassword('Changeme1!')

        dir.createAccount(account)

        String href = account.href

        //verify it was created:
        Account retrieved = dir.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email))).iterator().next()
        assertEquals(href, retrieved.href)

        //test delete:
        retrieved.delete()

        def list = dir.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(email)))
        assertFalse list.iterator().hasNext() //no results
    }


    /**
     * Asserts <a href="https://github.com/stormpath/stormpath-sdk-java/issues/58">Issue 58</a>.
     * @since 1.0.RC
     */
    @Test
    void testCreateDirectoryViaTenantActions() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateDirectoryViaTenantActions")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)
        assertNotNull dir.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testCreateDirectoryRequestViaTenantActions() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateDirectoryRequestViaTenantActions")
        GoogleProvider provider = client.instantiate(GoogleProvider.class)
        provider.setClientId("616598318417021").setClientSecret("c0ad961d45fdc0310c1c7d67c8f1d800")

        def request = Directories.newCreateRequestFor(dir)
                .forProvider(Providers.GOOGLE.builder()
                    .setClientId("616598318417021")
                    .setClientSecret("c0ad961d45fdc0310c1c7d67c8f1d800")
                    .setRedirectUri("http://localhost")
                    .build()
                ).build()
        dir = client.createDirectory(request);
        deleteOnTeardown(dir)
        assertNotNull dir.href
    }

    /**
     * @since 1.0.0
     */
    @Test
    void testCreateLinkedInDirectoryRequestViaTenantActions() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateLinkedInDirectoryRequest")

        def request = Directories.newCreateRequestFor(dir)
                .forProvider(Providers.LINKEDIN.builder()
                .setClientId("73i1dq2fko01s2")
                .setClientSecret("wJhXc81l63qEOc43")
                .build()
        ).build()
        dir = client.createDirectory(request);
        deleteOnTeardown(dir)
        assertNotNull dir.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testGetDirectoriesViaTenantActions() {
        def dirList = client.getDirectories()
        assertNotNull dirList.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testGetDirectoriesWithMapViaTenantActions() {
        def map = new HashMap<String, Object>()
        def dirList = client.getDirectories(map)
        assertNotNull dirList.href
    }

    /**
     * @since 1.0.RC
     */
    @Test
    void testGetDirectoriesWithDirCriteriaViaTenantActions() {
        def dirCriteria = Directories.criteria()
        def dirList = client.getDirectories(dirCriteria)
        assertNotNull dirList.href
    }

    /**
     * @since 1.0.0
     */
    @Test
    void testGetDirectoriesWithCustomData() {
        Directory directory = client.instantiate(Directory)
        directory.name = uniquify("Java SDK: DirectoryIT.testGetDirectoriesWithCustomData")
        directory.customData.put("someKey", "someValue")
        directory = client.createDirectory(directory);
        deleteOnTeardown(directory)
        assertNotNull directory.href

        def dirList = client.getDirectories(Directories.where(Directories.name().eqIgnoreCase(directory.getName())).withCustomData())

        def count = 0
        for (Directory dir : dirList) {
            count++
            assertNotNull(dir.getHref())
            assertEquals(dir.getCustomData().size(), 4)
        }
        assertEquals(count, 1)
    }

    /**
     * @since 1.0.RC4
     */
    @Test
    void testPasswordPolicy() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testPasswordPolicy")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)
        def passwordPolicy = dir.getPasswordPolicy()
        assertNotNull passwordPolicy.href
        assertEquals passwordPolicy.getResetTokenTtlHours(), 24
        assertEquals passwordPolicy.getResetEmailStatus(), EmailStatus.ENABLED
        assertEquals passwordPolicy.getResetSuccessEmailStatus(), EmailStatus.ENABLED
        passwordPolicy.setResetTokenTtlHours(100)
                .setResetEmailStatus(EmailStatus.DISABLED)
                .setResetSuccessEmailStatus(EmailStatus.DISABLED)
        passwordPolicy.save()

        //Let's check that the new state is properly retrieved in a new instance
        def retrievedPasswordPolicy = client.getResource(passwordPolicy.href, PasswordPolicy.class)
        assertEquals retrievedPasswordPolicy.getResetTokenTtlHours(), 100
        assertEquals retrievedPasswordPolicy.getResetEmailStatus(), EmailStatus.DISABLED
        assertEquals retrievedPasswordPolicy.getResetSuccessEmailStatus(), EmailStatus.DISABLED
    }

    /**
     * @since 1.0.RC4
     */
    @Test
    void testListSize() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testListSize")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        Account account01 = client.instantiate(Account)
        account01 = account01.setGivenName(uniquify('John01'))
                .setSurname('DELETEME')
                .setEmail(uniquify("john01deleteme") + "@stormpath.com")
                .setPassword('Changeme1!')

        dir.createAccount(account01)

        assertEquals(dir.getAccounts().getSize(), 1)

        def account02 = client.instantiate(Account)
        account02 = account02.setGivenName(uniquify('John02'))
                .setSurname('DELETEME')
                .setEmail(uniquify("john01deleteme") + "@stormpath.com")
                .setPassword('Changeme1!')

        dir.createAccount(account02)

        assertEquals(dir.getAccounts().getSize(), 2)

        def list = dir.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase(account01.email)))

        assertEquals(list.getSize(), 1)

        list = dir.getAccounts(Accounts.where(Accounts.email().eqIgnoreCase("listMustBeEmpty")))

        assertEquals(list.getSize(), 0)

        list = dir.getAccounts(Accounts.criteria().limitTo(1))
        int count = 0

        def firstAccount = null
        def firstPage = null
        for (Account account : list) {
            def acrlist = (AbstractCollectionResource) list
            assertEquals(acrlist.currentPage.items.size(), 1)
            assertEquals(acrlist.currentPage.size, 2)

            assertNotNull(account.getHref())
            if(count == 0) {
                firstAccount = account
                firstPage = acrlist.currentPage
            } else {
                assertNotEquals(account.getHref(), firstAccount.getHref()) //let's check that the items are actually moving
                assertNotSame(acrlist.currentPage, firstPage) //let's check that pages are actually moving
            }

            count++
        }
        assertEquals(count, 2)

        account01.delete()
        account02.delete()

        assertEquals(dir.getAccounts().getSize(), 0)
    }

    /**
     * @since 1.0.RC4.5
     */
    @Test
    void testAccountCreationPolicy(){
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testAccountCreationPolicy")
        dir = client.createDirectory(dir);
        deleteOnTeardown(dir)
        def accountPolicy = dir.getAccountCreationPolicy()
        assertNotNull accountPolicy.href

        // Validate default values
        assertEquals accountPolicy.getVerificationEmailStatus(), EmailStatus.DISABLED
        assertEquals accountPolicy.getVerificationSuccessEmailStatus(), EmailStatus.DISABLED
        assertEquals accountPolicy.getWelcomeEmailStatus(), EmailStatus.DISABLED

        //Set new values
        accountPolicy.setVerificationEmailStatus(EmailStatus.ENABLED)
        accountPolicy.setVerificationSuccessEmailStatus(EmailStatus.ENABLED)
        accountPolicy.setWelcomeEmailStatus(EmailStatus.ENABLED)
        accountPolicy.save()

        //Validate new values
        def retrievedAccountCreationPolicy = client.getResource(accountPolicy.href, AccountCreationPolicy.class)
        assertEquals(retrievedAccountCreationPolicy.getVerificationEmailStatus(), EmailStatus.ENABLED)
        assertEquals(retrievedAccountCreationPolicy.getVerificationSuccessEmailStatus(), EmailStatus.ENABLED)
        assertEquals(retrievedAccountCreationPolicy.getWelcomeEmailStatus(), EmailStatus.ENABLED)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testDirectoryExpansion(){

        //In order to check that expansion works we need to disable the cache due to this issue: https://github.com/stormpath/stormpath-sdk-java/issues/164
        //Once that issue has been fixed, we need to duplicate this test but having cache enabled this time
        Client client = buildClient(false);

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDirectoryExpansion")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        String href = dir.href

        DirectoryOptions options = Directories.options().withAccounts()

        // test options created successfully
        assertNotNull options
        assertEquals options.expansions.size(), 1

        //Test the expansion worked by reading the internal properties of the directory
        Directory retrieved = client.getResource(href, Directory.class, options)
        Map dirProperties = getValue(AbstractResource, retrieved, "properties")
        assertTrue dirProperties.get("accounts").size() > 1
        assertTrue dirProperties.get("accounts").get("size") == 0

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('Doe')
                .setEmail('johndoe@email.com')
                .setPassword('Changeme1!')
        dir.createAccount(account)

        //Test the expansion worked by reading the internal properties of the directory, it must contain the recently created account now
        retrieved = client.getResource(href, Directory.class, options)
        dirProperties = getValue(AbstractResource, retrieved, "properties")
        assertTrue dirProperties.get("accounts").size() > 1
        assertTrue dirProperties.get("accounts").get("size") == 1
        assertEquals dirProperties.get("accounts").get("items")[0].get("givenName"), "John"
    }

    /**
     * @since 1.0.RC4.6
     */
    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

}
