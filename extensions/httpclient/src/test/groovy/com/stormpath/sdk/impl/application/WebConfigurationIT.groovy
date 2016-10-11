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
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import org.testng.annotations.Test

import static org.testng.Assert.*

class WebConfigurationIT extends ClientIT {

    @Test
    void testGetWebConfigurationWithExpansion() {

        def requestCountingClient = buildCountingClient()

        def criteria = Applications.where(Applications.name().eqIgnoreCase("My Application")).withWebConfiguration()

        def applications = requestCountingClient.getApplications(criteria);

        assertEquals requestCountingClient.requestCount, 2 //Get current tenant / Get applications.

        Iterator<Application> iterator = applications.iterator()

        assertTrue iterator.hasNext()

        def webConfiguration = iterator.next().webConfiguration

        assertTrue webConfiguration.getOAuth2().password.enabled

        assertEquals requestCountingClient.requestCount, 2
    }

    @Test
    void testWebConfiguration() {
        def application = createTempApp()

        def webConfig = application.getWebConfiguration()

        assertNotNull webConfig

        def test = webConfig.getForgotPassword()

        assertNotNull test
    }

}
