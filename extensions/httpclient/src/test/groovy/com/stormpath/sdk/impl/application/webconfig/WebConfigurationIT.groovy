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

package com.stormpath.sdk.impl.application.webconfig

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationCriteria
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig
import com.stormpath.sdk.application.webconfig.ApplicationWebConfigStatus
import com.stormpath.sdk.application.webconfig.MeConfig
import com.stormpath.sdk.application.webconfig.MeExpansionConfig
import com.stormpath.sdk.application.webconfig.Oauth2Config
import com.stormpath.sdk.application.webconfig.VerifyEmailConfig
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.oauth.AccessToken
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequestAuthenticator
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.oauth.RefreshToken
import org.testng.annotations.Test

import static org.testng.Assert.*

class WebConfigurationIT extends ClientIT {

    @Test
    void testGetWebConfigurationWithExpansion() {

        def requestCountingClient = buildCountingClient()

        def criteria = Applications.where(Applications.name().eqIgnoreCase("My Application")).withWebConfig()

        def application = getTenantApplication(requestCountingClient, criteria)

        assertEquals requestCountingClient.requestCount, 2 //Get current tenant / Get applications.

        def webConfiguration = application.webConfig

        assertTrue webConfiguration.getLogin().enabled
        assertTrue webConfiguration.getOAuth2().enabled
        assertFalse webConfiguration.getMobileCallback().enabled

        assertEquals requestCountingClient.requestCount, 2
    }

    @Test
    void testWebConfigurationUpdateLeafProperty() {

        def webConfig = createTempApp().getWebConfig()

        webConfig.getLogin().setEnabled(false)
        webConfig.getRegister().setEnabled(false)
        webConfig.getMobileCallback().setEnabled(true)

        Oauth2Config oauth2Config = webConfig.getOAuth2()
        oauth2Config.setEnabled(false)

        MeConfig meConfig = webConfig.getMe()

        meConfig.setEnabled(false)

        meConfig.getExpansions().setApiKeys(true).setApplications(true).setCustomData(true).setDirectory(true)
                .setGroupMemberships(true).setProviderData(true).setTenant(true).setGroups(true)

        webConfig.save()

        def readWebConfig = buildClient(false).getResource(webConfig.href, ApplicationWebConfig)

        assertFalse readWebConfig.getRegister().isEnabled()
        assertFalse readWebConfig.getLogin().isEnabled()
        assertTrue readWebConfig.getMobileCallback().isEnabled()

        Oauth2Config readOAuth2 = readWebConfig.getOAuth2()

        assertFalse readOAuth2.isEnabled()
        meConfig = readWebConfig.getMe()
        MeExpansionConfig expansions = meConfig.getExpansions()

        assertFalse meConfig.isEnabled()
        assertTrue expansions.apiKeys
        assertTrue expansions.applications
        assertTrue expansions.customData
        assertTrue expansions.directory
        assertTrue expansions.groups
        assertTrue expansions.groupMemberships
        assertTrue expansions.tenant
        assertTrue expansions.providerData
    }

    @Test
    void testUpdateFirstLevelProperties() {

        def webConfig = createTempApp().getWebConfig()

        webConfig.setStatus(ApplicationWebConfigStatus.DISABLED)
        webConfig.setSigningApiKey(null)

        String uniqueDnsLabel = uniquify("label").toLowerCase()
        webConfig.setDnsLabel(uniqueDnsLabel)
        webConfig.save()

        def readWebConfig = buildClient(false).getResource(webConfig.href, ApplicationWebConfig)

        assertEquals readWebConfig.status, ApplicationWebConfigStatus.DISABLED
        assertNull readWebConfig.signingApiKey
        assertEquals readWebConfig.getDnsLabel(), uniqueDnsLabel
        assertTrue readWebConfig.getDomainName().startsWith(uniqueDnsLabel)
    }

    @Test
    void testGetReferences() {
        def application = createTempApp()
        def webConfig = application.getWebConfig()
        assertEquals application.getHref(), webConfig.getApplication().getHref()
        assertEquals application.getTenant(), webConfig.getTenant()
    }

    @Test
    void testUpdateNullableProperties()  {
        def webConfig = createTempApp().getWebConfig()

        VerifyEmailConfig verifyEmail = webConfig.getVerifyEmail()
        assertNull verifyEmail.isEnabled()
        verifyEmail.setEnabled(true)
        webConfig.save()

        def noCacheClient =  buildClient(false)

        webConfig = noCacheClient.getResource(webConfig.href, ApplicationWebConfig)
        verifyEmail = webConfig.getVerifyEmail()
        assertTrue verifyEmail.isEnabled()

        verifyEmail.setEnabled(null)
        webConfig.save()
        webConfig = noCacheClient.getResource(webConfig.href, ApplicationWebConfig)
        assertNull webConfig.getVerifyEmail().isEnabled()
    }

    @Test
    void testWebConfiguration_updateApiKey() {
        def criteria = Applications.where(Applications.name().eqIgnoreCase("Stormpath")).withWebConfig()
        def adminApp = getTenantApplication(client, criteria)

        def apiKey = createTmpApiKey(adminApp)

        def webConfig = createTempApp().getWebConfig()

        webConfig.setSigningApiKey(apiKey)
        webConfig.setStatus(ApplicationWebConfigStatus.ENABLED)
        webConfig.save()

        assertNotNull webConfig.domainName
    }

    @Test
    void testEnableStormpathAdminApp_ErrorResponse() {
        def criteria = Applications.where(Applications.name().eqIgnoreCase("Stormpath")).withWebConfig()
        def adminApp = getTenantApplication(client, criteria)

        def apiKey = createTmpApiKey(adminApp)

        def webConfig =  adminApp.getWebConfig()

        assertEquals webConfig.getStatus(), ApplicationWebConfigStatus.DISABLED
        assertNull webConfig.getDomainName()
        assertNull webConfig.getDnsLabel()

        try {
            webConfig.setStatus(ApplicationWebConfigStatus.ENABLED)
            webConfig.setSigningApiKey(apiKey)
            webConfig.save()
            fail("should have failed")
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            assertEquals e.getStatus(), 400
        }
    }

    @Test
    void testGetAccessTokenSignedWithDifferentKey() {

        def app = createTempApp()

        def account = createTestAccount(app)

        OAuthPasswordGrantRequestAuthentication grantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
                .setLogin(account.email).setPassword("Changeme1!").build()

        OAuthRequestAuthenticator authenticator = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app)

        def accessTokenResult = authenticator.authenticate(grantRequest)

        def webConfigApiKey = app.getWebConfig().getSigningApiKey()

        def client = Clients.builder().setBaseUrl(baseUrl).setCacheManager(Caches.newDisabledCacheManager()).setApiKey(webConfigApiKey).build()

        def newClientApp = client.getResource(app.href, Application)

        // Authenticate token against Stormpath
        OAuthBearerRequestAuthentication authRequest = OAuthRequests.OAUTH_BEARER_REQUEST.builder().setJwt(accessTokenResult.getAccessTokenString()).build()
        def authResultRemote = Authenticators.OAUTH_BEARER_REQUEST_AUTHENTICATOR.forApplication(newClientApp).authenticate(authRequest)

        assertEquals authResultRemote.getApplication().getHref(), app.href
        assertEquals authResultRemote.getAccount().getHref(), account.href

        def accessToken = client.getResource(accessTokenResult.accessTokenHref, AccessToken)

        assertNotNull accessToken

        def refreshToken = client.getResource(accessTokenResult.refreshToken.href, RefreshToken)

        assertNotNull refreshToken
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
