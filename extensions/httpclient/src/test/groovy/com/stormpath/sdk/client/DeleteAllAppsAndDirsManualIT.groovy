package com.stormpath.sdk.client

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.tenant.Tenant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.Test

/**
 * NEVER ENABLE THIS TEST AND THEN COMMIT TO VERSION CONTROL.  This is used purely as a quick way to blast all
 * apps and dirs in a testing tenant - it is used specifically for testing purposes only.
 * <p/>
 * IT IS NOT OK TO RUN THIS AUTOMATICALLY - EVER.
 *
 * @since 0.9
 */
@Test(enabled=false)
class DeleteAllAppsAndDirsManualIT extends ClientIT {

    private static final Logger log = LoggerFactory.getLogger(DeleteAllAppsAndDirsManualIT.class);
    private static final int NUMBER_OF_APPS_AND_DIRS_TO_CREATE = 408;

    @Test(enabled=false)
    void createAppsAndDirs() {
        def tenant = client.getCurrentTenant();
        for (int i = 0; i < NUMBER_OF_APPS_AND_DIRS_TO_CREATE; i++) {
            createAppAndDirectory(tenant);
        }
    }

    /*

   First run of the test, I get the following output:
17:35:28.726 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Apps: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 209
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:34.642 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:35:35.056 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Apps: 200      //Doesn't print out the number of leftover Dirs.

Process finished with exit code 0

      For some reason, it does not print out the leftover dirs.

   Second run:
   17:40:16.783 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Apps: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 100
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.748 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:40:19.874 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Apps: 100

Process finished with exit code 0

    Third run:
17:43:33.621 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Apps: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 50
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.536 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:43:35.593 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Apps: 50

Process finished with exit code 0

    Fourth run:
17:44:31.573 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Apps: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 25
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.641 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:44:32.661 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Apps: 25
17:44:32.682 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Dirs: 25

Process finished with exit code 0

    Fifth run:
17:45:07.013 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Apps: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of Deleted Dirs: 24
17:45:07.658 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.659 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - ************
17:45:07.677 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Apps: 25     //INCORRECT:  according to the db there is only one app left
17:45:07.699 [main] INFO  c.s.s.client.DeleteAllAppsAndDirsManualIT - Number of leftover Dirs: 25     //INCORRECT:  according to the db there is only one dir left

===============================================
Custom suite
Total tests run: 1, Failures: 0, Skips: 0
===============================================


Process finished with exit code 0
     **/
    @Test(enabled=false)
    public void deleteAll() {
        def tenant = client.getCurrentTenant();

        int counter = 0;
        def apps = tenant.getApplications();
        for (def app : apps) {
            if (!(app.getName().equals("Stormpath"))) {
                app.delete();
                counter++;
            }
        }
        log.info("Number of Deleted Apps: " + counter);

        counter = 0;
        def dirs = tenant.getDirectories();
        try {
            for (def dir : dirs) {
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

        def leftoverApps = tenant.getApplications();
        counter=0;
        for (def leftoverApp : leftoverApps) {
            counter++;
        }
            log.info("Number of leftover Apps: " + counter);

        def lefoverDirs = tenant.getDirectories();
        counter=0;
        for (def lefoverDir : lefoverDirs) {
            counter++;
        }
            log.info("Number of leftover Dirs: " + counter);

    }

    private Application createAppAndDirectory(Tenant tenant) {
        def app = client.instantiate(Application.class);
        app.setName(uniquify("Java SDK IT"));
        app.setStatus(ApplicationStatus.DISABLED);
        app.setDescription(uniquify("Test Application Description"));
        return tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build());
    }
}
