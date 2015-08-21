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
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.impl.client.RequestCountingClient
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class CollectionIterationIT extends ClientIT {

    /**
     * This test asserts three things:
     *
     * 1.  When iterating over a paginated collection, and each page is totally full (i.e. the page limit is equal to
     *     the last page's items array size), that the number of accounts is the same number as expected, and
     *
     * 2.  When iterating over a new instance of the same collection (i.e. a new query is executed), and the page limit
     *     is greater than the last page's items array size (last page is not totally full), that the number of
     *     accounts is the same number as expected.
     *
     * 3.  When each collection element has a collection expansion enabled (i.e. get a list of accounts and each
     *     account has its groups collection expanded), that the same number of requests to the server occur.  In
     *     other words, iterating over a collection when its elements also have an expanded collection does not incur
     *     additional requests for the nested collections.
     *
     * This ensures that collection iteration always exhausts the entire collection regardless of how pages are
     * chunked.
     *
     * These three things could have been asserted as separate tests, but were written as one so that we don't have
     * to hammer the API servers more than necessary.
     */
    @Test
    void testCollectionIteration() {

        RequestCountingClient client = buildCountingClient();

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK IT Dir")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        //1 to get the current tenant (required for all tenant operations, 1 to create the directory
        assertEquals client.requestCount, 2
        client.resetRequestCount()

        //let's create a group and assign it to some accounts to ensure that collection expansion does not
        //cause additional requests:
        final String GROUP_NAME = 'foo'
        Group group = client.instantiate(Group)
        group.name = GROUP_NAME
        group = dir.createGroup(group)

        //one request to create a group:
        assertEquals client.requestCount, 1
        client.resetRequestCount()

        final int PAGE_SIZE = 5;
        final int NUM_PAGES = 3;
        final int NUM_ACCOUNTS = PAGE_SIZE * NUM_PAGES;

        //collect the accounts as we create them so we can assign the group to some of them afterwards:
        List<Account> createdAccounts = new ArrayList<>(NUM_ACCOUNTS);

        for(int i = 0; i < NUM_ACCOUNTS; i++) {
            Account account = client.instantiate(Account)
            account = account.setGivenName(uniquify("Test Account ${i}"))
                    .setSurname('DELETEME')
                    .setEmail(uniquify("john${i}deleteme") + "@stormpath.com")
                    .setPassword('Changeme1!')
            dir.createAccount(account);
            deleteOnTeardown(account)
        }

        //15 new accounts = 15 requests
        assertEquals client.requestCount, NUM_ACCOUNTS
        client.resetRequestCount();

        int counter = 0;
        int assignedCount = 0;
        for(Account account : createdAccounts) {
            if (counter % 2 != 0) { //let's assign the group to every other account so that not account.getGroups() calls are identical:
                account.addGroup(group);
                assignedCount++
            }
        }

        //1 request per group assignment
        assertEquals client.requestCount, assignedCount
        client.resetRequestCount()

        //15 accounts, 5 per page = 3 full pages
        AccountList list = dir.getAccounts(Accounts.criteria().limitTo(PAGE_SIZE))

        assertEquals list.size, NUM_ACCOUNTS

        //now ensure local iteration count matches the expected number
        int count = 0;
        for( Account a : list) {
            count++;
        }
        assertEquals count, NUM_ACCOUNTS

        //ensure that we only execute one request per page:
        assertEquals client.requestCount, NUM_PAGES
        client.resetRequestCount()

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

        //While the pages were not all the same size, we should still see the same number of pages for this
        //particular test:
        assertEquals client.requestCount, NUM_PAGES
        client.resetRequestCount()

        //now test that collection expansion *does not* incur further requests:
        //execute the same queries as above, but expand the groups collection for each account.
        //In this test, we didn't assign any groups to the accounts, so we won't see any results, but we should see
        //that each account's groups collection is empty, and any attempt at acquiring the groups or iterating over
        //them will *not* incur further requests.  So, at the end of this next code block, the total number of
        //requests should equal the total number of pages - no more
        list = dir.getAccounts(Accounts.criteria().limitTo(PAGE_SIZE).withGroups())

        //now ensure local iteration count matches the same expected number
        count = 0
        int accountsWithGroups = 0;
        for( Account a : list) {

            //iterate over the accounts groups collection to force the collection to be materialized
            for( Group g : a.groups) {
                assertEquals g.name, GROUP_NAME
                accountsWithGroups++
            }
            count++
        }
        assertEquals count, NUM_ACCOUNTS
        assertEquals accountsWithGroups, assignedCount

        //and finally, assert that, with account group expansion, we still only had 3 total requests (1 per page):
        assertEquals client.requestCount, NUM_PAGES
        client.resetRequestCount()
    }
}
