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
package com.stormpath.sdk.client;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;


/**
 * @since 0.9
 */
public class DeleteAllAppsAndDirsIT  {

    private static final Logger log = LoggerFactory.getLogger(DeleteAllAppsAndDirsIT.class);
    private static final int NUMBER_OF_APPS_AND_DIRS_TO_CREATE = 408;


    String apiKeyFileLocation = System.getProperty("user.home") + "/.stormpath/iam/localhost/apiKey.properties";
    String baseUrl = "http://localhost:8080/v1";
    Client client;

    @BeforeClass
    void setupClient() {
        client = buildClient();
    }

    Client buildClient() {
        return new ClientBuilder().setBaseUrl(baseUrl)
                .setApiKeyFileLocation(apiKeyFileLocation)
                .setCacheManager(Caches.newCacheManager().build())
                .build();
    }

    protected static String uniquify(String s) {
        return s + " " + UUID.randomUUID().toString();
    }

    @Test
    public void createAppsAndDirs() {
        Tenant tenant = client.getCurrentTenant();
        for (int i = 0; i < NUMBER_OF_APPS_AND_DIRS_TO_CREATE; i++) {
            createAppAndDirectory(tenant);
        }

    }

    /*

   First run of the test, I get the following output:
17:35:28.726 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Apps: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:35:35.056 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Apps: 200      //Doesn't print out the number of leftover Dirs.

Process finished with exit code 0

      For some reason, it does not print out the leftover dirs.

   Second run:
   17:40:16.783 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Apps: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:40:19.874 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Apps: 100

Process finished with exit code 0

    Third run:
17:43:33.621 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Apps: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:43:35.593 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Apps: 50

Process finished with exit code 0

    Fourth run:
17:44:31.573 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Apps: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:44:32.661 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Apps: 25
17:44:32.682 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Dirs: 25

Process finished with exit code 0

    Fifth run:
17:45:07.013 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Apps: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of Deleted Dirs: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - ************
17:45:07.677 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Apps: 25     //INCORRECT:  according to the db there is only one app left
17:45:07.699 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsIT - Number of leftover Dirs: 25     //INCORRECT:  according to the db there is only one dir left

===============================================
Custom suite
Total tests run: 1, Failures: 0, Skips: 0
===============================================


Process finished with exit code 0





     **/

    @Test
    public void deleteAll() {
        Tenant tenant = client.getCurrentTenant();

        int counter = 0;
        ApplicationList apps = tenant.getApplications();
        for (Application app : apps) {
            if (!(app.getName().equals("Stormpath"))) {
                app.delete();
                counter++;
            }
        }
        log.info("Number of Deleted Apps: " + counter);

        counter = 0;
        DirectoryList dirs = tenant.getDirectories();
        try {
            for (Directory dir : dirs) {
                if (!(dir.getName().equals("Stormpath Administrators"))) {
                    dir.delete();
                    counter++;
                }
            }
        } finally {
            log.info("Number of Deleted Dirs: " + counter);
            log.info("************");
            log.info("************");
            log.info("************");
            log.info("************");
        }
        log.info("Number of Deleted Dirs: " + counter);
        log.info("************");
        log.info("************");
        log.info("************");
        log.info("************");   //extra logs are because sometimes testNg will not print everything.

        ApplicationList leftoverApps = tenant.getApplications();
        counter=0;
        for (Application leftoverApp : leftoverApps) {
            counter++;
        }
            log.info("Number of leftover Apps: " + counter);

        DirectoryList lefoverDirs = tenant.getDirectories();
        counter=0;
        for (Directory lefoverDir : lefoverDirs) {
            counter++;
        }
            log.info("Number of leftover Dirs: " + counter);

    }

    private void createAppAndDirectory(Tenant tenant) {
        Application app = client.instantiate(Application.class);
        app.setName(uniquify("Test Application"));
        app.setStatus(ApplicationStatus.DISABLED);
        app.setDescription(uniquify("Test Application Description"));

        app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build());
    }


}
