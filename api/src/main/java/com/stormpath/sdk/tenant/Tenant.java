/*
 * Copyright 2014 Stormpath, Inc.
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
import com.stormpath.sdk.directory.CreateDirectoryRequest;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
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
     * <b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
     *
     * @return the tenant's Stormpath globally-unique name. THIS CAN CHANGE. Do not rely on it as a permanent
     *         identifier.
     */
    String getName();

    /**
     * Returns the tenant's globally-unique human-readable key in Stormpath.
     * <p/>
     * <b>THIS CAN CHANGE IN THE FUTURE.  Do not rely on it as a permanent identifier.</b>  If you need a permanent ID,
     * use the {@link #getHref() href} as the permanent ID (this is true for all resources, not just Tenant resources).
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
     * @see #createApplication(com.stormpath.sdk.application.CreateApplicationRequest)
     */
    Application createApplication(Application application) throws ResourceException;

    /**
     * Creates a new Application resource in the Tenant based on the specified Create{@code CreateApplicationRequest}.
     * <h3>Usage</h3>
     * <pre>
     * tenant.createApplication(Applications.newCreateRequestFor(application).build());
     * </pre>
     * <p/>
     * If you would like to automatically create a Directory for this application's own needs:
     * <pre>
     * tenant.createApplication(Applications.newCreateRequestFor(application).createDirectory().build());
     * </pre>
     * The directory's name will be auto-generated to reflect your Application as closely as possible and not conflict
     * with any existing Directories in your tenant.
     * <p/>
     * Or if you prefer to specify the directory name yourself:
     * <pre>
     * tenant.createApplication(Applications.newCreateRequestFor(application).createDirectoryNamed("My Directory").build());
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
     * Returns a paginated list of the Tenant's applications that match the specified query criteria.  The
     * {@link com.stormpath.sdk.application.Applications Applications} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * tenant.getApplications(Applications
     *     .where(Applications.description().containsIgnoreCase("foo"))
     *     .and(Applications.name().startsWithIgnoreCase("bar"))
     *     .orderByName().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the  the query parameters to use when performing a request to the collection.
     * @return a paginated list of the Tenant's applications that match the specified query criteria.
     * @since 0.8
     */
    ApplicationList getApplications(ApplicationCriteria criteria);

    /**
     * Creates a new <b>Cloud</b> Directory resource in the Tenant.
     * <p/>
     * <b>Mirrored (LDAP or Active Directory) Directories cannot be created via the REST API or SDKs.</b>
     * <p/>
     * This method creates a natively hosted directory in Stormpath.  Please use the Stormpath Admin UI if you need to
     * create an LDAP/AD mirrored directory.
     *
     * @param directory the Directory resource to create.
     * @return the directory created.
     * @throws ResourceException if there was a problem creating the directory.
     * @since 0.9.0
     */
    Directory createDirectory(Directory directory);

    /**
     * Creates a new <b>Provider-based</b> Directory resource in the Tenant based on the specified {@code CreateDirectoryRequest}.
     * <h3>Usage</h3>
     * <pre>
     * Directory directory = client.instantiate(Directory.class);
     * directory.setName("My Facebook Directory");
     * ...
     * CreateDirectoryRequest request = Directories.newCreateRequestFor(directory)
     *      .forProvider(
     *          <b>Providers.FACEBOOK.builder()</b>
     *                  .setClientId("624508218317020")
     *                  .setClientSecret("d0ad961d45fgc0210c0c7d67e8f1w800")
     *                  .build()
     *      ).build();
     * directory = tenant.createDirectory(request);
     * </pre>
     * But note - if the specified directory name is already in use, a Resource Exception will be thrown to let you
     * know you must choose another Directory name.
     *
     * @param createDirectoryRequest the request reflecting how to create the Directory
     * @return the directory created.
     * @throws ResourceException if there was a problem creating the directory.
     * @since 1.0.beta
     */
    Directory createDirectory(CreateDirectoryRequest createDirectoryRequest);

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
     * Returns a paginated list of the Tenant's directories that match the specified query criteria.  The
     * {@link com.stormpath.sdk.directory.Directories Directories} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * tenant.getDirectories(Directories
     *     .where(Directories.description().containsIgnoreText("foo"))
     *     .and(Directories.name().startsWithIgnoreCase("bar"))
     *     .orderByName().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the  the query parameters to use when performing a request to the collection.
     * @return a paginated list of the Tenant's applications that match the specified query criteria.
     * @since 0.8
     */
    DirectoryList getDirectories(DirectoryCriteria criteria);

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
