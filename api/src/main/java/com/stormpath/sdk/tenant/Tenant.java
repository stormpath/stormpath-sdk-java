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
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;

/**
 * @since 0.1
 */
public interface Tenant extends Resource, Saveable {

    /**
     * Returns the tenant's globally-unique name in Stormpath.
     * <p/>
     * <b>THIS CAN CHANGE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID, use the
     * {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     *
     * @return the tenant's Stormpath globally-unique name. THIS CAN CHANGE. Do not rely on it as a permanent
     *         identifier.
     */
    String getName();

    /**
     * Returns the tenant's globally-unique human-readable key in Stormpath.
     * <p/>
     * <b>THIS CAN CHANGE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID, use the
     * {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     *
     * @return the tenant's Stormpath globally-unique human-readable name key. THIS CAN CHANGE. Do not rely on it as a
     *         permanent identifier.
     */
    String getKey();

    /**
     * Creates a new Application resource in the Tenant.
     *
     * @param application the Application resource to create.
     * @return the created Application
     * @throws ResourceException if there was a problem creating the application.
     */
    Application createApplication(Application application) throws ResourceException;

    /**
     * Creates a new Application resource in the Tenant based on the specified {@code CreateApplicationRequest}.
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
     * tenant.createApplication(CreateApplicationRequest.with(application).withDirectoryName("My Directory").build());
     * </pre>
     * But note - if the specified directory name is already in use, a Resource Exception will be thrown to let you
     * know you must choose another Directory name.
     *
     * @param request the request reflecting how to create the Application
     * @return the application created.
     * @throws ResourceException if there was a problem creating the application.
     * @since 0.8
     */
    Application createApplication(CreateApplicationRequest request) throws ResourceException;

    /**
     * Returns a paginated list of all of the Tenant's {@link Application} resources.
     *
     * @return a paginated list of all of the Tenant's {@link Application} resources.
     */
    ApplicationList getApplications();

    /**
     * Returns a paginated list of all of the Tenant's {@link com.stormpath.sdk.directory.Directory Directory} instances.
     *
     * @return a paginated list of all of the Tenant's {@link com.stormpath.sdk.directory.Directory Directory} instances.
     */
    DirectoryList getDirectories();

    /**
     * Verifies an account's email address based on a {@code sptoken} query parameter embedded in a clickable URL
     * found in an account verification email.  For example:
     * <pre>
     * https://my.company.com/email/verify?<b>sptoken=ExAmPleEmAilVeRiFiCaTiOnTokEnHeRE</b>
     * </pre>
     * Based on this URL, the following should be invoked:
     * <pre>
     * tenant.verifyAccountEmail(&quot;<b>ExAmPleEmAilVeRiFiCaTiOnTokEnHeRE</b>&quot;);
     * </pre>
     * <p/>
     * If the token is valid, the associated account will be validated (changing the account's status from
     * {@code UNVERIFIED} to {@code ENABLED}) and returned.
     *
     * @param token the clickable URL's {@code sptoken} query parameter value
     * @since 0.4
     */
    Account verifyAccountEmail(String token);
}
