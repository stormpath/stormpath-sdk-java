/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.servlet.utils

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.application.DefaultApplication
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.servlet.api.ServletApiRequestAuthenticator
import com.stormpath.sdk.servlet.oauth.ServletOauthRequestAuthenticator
import com.stormpath.sdk.servlet.util.Servlets
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.*

/**
 * @since 1.0.RC4.3-SNAPSHOT
 */
class ServletsTest {

    InternalDataStore internalDataStore;

    @BeforeMethod
    void setUp() {
        internalDataStore = createStrictMock(InternalDataStore)
    }

    @Test
    void testMethods(){

        Application application;

        def properties = [href: "https://enterprise.stormpath.com/v1/applications/jefoifj93riu23ioj"]
        application = new DefaultApplication(internalDataStore, properties)

        assertTrue(Servlets.servletApiRequestAuthenticator(application) instanceof ServletApiRequestAuthenticator)

        assertTrue(Servlets.servletOauthRequestAuthenticator(application) instanceof ServletOauthRequestAuthenticator)

    }

    @Test
    void testErrors(){

        try {
            Servlets.servletApiRequestAuthenticator(null)
            fail('application argument cannot be null.')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'application argument cannot be null.'
        }

        try {
            Servlets.servletOauthRequestAuthenticator(null)
            fail('application argument cannot be null.')
        } catch (IllegalArgumentException iae) {
            assertEquals iae.message, 'application argument cannot be null.'
        }

    }
}
