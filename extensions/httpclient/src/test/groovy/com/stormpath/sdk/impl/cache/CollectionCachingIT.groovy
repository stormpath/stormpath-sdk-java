/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.cache

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import org.testng.annotations.Test


class CollectionCachingIT extends ClientIT {

    @Test
    void testCollectionCaching() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK IT Collection Caching Dir")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        for(int i = 0; i < 10; i++) {
            Account account = client.instantiate(Account)
            account = account.setGivenName(uniquify("Test Account ${i}"))
                    .setSurname('DELETEME')
                    .setEmail(uniquify("john${i}deleteme") + "@stormpath.com")
                    .setPassword('Changeme1!')
            dir.createAccount(account);
        }

        AccountList list = dir.getAccounts(Accounts.criteria().limitTo(2)) //10 accounts, limit 2 per page = 5 pages

        println "list size: ${list.getSize()}"

        for( Account a : list) {
            a.getUsername();
            String s = a.toString()
            println s
        }

        for( Account a : list) {
            a.getUsername() //force materialization
            String s = a.toString()
            println s
        }
    }
}
