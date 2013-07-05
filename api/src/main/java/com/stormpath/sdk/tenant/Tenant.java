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
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;

import java.util.Map;

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
     * tenant.createApplication(CreateApplica8tionRequest.with(application).build());
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
     * <p/>
     * Tip: Instead of iterating over all applications, it might be more convenient (and practical) to execute a search
     * for one or more directories using the {@link #getApplications(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all of the Tenant's {@link Application} resources.
     * @see #getApplications(java.util.Map)
     */
    ApplicationList getApplications();

    /**
     * Returns a paginated list of the Tenant's applications that match the specified query criteria.  The
     * {@link com.stormpath.sdk.application.Applications Applications} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * tenant.list(Applications
     *     .where(Applications.DESCRIPTION.icontains("foo"))
     *     .and(Applications.NAME.iStartsWith("bar"))
     *     .orderByName().descending()
     *     .expandAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the  the query parameters to use when performing a request to the collection.
     * @return a paginated list of the Tenant's applications that match the specified query criteria.
     * @since 0.8
     */
    ApplicationList list(ApplicationCriteria criteria);

    /**
     * Returns a paginated list of the Tenant's applications that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../tenants/tenantId/applications?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the Tenant's applications that match the specified query criteria.
     * @since 0.8
     */
    ApplicationList getApplications(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of all of the Tenant's {@link com.stormpath.sdk.directory.Directory Directory} instances.
     * <p/>
     * Tip: Instead of iterating over all directories, it might be more convenient (and practical) to execute a search
     * for one or more directories using the {@link #getDirectories(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all of the Tenant's {@link com.stormpath.sdk.directory.Directory Directory} instances.
     * @see #getDirectories(java.util.Map)
     */
    DirectoryList getDirectories();

    /**
     * Returns a paginated list of the Tenant's directories that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../tenants/tenantId/directories?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the Tenant's directories that match the specified query criteria.
     * @since 0.8
     */
    DirectoryList getDirectories(Map<String, Object> queryParams);

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
