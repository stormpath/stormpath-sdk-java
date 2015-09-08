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

import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.impl.directory.DefaultDirectory
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.Test

import static org.easymock.EasyMock.createStrictMock
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertSame

/**
 * @since 1.0.0
 */
class DefaultVerificationEmailRequestBuilderTest {

    @Test
    void testBuilder() {
        String login = "myemail@mydomain.com"

        def requestBuilder = Applications.verificationEmailBuilder();
        def request = requestBuilder.setLogin(login).build()

        assertSame request.getLogin(), login
        assertNull request.getAccountStore()

        request = requestBuilder.setAccountStore(null).build()

        assertSame request.getLogin(), login
        assertNull request.getAccountStore()

        def properties = [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf",
                          name: "My Directory",
                          description: "My Description",
                          accounts: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/accounts"],
                          groups: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/groups"],
                          tenant: [href: "https://api.stormpath.com/v1/tenants/jdhrgojeorigjj09etiij"],
                          provider: [href: "https://api.stormpath.com/v1/directories/iouertnw48ufsjnsDFSf/provider"]
        ]

        def dir = new DefaultDirectory(createStrictMock(InternalDataStore), properties)
        request = requestBuilder.setAccountStore(dir).build()

        assertSame request.getLogin(), login
        assertSame request.getAccountStore(), dir
    }

    @Test
    void testAccountStoreNotSpecified() {
        def requestBuilder = Applications.verificationEmailBuilder()
        def request = requestBuilder.setLogin("myemail@mydomain.com").build()
        assertNull request.getAccountStore()
    }

    @Test(expectedExceptions = IllegalStateException)
    void testLoginNotSpecified() {
        def requestBuilder = Applications.verificationEmailBuilder();
        requestBuilder.build()
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testNullLogin() {
        def requestBuilder = Applications.verificationEmailBuilder();
        requestBuilder.setLogin(null)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInvalidLogin() {
        def requestBuilder = Applications.verificationEmailBuilder();
        requestBuilder.setLogin("")
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInvalidAccountStore() {
        String login = "myemail@mydomain.com"

        def requestBuilder = Applications.verificationEmailBuilder();
        def dir = new DefaultDirectory(createStrictMock(InternalDataStore))
        requestBuilder.setLogin(login).setAccountStore(dir).build()
    }

}
