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
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.api.ApiKey

import static org.testng.Assert.*

/**
 * This test validates that the example code in the JSDK Quickstart Guide (http://docs.stormpath.com/java/quickstart) works
 *
 * @since 1.0-SNAPSHOT
 */
class SdkQuickstartIT extends ClientIT {

    @Test
    void testAuthSchemeForGoogleAppEngine(){
        String path = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
        ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();

        //If using Google App Engine, you must use Basic authentication:
        Client client = Clients.builder().setApiKey(apiKey)
            .setAuthenticationScheme(AuthenticationScheme.BASIC)
            .build();

        assertNotNull client
        assertEquals client.getApiKey().getId(), apiKey.getId()
    }

    @Test
    void testCode() {

        // Create a Client

        // Build the Api Key object
        String path = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
        ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();

        // Instantiate a builder for your client and set required properties
        ClientBuilder builder = Clients.builder()
        builder.setApiKey(apiKey)
        builder.setBaseUrl(super.getBaseUrl())

        // Build the client instance that you will use throughout your application code
        Client client = builder.build();

        assertNotNull client
        assertEquals client.getApiKey().getId(), apiKey.getId()

        // Obtain your default tenant
        Tenant tenant = client.getCurrentTenant();

        // Retrieve your application
        ApplicationList applications = tenant.getApplications(
            Applications.where(Applications.name().eqIgnoreCase("My Application"))
        );

        assertTrue applications.iterator().hasNext()

        def count = 0
        for(Application applicationIt : applications) {
            count++
        }
        assertEquals(count, 1)

        Application application = applications.iterator().next()
        assertEquals application.name, "My Application"

        // Create a User Account

        //Create the account object
        Account account = client.instantiate(Account.class);

        //Set the account properties
        account.setGivenName("Joe");
        account.setSurname("Stormtrooper");
        account.setUsername("tk421"); //optional, defaults to email if unset
        account.setEmail("tk421@stormpath.com");
        account.setPassword("Changeme1");
        CustomData customData = account.getCustomData();
        customData.put("favoriteColor", "white");

        //Create the account using the existing Application object
        application.createAccount(account);
        deleteOnTeardown(account)

        AccountList accounts = application.getAccounts(
            Accounts.where(Accounts.username().eqIgnoreCase(account.username))
        );

        assertTrue accounts.iterator().hasNext()

        count = 0
        for(Account accountIt : accounts) {
            count++
        }
        assertEquals(count, 1)

        def retrieved = accounts.iterator().next()
        assertEquals retrieved.href, account.href

        // Search for a User Account

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("email", "tk421@stormpath.com");
        accounts = application.getAccounts(queryParams);

        assertTrue accounts.iterator().hasNext()

        count = 0
        for(Account accountIt : accounts) {
            count++
        }
        assertEquals(count, 1)

        retrieved = accounts.iterator().next()
        assertEquals retrieved.href, account.href

        // Authenticate a User Account

        String usernameOrEmail = "tk421@stormpath.com";
        String rawPassword = "Changeme1";

        // Create an authentication request using the credentials
        AuthenticationRequest request = new UsernamePasswordRequest(usernameOrEmail, rawPassword);

        // Now let's authenticate the account with the application:
        AuthenticationResult result = application.authenticateAccount(request);
        def retrievedAccount = result.getAccount();

        assertNotNull retrievedAccount
        assertEquals retrievedAccount.href, account.href


    }

}
