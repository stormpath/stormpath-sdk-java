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
import com.stormpath.sdk.directory.Directory
import org.testng.annotations.Test

/**
 * @since 0.9
 */
class DirectoryAccountCustomDataIT extends AbstractCustomDataIT {

    Directory directory

    @Test
    void testCreateAccountWithCustomData() {

        def app = createApplication();
        directory = retrieveAppDirectory(app);
        deleteOnTeardown(directory);
        deleteOnTeardown(app)

        def postedCustomData = createComplexData()
        def account1 = createAccount(postedCustomData, false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), false)
        updateAccount(account1, postedCustomData, createDataForUpdate(), true)

        postedCustomData = createComplexData()
        def account2 = createAccount(postedCustomData, true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), true)
        updateAccount(account2, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account3 = createAccount(postedCustomData, false)
        updateAccount(account3, postedCustomData, [:], false)
        updateAccount(account3, postedCustomData, createDataForUpdate(), false)

        postedCustomData = [:]
        def account4 = createAccount(postedCustomData, true)
        updateAccount(account4, postedCustomData, [:], true)
        updateAccount(account4, postedCustomData, createDataForUpdate(), true)

    }

    def Account createAccount(Map postedCustomData, boolean expand) {
        def account = newAccountData()

        account.customData.putAll(postedCustomData)

        def builder = Accounts.newCreateRequestFor(account)

        builder = expand ? builder.withResponseOptions(Accounts.options().withCustomData()) : builder

        directory.createAccount(builder.build());

        assertValidCustomData(account.href + "/customData", postedCustomData, account.customData, expand)

        deleteOnTeardown(account)

        return account
    }
}
