/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.organization;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.AccountLinker;
import com.stormpath.sdk.application.AccountStoreHolder;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * An Organization is a top-level container of Directories and Groups. Directories and Groups are guaranteed to
 * be unique within an {@link Organization}, but not across multiple Organizations. A {@code Organization}'s name is guaranteed
 * to be unique across all of a {@link com.stormpath.sdk.tenant.Tenant}'s organizations.
 * <p/>
 * You can think of an Organization as a 'virtual' AccountStore that 'wraps' other AccountStores.  Like other
 * AccountStores, an Organization can be mapped to an Application so that users in the Organization can login to that application.
 *
 * @since 1.0.RC7
 */
public interface Organization extends AccountStoreHolder<Organization>, Resource, Saveable, Deletable, AccountStore, Extendable, Auditable, AccountLinker {

    /**
     * Returns this Organization's name. The name is guaranteed to be non-null and unique in the owning Tenant.
     *
     * @return this Organization's name
     */
    String getName();

    /**
     * Sets the Organization's name. The name is required and must be unique among all other organizations in the owning
     * Tenant.
     *
     * @param name the name to set (must be non-null, non-empty and unique).
     * @return this instance for method chaining.
     */
    Organization setName(String name);


    /**
     * Sets the Organization's nameKey. The nameKey name must be non-null and unique in the owning Tenant.
     *
     * @param nameKey the name key to set (must be non-null, non-empty and unique).
     * @return this instance for method chaining.
     */
    Organization setNameKey(String nameKey);

    /**
     * Returns this Organization's nameKey.  The nameKey must be non-null and unique in the owning Tenant.
     *
     * @return this Organization's nameKey.
     */
    String getNameKey();

    /**
     * Returns the description. This is an optional property and may be null or empty.
     *
     * @return the description. This is an optional property and may be null or empty.
     */
    String getDescription();

    /**
     * Sets the description. This is an optional property and may be null or empty.
     *
     * @param description the description to add.
     * @return this instance for method chaining.
     */
    Organization setDescription(String description);

    /**
     * Returns the Organization's status.
     * <p/>
     * An {@link OrganizationStatus#ENABLED enabled} organization may be used as a 'virtual'
     * AccountStore that 'wraps' other AccountStores.
     * Like other AccountStores, an Organization can be mapped to an Application so that users in the Organization can login to that application.
     * A {@link OrganizationStatus#DISABLED disabled} organization cannot be used to store Directories or Groups.
     *
     * @return the organization's status.
     */
    OrganizationStatus getStatus();

    /**
     * Sets the organization's status.
     * <p/>
     * An {@link OrganizationStatus#ENABLED enabled} organization may be used as a 'virtual'
     * AccountStore that 'wraps' other AccountStores.
     * Like other AccountStores, an Organization can be mapped to an Application so that users in the Organization can login to that application.
     * A {@link OrganizationStatus#DISABLED disabled} organization cannot be used to store Directories or Groups.
     *
     * @param status the status to apply.
     * @return this instance for method chaining.
     */
    Organization setStatus(OrganizationStatus status);

    /**
     * Returns all AccountStoreMappings accessible to the Organization.
     * <p/>
     * Tip: Instead of iterating over all organizationAccountStoreMappings, it might be more convenient (and practical) to execute
     * a search for one or more organizationAccountStoreMappings using the {@link #getAccountStoreMappings()} (java.util.Map)} method
     * or the {@link #getAccountStoreMappings (com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)} instead of this one.
     *
     * @return all OrganizationAccountStoreMapping resources accessible to the organization.
     * @see #getAccountStoreMappings(java.util.Map)
     * @see #getAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)
     */
    OrganizationAccountStoreMappingList getAccountStoreMappings();

    /**
     * Returns a paginated list of the organization's mapped Account stores
     * that also match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../organizations/organizationId/accountStoreMappings?param1=value1&param2=value2&...
     * </pre>
     * <p/>
     * This is a type-unsafe alternative to the
     * {@link #getAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria) getAccountStoreMappings(organizationAccountStoreMappingCriteria)}
     * method, and might be useful when using dynamic languages like Groovy or JRuby.  Users of compiled languages,
     * or those that like IDE-completion, might favor the type-safe method instead.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the organization's mapped account stores that match the specified query criteria.
     * @see #getAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)
     */
    OrganizationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the organization's mapped Account stores that also match the specified query
     * criteria.
     * The {@link com.stormpath.sdk.organization.OrganizationAccountStoreMappings OrganizationAccountStoreMappings} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * organization.getAccountStoreMappings(OrganizationAccountStoreMappings.criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.organization.OrganizationAccountStoreMappings.*;
     *
     * ...
     *
     * organization.getAccountStoreMappings(criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the organization's mapped account stores that match the specified query criteria.
     */
    OrganizationAccountStoreMappingList getAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria);

    /**
     * Creates a new Account that may login to this application.
     *
     * <p>This is mostly a convenience method; it delegates creation to the Application's designated
     * {@link #getDefaultAccountStore() defaultAccountStore}, and functions as follows:
     *
     * <ul>
     * <li>If the {@code defaultAccountStore} is a Directory: the account is created in the Directory and
     * returned.</li>
     * <li>If the {@code defaultAccountStore} is a Group: the account is created in the Group's Directory, assigned to
     * the Group, and then returned.</li>
     * </ul>
     * </p>
     *
     * @param account the account to create/persist
     * @return a new Account that may login to this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultAccountStore()
     *                           defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be
     *                           created.
     */
    Account createAccount(Account account) throws ResourceException;

    /**
     * Creates a new Account that may login to this application according to the request criteria.
     *
     * <p>This is mostly a convenience method; it delegates creation to the Application's designated
     * {@link #getDefaultAccountStore() defaultAccountStore}, and functions as follows:
     * <ul>
     * <li>If the {@code defaultAccountStore} is a Directory: the account is created in the Directory and
     * returned.</li>
     * <li>If the {@code defaultAccountStore} is a Group: the account is created in the Group's Directory, assigned to
     * the Group, and then returned.</li>
     * </ul>
     * </p>
     * <h2>Example</h2>
     * <pre>
     *      organization.createAccount(Accounts.newCreateRequestFor(account).build());
     * </pre>
     *
     * <p>If you would like to force disabling the backing directory's account registration workflow:
     * <pre>
     *      organization.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build());
     * </pre>
     * If you would like to force the execution of the registration workflow, no matter what the backing directory
     * configuration is:
     * <pre>
     *      organization.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
     * </pre>
     * If you would like to retrieve the account's custom data in the response of the account creation.
     * <pre>
     *      organization.createAccount(Accounts.newCreateRequestFor(account).withResponseOptions(Accounts.options().withCustomData()).build());
     * </pre>
     * </p>
     *
     * @param request the account creation request
     * @return a new Account that may login to this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultAccountStore()
     *                           defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be
     *                           created.
     */
    Account createAccount(CreateAccountRequest request) throws ResourceException;

    /**
     * Creates a new {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping} for this Organization, allowing the associated
     * {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the applications related to this Organization.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this convenience method will call the server immediately.
     * <h3>Authentication Process and OrganizationAccountStoreMapping Order</h3>
     * During an authentication attempt, an Organization consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code OrganizationAccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the organization will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you control where the new {@code OrganizationAccountStoreMapping} will reside in the Organization's
     * overall list by setting its (zero-based)
     * {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping#setListIndex(int) listIndex} property before calling this
     * method.
     * <h4>{@code listIndex} values</h4>
     * <ul>
     * <li>negative: attempting to set a negative {@code listIndex} will cause an Error</li>
     * <li>zero: the account store mapping will be the first item in the list (and therefore consulted first
     * during the authentication process).</li>
     * <li>positive: the account store mapping will be inserted at that index.  Because list indices are zero-based,
     * the account store will be in the list at position {@code listIndex - 1}.</li>
     * </ul>
     * Any {@code listIndex} value equal to or greater than the current list size will automatically append the
     * {@code OrganizationAccountStoreMapping} at the end of the list.
     * <h4>Example</h4>
     * Setting a new {@code OrganizationAccountStoreMapping}'s {@code listIndex} to {@code 500} and then adding the mapping to
     * an organization with an existing 3-item list will automatically save the {@code OrganizationAccountStoreMapping} at the end
     * of the list and set its {@code listIndex} value to {@code 3} (items at index 0, 1, 2 were the original items,
     * the new fourth item will be at index 3).
     * <pre>
     * AccountStore directoryOrGroup = getDirectoryOrGroupYouWantToUseForLogin();
     * OrganizationAccountStoreMapping mapping = client.instantiate(OrganizationAccountStoreMapping.class);
     * mapping.setAccountStore(directoryOrGroup);
     * mapping.setListIndex(3); //this is zero-based, so index 3 == 4th item
     * mapping = organization.createAccountStoreMapping(mapping);
     * </pre>
     * Then, when {@link com.stormpath.sdk.application.Application#authenticateAccount(com.stormpath.sdk.authc.AuthenticationRequest) authenticating} an
     * account, this AccountStore (directory or group) will be consulted if no others before it in the list
     * found a matching account.
     *
     * @param mapping the new OrganizationAccountStoreMapping resource to add to the Organization's OrganizationAccountStoreMapping list.
     * @return the newly created OrganizationAccountStoreMapping instance.
     * @throws com.stormpath.sdk.resource.ResourceException
     * @since 1.0.RC9
     */
    OrganizationAccountStoreMapping createAccountStoreMapping(OrganizationAccountStoreMapping mapping) throws ResourceException;

    /**
     * Creates a new {@link OrganizationAccountStoreMapping} for this Organization and appends that
     * OrganizationAccountStoreMapping to the end of the Organization's OrganizationAccountStoreMapping list, allowing the association
     * {@link OrganizationAccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the Organization.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this convenience method will call the server immediately.
     * <h3>Authentication Process and OrganizationAccountStoreMapping Order</h3>
     * During an authentication attempt, an organization consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code OrganizationAccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the organization     will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you are setting the new {@code OrganizationAccountStoreMapping} to the end of the Organization's
     * overall list.
     * <p/>
     * NOTE: If you already know the account store where the account resides, you can
     * specify it at the time the authentication request is created (for example,
     * {@link com.stormpath.sdk.authc.UsernamePasswordRequestBuilder#inAccountStore(AccountStore)}).
     * This way you will be avoiding the authentication attempt to cycle through the Organization's account stores.
     * <p/>
     * <h4>Example</h4>
     * <pre>
     * AccountStore directoryOrGroup = getDirectoryOrGroupYouWantToUseForLogin();
     * OrganizationAccountStoreMapping mapping = application.addAccountStore(directoryOrGroup);
     * </pre>
     * Then, when {@link com.stormpath.sdk.application.Application#authenticateAccount(com.stormpath.sdk.authc.AuthenticationRequest) authenticating} an
     * account, this AccountStore (directory or group) will be consulted if no others before it in the list
     * found a matching account.
     *
     * @param accountStore the new AccountStore resource to add to the Organization's OrganizationAccountStoreMapping list.
     * @return the newly created OrganizationAccountStoreMapping instance.
     * @throws ResourceException
     */
    OrganizationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException;

    /**
     * Convenience method to add a a new {@link AccountStore} to this organization.
     * <p/>
     * The given String can be either an 'href' or a 'name' of a {@link Directory} or a {@link Group} belonging to the current Tenant.
     * <p/>
     * If the provided value is an 'href', this method will get the proper Resource and add it as a new AccountStore in this
     * Organization without much effort. However, if the provided value is not an 'href', it will be considered as a 'name'. In this case,
     * this method will search for both a Directory and a Group whose names equal the provided <code>hrefOrName</code>. If only
     * one resource exists (either a Directory or a Group), then it will be added as a new AccountStore in this Organization. However,
     * if there are two resources (a Directory and a Group) matching that name, a {@link com.stormpath.sdk.resource.ResourceException ResourceException}
     * will be thrown.
     * <p/>
     * At the end of this process, if a single matching resource is found, this method will then delegate the actual {@link OrganizationAccountStoreMapping}
     * creation to the {@link #addAccountStore(AccountStore)} method in order to fulfill its task.
     * </p>
     * Example providing an href:
     * <p/>
     * <pre>
     *      OrganizationAccountStoreMapping accountStoreMapping = organization.addAccountStore("https://api.stormpath.com/v1/groups/2rwq022yMt4u2DwKLfzriP");
     * </pre>
     * Example providing a name:
     * <p/>
     * <pre>
     *      OrganizationAccountStoreMapping accountStoreMapping = organization.addAccountStore("Foo Name");
     * </pre>
     * <b>USAGE NOTE 1:</b> When using 'names' this method is not efficient as it will search for both Directories and Groups within this Tenant
     * for a matching name. In order to do so, some looping takes place at the client side: groups exist within directories, therefore we need
     * to loop through every existing directory in order to find the required Group. In contrast, providing the Group's 'href' is much more
     * efficient as no actual search operation needs to be carried out.
     * <p/>
     * <b>USAGE NOTE 2:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method and will call the server immediately.
     *
     * @param hrefOrName either the 'href' or the 'name' of the desired Directory or Group.
     * @return the {@link OrganizationAccountStoreMapping} created after finding the actual resource described by <code>hrefOrName</code>. It returns
     * <code>null</code> if there is no group or directory matching the href or name given.
     * @throws ResourceException if the resource already exists as an account store in this organization.
     * @throws IllegalArgumentException if the given hrefOrName matches more than one resource in the current Tenant.
     * @see #addAccountStore(AccountStore)
     *
     */
    OrganizationAccountStoreMapping addAccountStore(String hrefOrName);

    /**
     * Returns the organization's parent (owning) Tenant.
     *
     * @return the organization's parent (owning) Tenant.
     */
    Tenant getTenant();

    /**
     * Creates a new {@link Group group} that may be used by this organization in the organization's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method.  It merely delegates to the Organization's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     *
     * @param group the Group to create/persist
     * @return a new Group that may be used by this organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupStore} or if the designated {@code defaultGroupStore} does not allow new
     *                           groups to be created.
     */
    Group createGroup(Group group) throws ResourceException;

    /**
     * Creates a new {@link Group group} that may be used by this organization in the organization's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method. It merely delegates to the Organization's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     * <h2>Example</h2>
     * <pre>
     *      organization.createGroup(Groups.newCreateRequestFor(group).build());
     * </pre>
     * <p/>
     * If you would like to retrieve the group's custom data in the response of the groups creation.
     * <pre>
     *      organization.createGroup(Groups.newCreateRequestFor(group).withResponseOptions(Groups.options().withCustomData()).build());
     * </pre>
     *
     * @param request the group creation request
     * @return a new Group that may be used by this organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupsStore} or if the designated {@code defaultGroupsStore} does not allow new
     *                           groups to be created.
     */
    Group createGroup(CreateGroupRequest request);
}
