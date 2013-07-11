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
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.tenant.Tenant
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
    void testCaching() {

        //create a bunch of apps:

        //def tenant = client.currentTenant

        Tenant tenant = client.getResource("http://localhost:8080/v1/tenants/5swIpWp9WyHanNPQtuI7m2?expand=applications,directories", Tenant)

        /*
        120.times { i ->
            Application app = client.instantiate(Application)
            app.name = "Testing Application ${i+1} " + UUID.randomUUID().toString()

            Application created =
                tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            resourcesToDelete.add(created);
        }*/


        int count = 0;
        def hrefs = []
        def list = tenant.getApplications()
        for( Application app : list) {
            hrefs << app.href
            count++
        }
        println "Applications seen: $count"

        hrefs.each { href ->
            client.getResource(href, Application)
        }

        count = 0
        hrefs = []
        list = tenant.getDirectories()
        for( Directory directory : list) {
            hrefs << directory.href
            count++
        }
        println "Directories seen: $count"

        hrefs.each { href ->
            client.getResource(href, Directory)
        }

        tenant = client.getResource(tenant.getHref(), Tenant)

        list = tenant.getApplications()
        for(Application app : list) {
            println "Application $app.name"
        }
        list = tenant.getDirectories()
        for(Directory dir : list) {
            println "Directory $dir.name"
        }

        def s = client.dataStore.cacheManager.toString()
        println '"cacheManager": ' + s;

        Thread.sleep(20)
    }
}
