package com.stormpath.sdk.impl.tenant

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.CreateApplicationRequest
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
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
                tenant.createApplication(CreateApplicationRequest.with(app).createDirectory(true).build())

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
