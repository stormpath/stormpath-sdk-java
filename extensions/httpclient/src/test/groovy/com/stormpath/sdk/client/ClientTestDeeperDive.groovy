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
import com.stormpath.sdk.group.GroupMembershipList
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.account.PasswordResetToken

/**
 * @since 0.1
 */
class ClientTestDeeperDive {

    public static void main(String[] args) {
        DefaultApiKey apiKey = new DefaultApiKey(args[0], args[1]);

        Client client = new Client(apiKey, "http://localhost:8080/v1")

        long start = System.currentTimeMillis();

        Tenant tenant = client.getCurrentTenant();

        ApplicationList applications = tenant.getApplications();

        Application applicationForPasswordResetSmut = null;

        boolean first=true;
        for( Application application : applications) {
            application.getName();
            if (application.getName() == "REST Application 99") {
                applicationForPasswordResetSmut = application;
            }
            println "Application $application"
            if (first) {
                println "application.name = $application.name"
                println "application.description = $application.description"
                println "application.status = $application.status"
                println "application.tenant = $application.tenant"
                println "application.accounts = $application.accounts"
//            println "application.passwordResetToken = $application.passwordResetToken"
            }
            first=false;
        }

        first=true;
        DirectoryList directories = tenant.getDirectories();

        for (Directory directory : directories) {
            directory.getName();
            println "Directory $directory";
            if (first) {
                println "directory.name = $directory.name"
                println "directory.description = $directory.description"
                println "directory.status = $directory.status"
                println "directory.accounts = $directory.accounts"
                println "directory.groups = $directory.groups"
                println "directory.tenant = $directory.tenant"
            }

            Group groupToAddMembership = null;
            GroupList groupList = directory.getGroups();
            for (Group group : groupList) {
                group.getName()
                println("- Group $group");
                if (first) {
                    println "group.name = $group.name"
                    println "group.description = $group.description"
                    println "group.status = $group.status"
                    println "group.tenant = $group.tenant"
                    println "group.directory = $group.directory"
                    println "group.accounts = $group.accounts"
                }
                if (group.getName() == "Testor Group 5") {
                    groupToAddMembership = group;
                }
                first=false;
            }

            AccountList accountList = directory.getAccounts()
            for (Account account : accountList) {
                println("-- Account $account");

                if (account.email == "Sandra@Fisher.com") {
                    GroupMembershipList memberships = account.groupMemberships
                    println(memberships)
                    println("*****")
                    for (GroupMembership groupMembership : memberships) {
                        groupMembership.account
                        println("---- $groupMembership")
                    }
                    println("*****")
                    if (groupToAddMembership) {
                        println()
//                        GroupMembership testor = client.dataStore.instantiate(GroupMembership.class)
//                        testor.create(account, groupToAddMembership);
//                        account.addGroup(groupToAddMembership);
                        groupToAddMembership.addAccount(account);
                        println("*** WORKED ***")
                    }
                    println()
                }


                if (account.emailVerificationToken) {
                    println("--- EmailVerificationToken $account.emailVerificationToken")
                    if (account.email == "bob@mupply.com") {
                        account.emailVerificationToken.save()
                    }
                    println("**************")
                    println("")
                    println("-- Account $account");
                }
            }
            if (directory.name == "Workflow Testing 3") {
                //"givenName":"Women", "surname":"InKitchen", "password":"Screwyou1", "email":"wysong@mindless.com"
                Account account = client.dataStore.instantiate(Account.class)
                account.setGivenName("SDK name")
                account.setSurname("SDK last name")
                account.setPassword("SDK3password")
                def emailAddress = "sdk6@mindless.com"
                account.setEmail(emailAddress)
                directory.createAccount(account)
                println "account = $account"
                String[] splits = account.getEmailVerificationToken().getHref().split("/")

                tenant.verifyAccountEmail(splits.last())
                account.getGivenName()
                println "account = $account"

                if (applicationForPasswordResetSmut) {
                    PasswordResetToken prt = applicationForPasswordResetSmut.createPasswordResetToken(emailAddress)
                    println "prt = $prt"

                    String[] splits2 = prt.getHref().split("/")
                    PasswordResetToken passwordResetToken = applicationForPasswordResetSmut.verifyPasswordResetToken(splits2.last())
                    passwordResetToken.getAccount()
                    println "passwordResetToken = $passwordResetToken"
                }

            }
        }

        long stop = System.currentTimeMillis();

        long duration = stop - start;

        println "Duration: $duration millis";

        System.exit(0);
    }
}
