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


package com.stormpath.sdk.client

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.Caches
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.client.DefaultClientBuilder
import com.stormpath.sdk.impl.client.RequestCountingClient
import com.stormpath.sdk.resource.CollectionResource
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.resource.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeTest
import org.testng.collections.Lists

import static com.stormpath.sdk.application.Applications.newCreateRequestFor

abstract class ClientIT {

    private static final Logger log = LoggerFactory.getLogger(ClientIT)

    String baseUrl = 'https://api.stormpath.com/v1'
    Client client
    String runId

    List<Deletable> resourcesToDelete;

    @BeforeClass
    void setupClient() {
        client = buildClient()
        runId = UUID.randomUUID().toString().replace('-', '')
    }

    @BeforeTest
    public void setUp() {
        resourcesToDelete = []
    }

    @AfterTest
    public void tearDown() {
        def reversed = resourcesToDelete.reverse() //delete in opposite order (cleaner - children deleted before parents)

        for (def r : reversed) {
            try {
                r.delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + r, t)
            }
        }
    }

    protected void deleteOnTeardown(Deletable d) {
        this.resourcesToDelete.add(d)
    }

    //NOTE ABOUT THE STORMPATH_API_KEY_ID and STORMPATH_API_KEY_SECRET env vars below:
    // you can either set them in the OS, or, if you're on the Stormpath dev team, set it in IntelliJ's defaults:
    // Run/Debug Configurations -> Edit Configurations -> Defaults -> TestNG (add the two env vars).  All new tests
    // created in IntelliJ after that point will pick up these vars.
    Client buildClient(boolean enableCaching=true) {

        def builder = Clients.builder()
        ((DefaultClientBuilder)builder).setBaseUrl(baseUrl)

        if (!enableCaching) {
            builder.setCacheManager(Caches.newDisabledCacheManager())
        }

        return builder.build()
    }

    Client buildClient(AuthenticationScheme authenticationScheme) {

        def builder = Clients.builder()
        ((DefaultClientBuilder)builder).setBaseUrl(baseUrl)

        builder.setAuthenticationScheme(authenticationScheme)

        return builder.build()
    }

    protected  String uniquify(String s) {
        return s + "-" + UUID.randomUUID().toString().replace('-', '') + "-" + runId
    }

    protected List<Map<String, String>> filterForRun(List<Map<String, String>> properties) {
        List<Map<String, String>> ret = Lists.newArrayList()
        for (Map<String, String> m : properties) {
            if (m.get("name") != null && m.get("name").endsWith(runId)) {
                ret.add(m);
            }
        }
        return ret
    }

    protected List<Resource> filterForRun(CollectionResource resources) {
        List<Resource> ret = Lists.newArrayList()
        for (Resource r : resources.iterator()) {
            // Expected that we are dealing with Directory, Group or Application objects
            String name = (String)r.class.getMethod("getName").invoke(r)
            if (name.endsWith(runId)) {
                ret.add(r)
            }
        }
        return ret
    }

    protected RequestCountingClient buildCountingClient() {

        com.stormpath.sdk.api.ApiKey apiKey = com.stormpath.sdk.api.ApiKeys.builder().build();

        return new RequestCountingClient(apiKey, baseUrl, null, Caches.newCacheManager().build(), AuthenticationScheme.SAUTHC1, 20000);
    }

    //Creates a new Application with an auto-created directory
    protected Application createApplication() {
        Application application = client.instantiate(Application)
        application.setName(uniquify("Java SDK IT App"))
        return client.currentTenant.createApplication(newCreateRequestFor(application).createDirectory().build())
    }

    protected Application createTempApp() {
        def app = createApplication();
        deleteOnTeardown(app.getDefaultAccountStore() as Directory)
        deleteOnTeardown(app)
        return app
    }
}
