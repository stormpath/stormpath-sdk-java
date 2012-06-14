/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.http

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.client.DefaultApiKey
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.tenant.Tenant

/**
 * @since 0.1
 */
class ClientTest {

    public static void main(String[] args) {
        DefaultApiKey apiKey = new DefaultApiKey(args[0], args[1]);

        Client client = new Client(apiKey, "http://localhost:8080/v1")

        long start = System.currentTimeMillis();

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

        long stop = System.currentTimeMillis();

        long duration = stop - start;

        println "Duration: $duration millis";

        System.exit(0);
    }
}
