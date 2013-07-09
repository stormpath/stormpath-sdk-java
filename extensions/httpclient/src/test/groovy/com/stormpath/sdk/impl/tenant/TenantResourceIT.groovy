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
package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.application.DefaultCreateApplicationRequest
import com.stormpath.sdk.resource.Deletable
import org.testng.annotations.AfterClass
import org.testng.annotations.Test

/**
 * @since 0.9
 */
class TenantResourceIT extends ClientIT {

    private static List<Deletable> resourcesToDelete = new ArrayList<Deletable>();

    @AfterClass
    void delete() {
        resourcesToDelete.reverse().each { Deletable resource ->
            resource.delete()
        }
    }

    @Test
    void testDelete() {
        def list = client.currentTenant.directories
        list.each { Directory dir ->
            if (!dir.name.equals('Stormpath Administrators')) {
                resourcesToDelete.add(dir);
            }
        }
    }

    @Test
    void testSearch() {

        //create a bunch of apps:

        def tenant = client.currentTenant

        120.times { i ->
            Application app = client.instantiate(Application)
            app.name = "Testing Application ${i+1} " + UUID.randomUUID().toString()

            Application created =
                tenant.createApplication(DefaultCreateApplicationRequest.with(app).createDirectory(true).build())

            resourcesToDelete.add(created);
        }

        def list = tenant.getApplications([limit: 50])

        int count = 0;
        for( Application app : list) {
            count++
        }
        println "Applications seen: $count"
    }
}
