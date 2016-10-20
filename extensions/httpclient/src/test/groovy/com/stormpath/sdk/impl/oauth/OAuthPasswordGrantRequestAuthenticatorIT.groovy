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
package com.stormpath.sdk.impl.oauth

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.organization.Organization
import com.stormpath.sdk.organization.Organizations
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.2.0
 */
class OAuthPasswordGrantRequestAuthenticatorIT extends ClientIT {
    static final String TEST_PASSWORD = 'Changeme1'

    Application testApplication;
    Account testAccount;
    Organization testOrganization;

    @BeforeMethod
    void setup() {
        testApplication = client.instantiate(Application)
        testApplication.name = uniquify("Java SDK IT oauth app")
        client.currentTenant.createApplication(Applications.newCreateRequestFor(testApplication).build())

        testOrganization = client.instantiate(Organization)
        testOrganization.name = uniquify("Java SDK IT oauth org")
        testOrganization.nameKey = uniquify("oauthIT")
        client.currentTenant.createOrganization(Organizations.newCreateRequestFor(testOrganization).createDirectoryNamed(uniquify("Java SDK IT oauth org dir")).build())

        testApplication.addAccountStore(testOrganization)

        testAccount = client.instantiate(Account)
        testAccount.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        testAccount.password = TEST_PASSWORD
        testAccount.email = testAccount.username + '@nowhere.com'
        testAccount.givenName = 'Joe'
        testAccount.surname = 'Smith'
        testOrganization.createAccount(testAccount)
    }

    @AfterMethod
    void tearDown() {
        testApplication.delete()
        (testOrganization.defaultAccountStore as Directory).delete()
        testOrganization.delete()
    }

    @Test
    void testAuthenticatedWithOrganizationNameKey() {
        def app = testApplication as DefaultApplication

        def authenticationRequest = new DefaultOAuthPasswordGrantRequestAuthentication(testAccount.email, TEST_PASSWORD).setOrganizationNameKey(testOrganization.nameKey)

        def result = app.createPasswordGrantAuthenticator().authenticate(authenticationRequest)

        assertEquals result.accessToken.expandedJwt.claims.org, testOrganization.href
    }
}
