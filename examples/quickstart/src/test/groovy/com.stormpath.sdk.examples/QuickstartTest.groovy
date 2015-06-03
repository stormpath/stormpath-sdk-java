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
package quickstart

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.ClientBuilder
import com.stormpath.sdk.client.Clients
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.tenant.Tenant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.testng.Assert.*
import static com.stormpath.sdk.application.Applications.newCreateRequestFor

/**
 * This test validates that the example code in the JSDK Quickstart Guide (http://docs.stormpath.com/java/quickstart) works
 *
 * @since 1.0.RC4.3
 */
class QuickstartTest {

    private static final Logger log = LoggerFactory.getLogger(QuickstartTest)

    private boolean deleteApplication = false;

    @Test
    void testCode() {
        try {
            String[] args={}
            Quickstart.main(args)
            assert true
        } catch(Exception e){
            fail()
        }
    }

    @BeforeTest
    void createTestData() {
        // makes sure the application required for testing exists
        ClientBuilder builder = Clients.builder();
        Client client = builder.build();
        Tenant tenant = client.getCurrentTenant();

        ApplicationList applications = tenant.getApplications(
                Applications.where(Applications.name().eqIgnoreCase("My Application"))
        );

        if (!applications.iterator().hasNext()){
            //create test application if doesn't exist
            Application application = client.instantiate(Application)
            application.setName("My Application")
            application.setDescription("Test Application created for the Quickstart Test")
            tenant.createApplication(newCreateRequestFor(application).createDirectory().build())
            deleteApplication = true
        }
    }

    @AfterTest
    void deleteTestData(){
        //deletes the account created during test
        ClientBuilder builder = Clients.builder();
        Client client = builder.build();
        Tenant tenant = client.getCurrentTenant();

        ApplicationList applications = tenant.getApplications(
            Applications.where(Applications.name().eqIgnoreCase("My Application"))
        );

        Application application;

        if (applications.iterator().hasNext()){
            application = applications.iterator().next()
        }

        AccountList accounts = application.getAccounts(
            Accounts.where(Accounts.surname().eqIgnoreCase("Quickstart_Stormtrooper"))
        );

        if (accounts.iterator().hasNext()){
            Account account = accounts.iterator().next()
            try {
                account.delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + t)
            }
        }

        // if necessary, deletes test application and directory
        if (deleteApplication){
            try {
                (application.getDefaultAccountStore() as Directory).delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + t)
            }
            try {
                application.delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + t)
            }
        }
    }
}
