/*
 * Copyright 2016 Stormpath, Inc.
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

package com.stormpath.sdk.impl.application

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationCriteria
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.application.EnabledProperty
import com.stormpath.sdk.application.ExpandOptions
import com.stormpath.sdk.application.MeProperty
import com.stormpath.sdk.application.OAuth2Property
import com.stormpath.sdk.application.WebConfiguration
import com.stormpath.sdk.application.WebConfigurationStatus
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import org.testng.annotations.Test

import static org.testng.Assert.*

class WebConfigurationIT extends ClientIT {

    @Test
    void testGetWebConfigurationWithExpansion() {

        def requestCountingClient = buildCountingClient()

        def criteria = Applications.where(Applications.name().eqIgnoreCase("My Application")).withWebConfiguration()

        def application = getTenantApplication(requestCountingClient, criteria)

        assertEquals requestCountingClient.requestCount, 2 //Get current tenant / Get applications.

        def webConfiguration = application.webConfiguration

        assertTrue webConfiguration.getOAuth2().password.enabled

        assertEquals requestCountingClient.requestCount, 2
    }

    @Test
    void testWebConfigurationUpdateLeafProperty() {

        def webConfig = createTempApp().getWebConfiguration()

        OAuth2Property oAuth2Property = webConfig.getOAuth2()

        oAuth2Property.getPassword().setEnabled(false)

        MeProperty meProperty = webConfig.getMe()

        ExpandOptions expandOptions = meProperty.getExpand()
        expandOptions.setApiKeys(true)
        expandOptions.setApplications(true)
        expandOptions.setCustomData(true)
        expandOptions.setDirectory(true)
        expandOptions.setGroupMemberships(true)
        expandOptions.setProviderData(true)
        expandOptions.setTenant(true)
        expandOptions.setGroups(true)

        webConfig.save()

        def readWebConfig = buildClient(false).getResource(webConfig.href, WebConfiguration)

        OAuth2Property readOAuth2 = readWebConfig.getOAuth2()

        assertFalse readOAuth2.getPassword().isEnabled()

        meProperty = readWebConfig.getMe()
        expandOptions = meProperty.getExpand()

        assertTrue expandOptions.getApiKeys()
        assertTrue expandOptions.getApplications()
        assertTrue expandOptions.getCustomData()
        assertTrue expandOptions.getDirectory()
        assertTrue expandOptions.getGroups()
        assertTrue expandOptions.getGroupMemberships()
        assertTrue expandOptions.getTenant()
        assertTrue expandOptions.getProviderData()

    }

    @Test
    void testUpdateFirstLevelProperties() {

        def webConfig = createTempApp().getWebConfiguration()

        webConfig.setStatus(WebConfigurationStatus.DISABLED)
        webConfig.setSigningApiKey(null)

        String uniqueDnsLabel = uniquify("label").toLowerCase()
        webConfig.setDnsLabel(uniqueDnsLabel)
        webConfig.save()

        def readWebConfig = buildClient(false).getResource(webConfig.href, WebConfiguration)

        assertEquals readWebConfig.status, WebConfigurationStatus.DISABLED
        assertNull readWebConfig.signingApiKey
        assertEquals readWebConfig.getDnsLabel(), uniqueDnsLabel
        assertTrue readWebConfig.getDomainName().startsWith(uniqueDnsLabel)
    }

    @Test
    void testGetReferences() {

        def application = createTempApp()

        def webConfig = application.getWebConfiguration()

        assertEquals application.getHref(), webConfig.getApplication().getHref()

        assertEquals application.getTenant(), webConfig.getTenant()

    }

    @Test
    void testUpdateNullableProperties()  {

        def webConfig = createTempApp().getWebConfiguration()

        EnabledProperty verifyEmail = webConfig.getVerifyEmail()

        assertNull verifyEmail.isEnabled()

        verifyEmail.setEnabled(true)

        webConfig.save()

        def noCacheClient =  buildClient(false)

        webConfig = noCacheClient.getResource(webConfig.href, WebConfiguration)

        verifyEmail = webConfig.getVerifyEmail()

        assertTrue verifyEmail.isEnabled()

        verifyEmail.setEnabled(null)

        webConfig.save()

        webConfig = noCacheClient.getResource(webConfig.href, WebConfiguration)

        assertNull webConfig.getVerifyEmail().isEnabled()
    }

    @Test
    void testWebConfiguration_updateApiKey() {

        def criteria = Applications.where(Applications.name().eqIgnoreCase("Stormpath")).withWebConfiguration()
        def adminApp = getTenantApplication(client, criteria)

        def apiKey = createTmpApiKey(adminApp)

        def webConfig = createTempApp().getWebConfiguration()

        webConfig.setSigningApiKey(apiKey)
        webConfig.setStatus(WebConfigurationStatus.ENABLED)
        webConfig.save()

        assertNotNull webConfig.domainName
    }

    @Test
    void testWebConfiguration_updateCookieName() {

        def webConfig = createTempApp().getWebConfiguration()

        webConfig.getAccessTokenCookie().setName("an-access-token-valid-name")
        webConfig.getRefreshTokenCookie().setName("a-refresh-token-valid-name")

        webConfig.save()

        def noCacheClient = buildClient(false)

        webConfig = noCacheClient.getResource(webConfig.href, WebConfiguration)

        assertEquals "an-access-token-valid-name", webConfig.getAccessTokenCookie().getName()
        assertEquals "a-refresh-token-valid-name", webConfig.getRefreshTokenCookie().getName()

    }

    @Test
    void testEnableStormpathAdminApp_ErrorResponse() {

        def criteria = Applications.where(Applications.name().eqIgnoreCase("Stormpath")).withWebConfiguration()
        def adminApp = getTenantApplication(client, criteria)

        def apiKey = createTmpApiKey(adminApp)

        def webConfig =  adminApp.getWebConfiguration()

        assertEquals webConfig.getStatus(), WebConfigurationStatus.DISABLED
        assertNull webConfig.getDomainName()
        assertNull webConfig.getDnsLabel()

        try {
            webConfig.setStatus(WebConfigurationStatus.ENABLED)
            webConfig.setSigningApiKey(apiKey)
            webConfig.save()
            fail("should have failed")
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals e.getStatus(), 400
        }
    }

    ApiKey createTmpApiKey(Application application) {
        def directory = client.instantiate(Directory)
        directory.setName(uniquify("Admins"))

        deleteOnTeardown(directory)
        client.currentTenant.createDirectory(directory)

        ApplicationAccountStoreMapping mapping = application.addAccountStore(directory)
        mapping.setDefaultAccountStore(true)
        mapping.save()

        def adminAccount = createTestAccount(application)

        return adminAccount.createApiKey()

    }

    static Application getTenantApplication(Client client, ApplicationCriteria criteria) {

        def applications = client.getApplications(criteria)

        Iterator<Application> iterator = applications.iterator()

        assertTrue iterator.hasNext()

        return iterator.next()
    }

}
