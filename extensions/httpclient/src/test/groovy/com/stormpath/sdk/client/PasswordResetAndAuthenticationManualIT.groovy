/*
 * Copyright 2013 Stormpath, Inc.
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
import com.stormpath.sdk.account.PasswordResetToken
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.tenant.Tenant

/**
 * This is a test for Password Reset and Authentication functionality.
 *  This class takes in four arguments from the command line.  The first
 *  and second argument is the ApiKey id and secret key respectively.
 *  The third argument is the old password for the account.  The fourth
 *  argument is the new password you wish to set to the account.
 *
 *  You will have to run this test the first time to see what account
 *  is being returned from your system.
 *
 *  TODO: Create an account in the Application so less human interaction will be needed.
 *
 * @since 0.4
 */
class PasswordResetAndAuthenticationManualIT {

//     1 - I create a password reset token
//     2 - I get the PasswordResetToken from the emailed token
//     3 - I get the Account from the PasswordResetToken
//     4 - I set the new password on the Account
//     5 - I call Account.save()
//     6 - I try to authenticate with the new password
//     7 - Authentication fails
    public static void main(String[] args) {
        DefaultApiKey apiKey = new DefaultApiKey(args[0], args[1]);
        String oldPassword = args[2];
        String newPassword = args[3];

        Client client = new Client(apiKey, "http://localhost:8080/v1")

        long start = System.currentTimeMillis();

        Tenant tenant = client.getCurrentTenant();

        Application appToTest;
        Account acctToTest;

        ApplicationList applications = tenant.getApplications()
        for (Application app : applications) {
            if (app.name != "Stormpath IAM") {
                for (Account acct : app.accounts) {
                    acctToTest = acct;
                    appToTest = app;
                    break;
                }
            }
        }

        if (!acctToTest) {
            println("Found no account to test with!!!")
            System.exit(0)
        }

//        acctToTest.directory
//        appToTest.accounts
//        println "appToTest = $appToTest"
//        println "acctToTest = $acctToTest"

        UsernamePasswordRequest request = new UsernamePasswordRequest(acctToTest.username, oldPassword)
        println(appToTest.authenticateAccount(request).getAccount())

//     1 - I create a password reset token
        PasswordResetToken token = appToTest.createPasswordResetToken(acctToTest.email)
//        token.email
//        println "token = $token"
//     2 - I get the PasswordResetToken from the emailed token

//     3 - I get the Account from the PasswordResetToken
        Account accountFromToken = token.account
        request = new UsernamePasswordRequest(accountFromToken.username, oldPassword)
        appToTest.authenticateAccount(request)

//     4 - I set the new password on the Account
        accountFromToken.setPassword(newPassword)
//     5 - I call Account.save()
        accountFromToken.save()

//     6 - I try to authenticate with the new password
        request = new UsernamePasswordRequest(accountFromToken.username, newPassword)
        appToTest.authenticateAccount(request)

        long stop = System.currentTimeMillis();
        long duration = stop - start;

        println "Duration: $duration millis";

        System.exit(0);
    }
}
