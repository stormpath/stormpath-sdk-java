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
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

/**
 * @since 1.1.0
 */
class PasswordStrengthPolicyIT extends ClientIT {

    /**
     * Sets the prevent reuse to 0 this means you can set the same password you have.
     */
    @Test
    void testPreventReuseDisabled() {

        def tempApp = createTempApp()

        //create a test account:
        def acct = createTestAccount(tempApp)

        def strength = acct.directory.getPasswordPolicy().strength

        strength.setPreventReuse(0)
        strength.save()

        def newPassword = "N3wP@ssw0rd!";

        def spToken = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken, newPassword)

        def spToken2 = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken2, newPassword)

    }

    /**
     * Sets the prevent reuse to 1 this means you can't set the same password you have,
     * you need at least 1 password different in between two equal passwords.
     * Using the same password twice results in a ResourceException
     */
    @Test(expectedExceptions = [ResourceException])
    void testPreventReuseEnabledError() {

        def tempApp = createTempApp()

        //create a test account:
        def acct = createTestAccount(tempApp)

        def strength = acct.directory.getPasswordPolicy().strength

        strength.setPreventReuse(1)
        strength.save()

        def newPassword = "N3wP@ssw0rd!";

        def spToken = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken, newPassword)

        def spToken2 = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken2, newPassword)
    }

    /**
     * Sets the prevent reuse to 1 this means you can't set the same password you have,
     * you need at least 1 password different in between two equal passwords.
     */
    @Test
    void testPreventReuseEnabled() {

        def tempApp = createTempApp()

        //create a test account:
        def acct = createTestAccount(tempApp)

        def strength = acct.directory.getPasswordPolicy().strength

        strength.setPreventReuse(1)
        strength.save()

        def newPassword = "N3wP@ssw0rd!";

        def spToken = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken, newPassword)

        def newPassword2 = "N3wP@ssw0rd2!";

        def spToken2 = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken2, newPassword2)

        def spToken3 = getPasswordResetToken(tempApp, acct)

        tempApp.resetPassword(spToken3, newPassword)
    }

    /**
     * @since 1.1.0
     */
    Account createTestAccount(def application) {

        //create a test account:
        def acct = client.instantiate(Account)
        def password = 'Changeme1!'
        acct.username = uniquify('Stormpath-SDK-Test-App-Acct1')
        acct.password = password
        acct.email = acct.username + '@nowhere.com'
        acct.givenName = 'Joe'
        acct.surname = 'Smith'
        acct = application.createAccount(Accounts.newCreateRequestFor(acct).setRegistrationWorkflowEnabled(false).build())
        deleteOnTeardown(acct)

        return acct
    }

    String getPasswordResetToken(def application, def account) {

        def token = application.sendPasswordResetEmail(account.email)

        return token.href.drop(token.href.lastIndexOf("/") + 1)
    }
}
