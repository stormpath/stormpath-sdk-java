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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationCriteria
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.application.EnabledProperty
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
        webConfig.save()

        def readWebConfig = buildClient(false).getResource(webConfig.href, WebConfiguration)

        OAuth2Property readOAuth2 = readWebConfig.getOAuth2()

        assertFalse readOAuth2.getPassword().isEnabled()
    }

    @Test
    void testUpdateNullableProperties()  {

        def webConfig = createTempApp().getWebConfiguration()

        EnabledProperty enabledProperty = webConfig.getVerifyEmail()

        assertNull enabledProperty.isEnabled()

    }

    @Test
    void enableWebConfiguration() {

        def criteria = Applications.where(Applications.name().eqIgnoreCase("My Application")).withWebConfiguration()
        def adminApplication = getTenantApplication(client, criteria)

        def directory = client.instantiate(Directory)
        directory.setName(uniquify("Admins"))

        deleteOnTeardown(directory)
        client.currentTenant.createDirectory(directory)

        ApplicationAccountStoreMapping mapping = adminApplication.addAccountStore(directory)
        mapping.setDefaultAccountStore(true)
        mapping.save()

        def adminAccount = createTestAccount(adminApplication)

        def apiKey = adminAccount.createApiKey()

        def webConfig = createTempApp().getWebConfiguration()

        webConfig.setSigningApiKey(apiKey)
        webConfig.setStatus(WebConfigurationStatus.ENABLED)
        webConfig.save()

        assertNotNull webConfig.domainName
    }

    static Application getTenantApplication(Client client, ApplicationCriteria criteria) {

        def applications = client.getApplications(criteria)

        Iterator<Application> iterator = applications.iterator()

        assertTrue iterator.hasNext()

        return iterator.next()
    }

}
