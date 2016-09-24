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
package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.authc.UsernamePasswordRequests
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue
import static org.testng.Assert.fail

/**
 * @since 1.1.0
 */
class PasswordStrengthPolicyIT extends ClientIT {

    /**
     * Sets the prevent reuse to 0 this means you can set the same password that is currently being used.
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

        def spToken = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken, newPassword)

        def spToken2 = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken2, newPassword)

        //Checking "newPassword" works
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(acct.username).setPassword(newPassword).build()
        def result = tempApp.authenticateAccount(request)

        def loginAccount = result.getAccount()

        assertEquals loginAccount.username, acct.username
    }

    /**
     * Sets the prevent reuse to 1 this means you can't set the same password you have,
     * you need at least 1 password different in between two equal passwords.
     * Using the same password twice results in a ResourceException
     */
    @Test
    void testPreventReuseEnabledError() {

        def tempApp = createTempApp()

        //create a test account:
        def acct = createTestAccount(tempApp)

        def strength = acct.directory.getPasswordPolicy().strength

        strength.setPreventReuse(1)
        strength.save()

        def newPassword = "N3wP@ssw0rd!";

        def spToken = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken, newPassword)

        def spToken2 = tempApp.sendPasswordResetEmail(acct.email).getValue()

        try {
            tempApp.resetPassword(spToken2, newPassword)
            fail ("Should have failed due to same password usage and password prevent reuse set to 1")
        } catch (Exception e){
            assertTrue e instanceof ResourceException
            assertEquals(((ResourceException)e).getCode(), 6401)
        }
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

        def spToken = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken, newPassword)

        def newPassword2 = "N3wP@ssw0rd2!";

        def spToken2 = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken2, newPassword2)

        def spToken3 = tempApp.sendPasswordResetEmail(acct.email).getValue()

        tempApp.resetPassword(spToken3, newPassword)

        //Checking "newPassword" works
        def request = UsernamePasswordRequests.builder().setUsernameOrEmail(acct.username).setPassword(newPassword).build()
        def result = tempApp.authenticateAccount(request)

        def loginAccount = result.getAccount()

        assertEquals loginAccount.username, acct.username
    }

}
