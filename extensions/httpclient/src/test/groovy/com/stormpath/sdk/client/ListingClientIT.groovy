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
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

/**
 * @since 0.1
 */
class ListingClientIT extends ClientIT {

    @Test
    void testListing() {

        Tenant tenant = client.getCurrentTenant();

        ApplicationList applications = tenant.getApplications();

        for (Application application : applications) {
            application.getName();
            println "Application $application"
        }

        DirectoryList directories = tenant.getDirectories();

        for (Directory directory : directories) {
            directory.getName();
            println "Directory $directory";

            GroupList groupList = directory.getGroups();
            for (Group group : groupList) {
                group.getName()
                println("- Group $group");
            }

            AccountList accountList = directory.getAccounts()
            for (Account account : accountList) {
                println("-- Account $account");
            }
        }
    }
}
