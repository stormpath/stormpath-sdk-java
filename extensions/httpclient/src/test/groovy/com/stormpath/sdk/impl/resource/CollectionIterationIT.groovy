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
package com.stormpath.sdk.impl.resource

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class CollectionIterationIT extends ClientIT {

    /**
     * This test asserts two things:
     *
     * 1.  When iterating over a paginated collection, and each page is totally full (i.e. the page limit is equal to
     *     the last page's items array size), that the number of accounts is the same number as expected, and
     *
     * 2.  When iterating over a new instance of the same collection (i.e. a new query is executed), and the page limit
     *     is greater than the last page's items array size (last page is not totally full), that the number of
     *     accounts is the same number as expected.
     *
     * This ensures that collection iteration always exhausts the entire collection regardless of how pages are
     * chunked.
     */
    @Test
    void testCollectionIteration() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK IT Collection Caching Dir")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        final int PAGE_SIZE = 5;
        final int NUM_PAGES = 3;
        final int NUM_ACCOUNTS = PAGE_SIZE * NUM_PAGES;

        for(int i = 0; i < NUM_ACCOUNTS; i++) {
            Account account = client.instantiate(Account)
            account = account.setGivenName(uniquify("Test Account ${i}"))
                    .setSurname('DELETEME')
                    .setEmail(uniquify("john${i}deleteme") + "@stormpath.com")
                    .setPassword('Changeme1!')
            dir.createAccount(account);
        }

        //15 accounts, 5 per page = 3 full pages
        AccountList list = dir.getAccounts(Accounts.criteria().limitTo(PAGE_SIZE))

        assertEquals list.size, NUM_ACCOUNTS

        //now ensure local iteration count matches the expected number
        int count = 0;
        for( Account a : list) {
            count++;
        }
        assertEquals count, NUM_ACCOUNTS

        //now let's change up the pagination params so that the final page is not a full page.
        //15 accounts, 6 per page = 2 full pages with 12 accounts and one final page with 3 accounts:
        list = dir.getAccounts(Accounts.criteria().limitTo(PAGE_SIZE + 1))

        assertEquals list.size, NUM_ACCOUNTS

        //now ensure local iteration count matches the same expected number
        count = 0
        for( Account a : list) {
            count++
        }
        assertEquals count, NUM_ACCOUNTS
    }
}
