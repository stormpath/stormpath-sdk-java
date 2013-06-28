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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * @since 0.1
 */
public interface Tenant extends Resource, Saveable {

    String getName();

    String getKey();

    void createApplication(Application application);

    /**
     * Creates a new Application instance based on the specified request.
     * <h3>Usage</h3>
     * <pre>
     * tenant.createApplication(CreateApplicationRequest.with(application).build());
     * </pre>
     * <p/>
     * If you would like to automatically create a Directory for this application's own needs:
     * <pre>
     * tenant.createApplication(CreateApplicationRequest.with(application).createDirectory(true).build());
     * </pre>
     * The directory's name will be auto-generated to reflect your Application as closely as possible and not conflict
     * with any existing Directories in your tenant.
     * <p/>
     * Or if you prefer to specify the directory name yourself:
     * <pre>
     * tenant.createApplication(CreateApplicationRequest.with(application).withDirectoryName("My Directory");
     * </pre>
     * But note - if the specified directory name is already in use, a Resource Exception will be thrown to let you
     * know you must choose another Directory name.
     *
     * @param request the request reflecting how to create the Application
     * @return the application created.
     */
    Application createApplication(CreateApplicationRequest request);

    ApplicationList getApplications();

    DirectoryList getDirectories();

    /**
     * @since 0.4
     */
    Account verifyAccountEmail(String token);
}
