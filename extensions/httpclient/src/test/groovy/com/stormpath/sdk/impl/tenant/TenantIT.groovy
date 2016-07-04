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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.Organizations
import com.stormpath.sdk.organization.OrganizationStatus
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GithubProvider
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.LinkedInProvider
import com.stormpath.sdk.provider.Providers
import com.stormpath.sdk.lang.Duration
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.tenant.Tenant
import com.stormpath.sdk.tenant.TenantOptions
import com.stormpath.sdk.tenant.Tenants
import org.testng.annotations.Test

import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

import static com.stormpath.sdk.application.Applications.newCreateRequestFor
import static org.testng.Assert.*

/**
 * @since 0.8
 */
class TenantIT extends ClientIT {

    //TODO - enable this test after assertions are made with the Cache statistics API.
    // This currently just prints output and isn't a valid test for asserting caching.
    @Test(enabled = false)
    void testCaching() {

        //create a bunch of apps:
        def tenant = client.currentTenant

        /*
        120.times { i ->
            Application app = client.instantiate(Application)
            app.name = "Testing Application ${i+1} " + UUID.randomUUID().toString()

            Application created =
                tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            resourcesToDelete.add(created);
        }*/

        int count = 0;
        def hrefs = []
        def list = tenant.getApplications()
        for( Application app : list) {
            hrefs << app.href
            count++
        }
        println "Applications seen: $count"

        hrefs.each { href ->
            client.getResource(href, Application)
        }

        count = 0
        hrefs = []
        list = tenant.getDirectories()
        for( Directory directory : list) {
            hrefs << directory.href
            count++
        }
        println "Directories seen: $count"

        hrefs.each { href ->
            client.getResource(href, Directory)
        }

        tenant = client.getResource(tenant.getHref(), Tenant)

        list = tenant.getApplications()
        for(Application app : list) {
            println "Application $app.name"
        }
        list = tenant.getDirectories()
        for(Directory dir : list) {
            println "Directory $dir.name"
        }

        def s = client.dataStore.cacheManager.toString()
        println '"cacheManager": ' + s;

        Thread.sleep(20)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testCurrentTenantWithOptions(){

        TenantOptions options = Tenants.options().withDirectories().withApplications().withGroups()
        def tenant = client.getCurrentTenant(options)

        assertNotNull tenant

        Map properties = getValue(AbstractResource, tenant, "properties")
        def apps = properties.get("applications").get("size")
        def dirs = properties.get("directories").get("size")
        def groups = properties.get("groups").get("size")

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCurrentTenantWithOptions Dir")
        dir = tenant.createDirectory(dir);
        deleteOnTeardown(dir)

        assertNotNull dir.href

        Application app = client.instantiate(Application)
        app.setName(uniquify("Java SDK: TenantIT.testCurrentTenantWithOptions App"))
        app = tenant.createApplication(newCreateRequestFor(app).build())
        deleteOnTeardown(app)

        assertNotNull app.href

        def group = client.instantiate(Group)
        group.setName(uniquify("Java SDK: TenantIT.testCurrentTenantWithOptions Group"))
        group = dir.createGroup(group)
        deleteOnTeardown(group)

        assertNotNull group.href

        def tenant2 = client.getCurrentTenant(options)

        assertNotNull tenant2

        properties = getValue(AbstractResource, tenant2, "properties")
        assertTrue properties.get("applications").get("size") == apps + 1
        assertTrue properties.get("directories").get("size") == dirs + 1
        assertTrue properties.get("groups").get("size") == groups + 1

        // this also validates the workaround that avoids the incorrect load of expanded resources from the cache
        // https://github.com/stormpath/stormpath-sdk-java/issues/164
        assertEquals dirs + 1, tenant2.directories.size
        assertEquals apps + 1, tenant2.applications.size
        assertEquals groups + 1, tenant2.groups.size
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithoutProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithoutProvider")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "stormpath")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
    }

    /**
     * @since 1.0.RC7
     */
    @Test
    void testCreateOrganization() {
        def organizationName = uniquify("JavaSDK: OrganizationIT.testCreateOrganization")
        def org = client.instantiate(Organization)

        org.name = organizationName
        org.nameKey = UUID.randomUUID().toString().replace('-', '').substring(1,10)
        org.description = "test description"
        org.status = OrganizationStatus.ENABLED
        def createdOrg = client.currentTenant.createOrganization(org)
        deleteOnTeardown(createdOrg)

        assertEquals(createdOrg.getHref(), org.href)
        assertEquals(createdOrg.getName(), organizationName)
        assertNotNull(createdOrg.getDescription())
        assertNotNull(createdOrg.getCreatedAt())
    }

    /**
     * @since 1.0.RC7
     */
    @Test
    void testGetOrganizations() {
        def tenant = client.currentTenant
        def organizationName = uniquify("JavaSDK: OrganizationIT.testGetOrganization")
        Organization org = client.instantiate(Organization)
        org.name = organizationName
        org.nameKey = UUID.randomUUID().toString().replace('-', '').substring(1,10)
        org.status = OrganizationStatus.ENABLED
        def createdOrg = tenant.createOrganization(org)
        deleteOnTeardown(createdOrg)

        def orgList = client.getOrganizations()

        assertNotNull orgList.href
        assertTrue orgList.iterator().hasNext()
    }

    /**
     * @since 1.0.RC7
     */
    @Test
    void testGetOrganizationWithCriteria() {
        def tenant = client.currentTenant
        def organizationName = uniquify("JavaSDK: OrganizationIT.testGetOrganizationWithCriteria")
        Organization org = client.instantiate(Organization)
        org.name = organizationName
        org.nameKey = UUID.randomUUID().toString().replace('-', '').substring(1,10)
        org.status = OrganizationStatus.ENABLED
        def createdOrg = tenant.createOrganization(org)
        deleteOnTeardown(createdOrg)

        def orgList = tenant.getOrganizations(Organizations.where(Organizations.name().containsIgnoreCase("OrganizationIT.testGetOrganizationWithCriteria")))

        assertNotNull orgList.href
        assertTrue orgList.iterator().hasNext()
        def retrieved = orgList.iterator().next()
        assertEquals retrieved.href, org.href
        assertEquals retrieved.name, org.name
        assertEquals retrieved.createdAt, org.createdAt
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithGoogleProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithGoogleProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GOOGLE.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectUri("https://www.myAppURL:8090/index.jsp")
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "google")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(GoogleProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((GoogleProvider)provider).getClientId(), clientId)
        assertEquals(((GoogleProvider)provider).getClientSecret(), clientSecret)
        assertEquals(((GoogleProvider)provider).getRedirectUri(), "https://www.myAppURL:8090/index.jsp")
    }

    //@since 1.0.beta
    @Test
    void testCreateDirWithFacebookProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithFacebookProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.FACEBOOK.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "facebook")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(FacebookProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((FacebookProvider)provider).getClientId(), clientId)
        assertEquals(((FacebookProvider)provider).getClientSecret(), clientSecret)
    }

    //@since 1.0.RC3
    @Test
    void testCreateDirWithLinkedInProviderNoRedirectUri() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithLinkedInProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.LINKEDIN.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "linkedin")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(LinkedInProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((LinkedInProvider)provider).getClientId(), clientId)
        assertEquals(((LinkedInProvider)provider).getClientSecret(), clientSecret)
    }

    //@since 1.0.RC3
    @Test
    void testCreateDirWithLinkedInProviderWithRedirectUri() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithLinkedInProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")
        def redirectUri = "https://www.myAppURL:8090/index.jsp"

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.LINKEDIN.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .setRedirectUri(redirectUri)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "linkedin")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(LinkedInProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((LinkedInProvider)provider).getClientId(), clientId)
        assertEquals(((LinkedInProvider)provider).getClientSecret(), clientSecret)
        assertEquals(((LinkedInProvider)provider).getRedirectUri(), redirectUri)
    }

    //@since 1.0.0
    @Test
    void testGetAccounts() {
        def tenant = client.currentTenant
        def count = 0
        def accounts = tenant.getAccounts()
        for(Account acct : accounts) {
            count++
            //Let's iterate 3 times only, enough to know the collection was received while avoiding unnecessary traffic
            if(count > 2) break
        }
    }

    /**
     * @since 1.0.RC4.3
     */
    @Test
    void testGetAccountsWithCriteria() {
        def uniqueEmail = uniquify("myUnique") + "@email.com"

        def criteria = Accounts.criteria().add(Accounts.email().eqIgnoreCase(uniqueEmail))

        def tenant = client.currentTenant

        def originalQty = 0
        def accounts = tenant.getAccounts(criteria)
        for(Account acct : accounts) {
            originalQty++
        }

        def app = createTempApp()
        Account account = client.instantiate(Account)
        account.email = uniqueEmail
        account.username = uniquify("username")
        account.password = uniquify("paS5w&")
        account.givenName = uniquify("givenName")
        account.surname = uniquify("surname")
        account = app.createAccount(account)
        deleteOnTeardown(account)

        def newQty = 0
        accounts = tenant.getAccounts(criteria)
        for(Account acct : accounts) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetAccountsWithMap() {
        def uniqueEmail = uniquify("myUnique") + "@email.com"

        def queryParams = ['email': uniqueEmail]

        def tenant = client.currentTenant

        def originalQty = 0
        def accounts = tenant.getAccounts(queryParams);
        for(Account acct : accounts) {
            originalQty++
        }

        def app = createTempApp()
        Account account = client.instantiate(Account)
        account.email = uniqueEmail
        account.username = uniquify("username")
        account.password = uniquify("paS5w&")
        account.givenName = uniquify("givenName")
        account.surname = uniquify("surname")
        account = app.createAccount(account)
        deleteOnTeardown(account)

        def newQty = 0
        accounts = tenant.getAccounts(queryParams)
        for(Account acct : accounts) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetGroups() {
        def tenant = client.currentTenant
        def count = 0
        def groups = tenant.getGroups()
        for(Group group : groups) {
            count++
            //Let's iterate 3 times only, enough to know the collection was received while avoiding unnecessary traffic
            if(count > 2) break
        }
    }

    //@since 1.0.RC3
    @Test
    void testGetGroupsWithCriteria() {
        def uniqueName = uniquify("uniqueGroupName")

        def criteria = Groups.criteria().add(Groups.name().eqIgnoreCase(uniqueName))

        def tenant = client.currentTenant

        def originalQty = 0
        def groups = tenant.getGroups(criteria)
        for(Group grp : groups) {
            originalQty++
        }

        def app = createTempApp()
        Group group = client.instantiate(Group)
        group.name = uniqueName
        group = app.createGroup(group)
        deleteOnTeardown(group)

        def newQty = 0
        groups = tenant.getGroups(criteria)
        for(Group grp : groups) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.RC3
    @Test
    void testGetGroupsWithMap() {
        def uniqueName = uniquify("uniqueGroupName")

        def queryParams = ['name': uniqueName]

        def tenant = client.currentTenant

        def originalQty = 0
        def groups = tenant.getGroups(queryParams);
        for(Group grp : groups) {
            originalQty++
        }

        def app = createTempApp()
        Group group = client.instantiate(Group)
        group.name = uniqueName
        group = app.createGroup(group)
        deleteOnTeardown(group)

        def newQty = 0
        groups = tenant.getGroups(queryParams)
        for(Group grp : groups) {
            newQty++
        }

        assertEquals newQty, originalQty + 1
    }

    //@since 1.0.0
    @Test
    void testCreateDirWithGithubProvider() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testCreateDirWithGithubProvider")
        def clientId = uniquify("999999911111111")
        def clientSecret = uniquify("a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0")

        def request = Directories.newCreateRequestFor(dir).
                forProvider(Providers.GITHUB.builder()
                        .setClientId(clientId)
                        .setClientSecret(clientSecret)
                        .build()
                ).build()

        dir = client.currentTenant.createDirectory(request)
        deleteOnTeardown(dir)

        def provider = dir.getProvider()

        assertEquals(provider.getHref(), dir.getHref() + "/provider")
        assertEquals(provider.getProviderId(), "github")
        assertNotNull(provider.getCreatedAt())
        assertNotNull(provider.getModifiedAt())
        assertTrue(GithubProvider.isAssignableFrom(provider.getClass()))
        assertEquals(((GithubProvider)provider).getClientId(), clientId)
        assertEquals(((GithubProvider)provider).getClientSecret(), clientSecret)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testTenantExpansionWithoutCache(){

        Client client = buildClient(false);

        def tenant = client.currentTenant

        TenantOptions options = Tenants.options()
                .withApplications()
                .withGroups()

        // test options created successfully
        assertNotNull options
        assertEquals options.expansions.size(), 2

        //Test the expansion worked by reading the internal properties of the tenant
        def retrieved = client.getResource(tenant.href, Tenant.class, options)
        Map tenantProperties = getValue(AbstractResource, retrieved, "properties")
        assertTrue tenantProperties.get("groups").size() > 1
        assertTrue tenantProperties.get("applications").size() > 1
        assertTrue tenantProperties.get("accounts").size() == 1 //this is not expanded, must be 1
        def groupsQty = tenantProperties.get("groups").get("size")
        def applicationsQty = tenantProperties.get("applications").get("size")

        def app = createTempApp()
        Group group1 = client.instantiate(Group)
        group1.name = uniquify("Java SDK: TenantIT.testTenantExpansion_group1")
        group1 = app.createGroup(group1)
        deleteOnTeardown(group1)

        Group group2 = client.instantiate(Group)
        group2.name = uniquify("Java SDK: TenantIT.testTenantExpansion_group2")
        group2 = app.createGroup(group2)
        deleteOnTeardown(group2)

        //Test the expansion worked by reading the internal properties of the tenant, it must contain the recently created groups now
        retrieved = client.getResource(tenant.href, Tenant.class, options)
        tenantProperties = getValue(AbstractResource, retrieved, "properties")
        assertEquals(tenant.href, retrieved.href)

        assertTrue tenantProperties.get("groups").get("size") > groupsQty
        assertTrue tenantProperties.get("applications").get("size") > applicationsQty

    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testTenantExpansionWithCache(){

        def tenant = client.currentTenant

        TenantOptions options = Tenants.options()
                .withApplications()
                .withGroups()

        // test options created successfully
        assertNotNull options
        assertEquals options.expansions.size(), 2

        //Test the expansion worked by reading the internal properties of the tenant
        def retrieved = client.getResource(tenant.href, Tenant.class, options)
        Map tenantProperties = getValue(AbstractResource, retrieved, "properties")

        def groups = tenantProperties.get("groups").size()
        def apps =  tenantProperties.get("applications").size()
        def accounts = tenantProperties.get("accounts").size()

        assertTrue groups > 1
        assertTrue apps > 1
        assertTrue accounts == 1 //this is not expanded, must be 1

        def groupsQty = tenantProperties.get("groups").get("size")
        def applicationsQty = tenantProperties.get("applications").get("size")

        def app = createTempApp()

        Group group1 = client.instantiate(Group)
        group1.name = uniquify("Java SDK: TenantIT.testTenantExpansion_group1")
        group1 = app.createGroup(group1)
        deleteOnTeardown(group1)

        Group group2 = client.instantiate(Group)
        group2.name = uniquify("Java SDK: TenantIT.testTenantExpansion_group2")
        group2 = app.createGroup(group2)
        deleteOnTeardown(group2)

        //Test the expansion worked by reading the internal properties of the tenant, it must contain the recently created groups now
        def retrieved2 = client.getResource(tenant.href, Tenant.class, options)
        assertEquals(tenant.href, retrieved2.href)

        tenantProperties = getValue(AbstractResource, retrieved2, "properties")

        assertTrue tenantProperties.get("groups").get("size") > groupsQty
        assertTrue tenantProperties.get("applications").get("size") > applicationsQty
    }

    /**
     * @since 1.0.RC4.6
     */
    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testGetDirectoriesWithWrongTimestampFilter() {
        def tenant = client.currentTenant
        Directory directory = client.instantiate(Directory)
        directory.name = uniquify("Java SDK: TenantIT.testGetDirectoriesWithWrongTimestampFilters")
        directory = client.createDirectory(directory);
        deleteOnTeardown(directory)
        assertNotNull directory.href

        try {
            def dirList = tenant.getDirectories(
                    Directories.where(Directories.modifiedAt().matches("wrong match expression")))
            fail("should have thrown")
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 2103)
            assertTrue(e.getDeveloperMessage().contains("modifiedAt query parameter value is invalid or an unexpected type."))
        }
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test
    void testGetApplicationsWithTimestampFilter() {
        def tenant = client.currentTenant

        Application application = client.instantiate(Application)
        application.setName(uniquify("testGetApplicationsWithTimestampFilter app"))
        application = tenant.createApplication(Applications.newCreateRequestFor(application).createDirectory().build())
        deleteOnTeardown(application)
        deleteOnTeardown(application.getDefaultAccountStore());

        Date appCreationTimestamp = application.createdAt

        //equals
        def appList = client.getApplications(Applications.where(Applications.createdAt().equals(appCreationTimestamp)))
        assertNotNull appList.href

        def retrieved = appList.iterator().next()
        assertEquals retrieved.href, application.href
        assertEquals retrieved.createdAt, application.createdAt

        //gt
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().gt(appCreationTimestamp)))
        assertNotNull appList.href
        assertFalse appList.iterator().hasNext()

        //gte
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().gte(appCreationTimestamp)))
        assertNotNull appList.href
        assertTrue appList.iterator().hasNext()
        retrieved = appList.iterator().next()
        assertEquals retrieved.href, application.href
        assertEquals retrieved.name, application.name
        assertEquals retrieved.createdAt, application.createdAt

        //lt
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().lt(appCreationTimestamp)))
        assertNotNull appList.href
        assertFalse appList.iterator().hasNext()

        //lte
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().lte(appCreationTimestamp)))
        assertNotNull appList.href
        assertTrue appList.iterator().hasNext()
        retrieved = appList.iterator().next()
        assertEquals retrieved.href, application.href
        assertEquals retrieved.name, application.name
        assertEquals retrieved.createdAt, application.createdAt

        //in
        Calendar cal = Calendar.getInstance()
        cal.setTime(appCreationTimestamp)
        cal.add(Calendar.SECOND, 2)
        Date afterCreationDate = cal.getTime()

        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().in(appCreationTimestamp, afterCreationDate)))
        assertNotNull appList.href
        assertTrue appList.iterator().hasNext()
        retrieved = appList.iterator().next()
        assertEquals retrieved.href, application.href
        assertEquals retrieved.name, application.name
        assertEquals retrieved.createdAt, application.createdAt

        //in
        cal.setTime(appCreationTimestamp)
        cal.add(Calendar.SECOND, -10)
        Date newDate = cal.getTime()
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().in(newDate, new Duration(1, TimeUnit.SECONDS))))
        assertNotNull appList.href
        assertFalse appList.iterator().hasNext()

        //in
        appList = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(application.name))
                .and(Applications.createdAt().in(appCreationTimestamp, new Duration(1, TimeUnit.MINUTES))))
        assertNotNull appList.href
        assertTrue appList.iterator().hasNext()
        retrieved = appList.iterator().next()
        assertEquals retrieved.href, application.href
        assertEquals retrieved.name, application.name
        assertEquals retrieved.createdAt, application.createdAt
    }

    /**
     * @since 1.0.RC4.6
     */
    @Test (enabled = false) //ignoring because of sporadic Travis failures
    void testSaveWithResponseOptions() {
        def tenant = client.getCurrentTenant()
        def href = tenant.getHref()

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: TenantIT.testSaveWithResponseOptions-dir")
        dir = tenant.createDirectory(dir)
        deleteOnTeardown(dir)

        tenant.getCustomData().put("testData", "testDataValue")

        def retrieved = tenant.saveWithResponseOptions(Tenants.options().withDirectories().withCustomData())
        assertEquals href, retrieved.getHref()
        assertEquals "testDataValue", retrieved.getCustomData().get("testData")
        assertTrue retrieved.getDirectories().iterator().hasNext()
    }
}
