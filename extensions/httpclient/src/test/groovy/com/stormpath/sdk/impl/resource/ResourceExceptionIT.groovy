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
package com.stormpath.sdk.impl.resource

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directories
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

/**
 * @since 1.0.RC4.3-SNAPSHOT
 */
class ResourceExceptionIT extends ClientIT {

    /**
     * This test validates that methods in ResourceException are passing correctly the values returned by the server.
     */
    @Test
    void testMethods() {

        Application application = client.instantiate(Application)
        application.setName(uniquify("Java SDK IT App for ResourceExceptionIT"))
        def dirName = uniquify("Java SDK IT Dir for ResourceException IT")
        application = client.currentTenant.createApplication(Applications.newCreateRequestFor(application).
                createDirectoryNamed(dirName).build())
        deleteOnTeardown(application)

        def directory = client.currentTenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName))).iterator().next()
        deleteOnTeardown(directory)

        // login+password authentication request
        AuthenticationRequest request = new UsernamePasswordRequest("admin", "bar");
        try {
            AuthenticationResult result = application.authenticateAccount(request);
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            def developerMessage = "Login attempt failed because there is no Account in the Application's associated Account Stores with the specified username or email."
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 7104)
            assertEquals(developerMessage, e.getDeveloperMessage())
        }

        // authentication request for specific account store
        request = new UsernamePasswordRequest("admin", "bar", directory);
        try {
            AuthenticationResult result = application.authenticateAccount(request);
        } catch (com.stormpath.sdk.resource.ResourceException e) {
            def developerMessage = "Login attempt failed because there is no Account in the Application's associated Account Stores with the specified username or email."
            assertEquals(e.getStatus(), 400)
            assertEquals(e.getCode(), 7104)
            assertEquals(developerMessage, e.getDeveloperMessage())
        }
    }
}










