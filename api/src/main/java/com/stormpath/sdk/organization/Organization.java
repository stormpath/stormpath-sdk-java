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
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.ResourceException;
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
 * @since 1.0.RC4.6
 */
public interface Organization extends Resource, Saveable, Deletable, AccountStore, Extendable, Auditable {

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
     * Returns a paginated list of all groups in the Organization.
     * <p/>
     * Tip: Instead of iterating over all groups, it might be more convenient (and practical) to execute a search
     * for one or more groups using the {@link #getGroups(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all groups in the organization.
     * @see #getGroups(java.util.Map)
     */
    GroupList getGroups();

    /**
     * Returns a paginated list of the organization's groups that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../organizations/organizationId/groups?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the organization's groups that match the specified query criteria.
     */
    GroupList getGroups(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the organization's groups that match the specified query criteria.  The
     * {@link com.stormpath.sdk.group.Groups Groups} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * organization.getGroups(Groups.where(
     *     Groups.name().containsIgnoreCase("foo"))
     *     .and(Groups.description().startsWithIgnoreCase("bar"))
     *     .orderByName()
     *     .orderByDescription().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     * import static com.stormpath.sdk.group.Groups.*;
     *
     * ...
     *
     * organization.getGroups(where(
     *     name().containsIgnoreCase("foo"))
     *     .and(description().startsWithIgnoreCase("bar"))
     *     .orderByName()
     *     .orderByDescription().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the organization's groups that match the specified query criteria.
     */
    GroupList getGroups(GroupCriteria criteria);

    /**
     * Returns a paginated list of all directories in the Organization.
     * <p/>
     * Tip: Instead of iterating over all directories, it might be more convenient (and practical) to execute a search
     * for one or more directories using the {@link #getDirectories()} (java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all directories in the organization.
     * @see #getDirectories() (java.util.Map)
     * @see #getDirectories(DirectoryCriteria)
     */
    DirectoryList getDirectories();

    /**
     * Returns a paginated list of the organization's directories that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../organizations/organizationId/directories?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the organization's directories that match the specified query criteria.
     */
    DirectoryList getDirectories(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the organization's directories that match the specified query criteria.  The
     * {@link com.stormpath.sdk.directory.Directories Directories} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * organization.getDirectories(Directories.where(
     *     Directories.name().containsIgnoreCase("foo"))
     *     .and(Directories.description().startsWithIgnoreCase("bar"))
     *     .orderByName()
     *     .orderByDescription().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     * import static com.stormpath.sdk.directory.Directories.*;
     *
     * ...
     *
     * organization.getDirectories(where(
     *     name().containsIgnoreCase("foo"))
     *     .and(description().startsWithIgnoreCase("bar"))
     *     .orderByName()
     *     .orderByDescription().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the Organization's directories that match the specified query criteria.
     */
    DirectoryList getDirectories(DirectoryCriteria criteria);

    /**
     * Returns all AccountStoreMappings accessible to the Organization.
     * <p/>
     * Tip: Instead of iterating over all organizationAccountStoreMappings, it might be more convenient (and practical) to execute
     * a search for one or more organizationAccountStoreMappings using the {@link #getOrganizationAccountStoreMappings()} (java.util.Map)} method
     * or the {@link #getOrganizationAccountStoreMappings (com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)} instead of this one.
     *
     * @return all OrganizationAccountStoreMapping resources accessible to the organization.
     * @see #getOrganizationAccountStoreMappings(java.util.Map)
     * @see #getOrganizationAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)
     */
    OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings();

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
     * {@link #getOrganizationAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria) getOrganizationAccountStoreMappings(organizationAccountStoreMappingCriteria)}
     * method, and might be useful when using dynamic languages like Groovy or JRuby.  Users of compiled languages,
     * or those that like IDE-completion, might favor the type-safe method instead.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the organization's mapped account stores that match the specified query criteria.
     * @see #getOrganizationAccountStoreMappings(com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria)
     */
    OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the organization's mapped Account stores that also match the specified query
     * criteria.
     * The {@link com.stormpath.sdk.organization.OrganizationAccountStoreMappings OrganizationAccountStoreMappings} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * organization.getOrganizationAccountStoreMappings(OrganizationAccountStoreMappings.criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.organization.OrganizationAccountStoreMappings.*;
     *
     * ...
     *
     * organization.getOrganizationAccountStoreMappings(criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the organization's mapped account stores that match the specified query criteria.
     */
    OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria);

    /**
     * Returns the {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or a
     * {@link com.stormpath.sdk.directory.Directory Directory}) used to persist
     * new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Organization}, or
     * {@code null} if no accountStore has been designated.
     * <p/>
     * Because an Organization is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Organization delegates
     * new account persistence.
     * <h3>Directory or Group?</h3>
     * As both Directory and Organization are sub-interfaces of {@link com.stormpath.sdk.directory.AccountStore}, you can determine which of the two
     * is returned by using the <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a>.  For
     * example:
     * <p/>
     * <pre>
     * AccountStore accountStore = application.getDefaultAccountStore();
     * accountStore.accept(new {@link com.stormpath.sdk.directory.AccountStoreVisitor AccountStoreVisitor}() {
     *
     *     public void visit(Directory directory) {
     *         //the accountStore is a Directory
     *     }
     *
     *     public void visit(Group group) {
     *         //the accountStore is a Group;
     *     }
     * };
     * </pre>
     * <h3>Setting the 'New Account Store'</h3>
     * You may set the defaultAccountStore by acquiring one of the Organization's
     * {@link #getOrganizationAccountStoreMappings() accountStoreMappings} and calling
     * {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping#setDefaultAccountStore(boolean) setDefaultAccountStore}<code>(true)</code> or by
     * calling {@link #setDefaultAccountStore(com.stormpath.sdk.directory.AccountStore)}
     *
     * @return the {@link com.stormpath.sdk.directory.AccountStore} (which will be either a Group or Directory) used to persist
     *         new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Organization}, or
     *         {@code null} if no accountStore has been designated.
     */
    AccountStore getDefaultAccountStore();

    /**
     * Sets the {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or a
     * {@link com.stormpath.sdk.directory.Directory Directory}) used to persist
     * new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Organization}.
     * <p/>
     * Because an Organization is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating accounts; this method sets the AccountStore to which the Organization delegates
     * new account persistence.
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method
     * to be called to persist changes, this convenience method will call the server immediately.
     * </p>
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *                     new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the
     *                     Organization}
     */
    void setDefaultAccountStore(AccountStore accountStore);

    /**
     * Returns the {@link AccountStore} used to persist
     * new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Organization}, or
     * {@code null} if no accountStore has been designated.
     *
     * <p/>
     * Because an Organization is not an {@code AccountStore} itself, it delegates to a Directory when creating groups;
     * this method returns the AccountStore to which the Organization delegates new group persistence.
     * <h3>Directory or Group?</h3>
     * As both Group and Directory are sub-interfaces of {@link AccountStore}, you can determine which of the two
     * is returned by using the <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a>.  For
     * example:
     * <p/>
     * <pre>
     * AccountStore groupStore = application.getDefaultGroupStore();
     * groupStore.accept(new {@link com.stormpath.sdk.directory.AccountStoreVisitor AccountStoreVisitor}() {
     *
     *     public void visit(Directory directory) {
     *         //groupStore is a Directory
     *     }
     *
     *     public void visit(Group group) {
     *         //groupStore is a Group;
     *     }
     * };
     * </pre>
     * In practice, Stormpath's current REST API requires this to be a Directory.  However, this could be
     * a Group in the future, so do not assume it will always be a Directory if you want your code to be
     * forward compatible; use the Visitor pattern and do not cast directly to a Directory.
     * <h3>Setting the 'New Group Store'</h3>
     * You set the newGroupStore by acquiring one of the Organization's
     * {@link #getOrganizationAccountStoreMappings() accountStoreMappings} and calling
     * {@link com.stormpath.sdk.organization.OrganizationAccountStoreMapping#setDefaultGroupStore(boolean) setDefaultGroupStore}<code>(true)</code> or by
     * calling {@link #setDefaultGroupStore(com.stormpath.sdk.directory.AccountStore)}.
     *
     * @return the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *         new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Organization}, or
     *         {@code null} if no accountStore has been designated.
     */
    AccountStore getDefaultGroupStore();

    /**
     * Sets the {@link AccountStore} (a {@link com.stormpath.sdk.directory.Directory Directory}) that will be used to
     * persist new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Organization}.
     * <b>Stormpath's current REST API requires this to be a Directory. However, this could be a Group in the future, so do not assume it is always a
     * Directory if you want your code to function correctly if/when this support is added.</b>
     * <p/>
     * Because an Organization is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating groups; this method sets the AccountStore to which the Organization delegates
     * new group persistence.
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method
     * to be called to persist changes, this convenience method will call the server immediately.
     * </p>
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group or Organization) used to persist
     *                     new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Organization}
     */
    void setDefaultGroupStore(AccountStore accountStore);

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
     */
    OrganizationAccountStoreMapping createOrganizationAccountStoreMapping(OrganizationAccountStoreMapping mapping) throws ResourceException;

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
     * {@link com.stormpath.sdk.authc.UsernamePasswordRequest#UsernamePasswordRequest(String, char[],
     * com.stormpath.sdk.directory.AccountStore)}).
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
     * Creates a new Group that may be used by this organization in the organization's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method.  It merely delegates to the Organization's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     *
     * @param group the Group to create/persist
     * @return a new Group that may be used by this Organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupStore} or if the designated {@code defaultGroupStore} does not allow new
     *                           groups to be created.
     */
    Group createGroup(Group group) throws ResourceException;

    /**
     * Creates a new Group that may be used by this organization in the organization's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method. It merely delegates to the Organization's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     * <h2>Example</h2>
     * <pre>
     * organization.createGroup(Groups.newCreateRequestFor(group).build());
     * </pre>
     * <p/>
     * If you would like to retrieve the group's custom data in the response of the groups creation.
     * <pre>
     * organization.createGroup(Groups.newCreateRequestFor(group).withResponseOptions(Groups.options().withCustomData()).build());
     * </pre>
     *
     * @param request the group creation request
     * @return a new Group that may be used by this organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupsStore} or if the designated {@code defaultGroupsStore} does not allow new
     *                           groups to be created.
     */
    Group createGroup(CreateGroupRequest request);

    /**
     * Returns the organization's parent (owning) Tenant.
     *
     * @return the organization's parent (owning) Tenant.
     */
    Tenant getTenant();

    /**
     * Returns a paginated list of all accounts in the organization's Directories and Groups that may login to applications associated to it.
     * <p/>
     * Tip: Instead of iterating over all accounts, it might be more convenient (and practical) to execute a search
     * for one or more accounts using the {@link #getAccounts(com.stormpath.sdk.account.AccountCriteria)} or
     * {@link #getAccounts(java.util.Map)} methods instead of this one.
     *
     * @return a paginated list of all accounts in organization's Directories and Groups.
     * @see #getAccounts(com.stormpath.sdk.account.AccountCriteria)
     * @see #getAccounts(java.util.Map)
     */
    AccountList getAccounts();

    /**
     * Returns a paginated list of all accounts in the organization's Directories and Groups that may login to applications associated to it and also match the specified query
     * criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../organizations/applicationId/accounts?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the organization's accounts that match the specified query criteria.
     */
    AccountList getAccounts(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of all accounts in the organization's Directories and Groups that may login to applications associated to it and also match the specified query
     * criteria.
     * The {@link com.stormpath.sdk.account.Accounts Accounts} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * organization.getAccounts(Accounts
     *     .where(Accounts.surname().containsIgnoreCase("Smith"))
     *     .and(Accounts.givenName().eqIgnoreCase("John"))
     *     .orderBySurname().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.account.Accounts.*;
     *
     * ...
     *
     * organization.getAccounts(where(
     *     surname().containsIgnoreCase("Smith"))
     *     .and(givenName().eqIgnoreCase("John"))
     *     .orderBySurname().descending()
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the organization's accounts that match the specified query criteria.
     */
    AccountList getAccounts(AccountCriteria criteria);

    /**
     * Creates a new Account that may login to applications related to this organization.
     *
     * <p>This is mostly a convenience method; it delegates creation to the Organization's designated
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
     * @return a new Account that may login to applications related to this organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultAccountStore()
     *                           defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be
     *                           created.
     */
    Account createAccount(Account account) throws ResourceException;

    /**
     * Creates a new Account that may login to applications related to this organization according to the request criteria.
     *
     * <p>This is mostly a convenience method; it delegates creation to the Organization's designated
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
     * organization.createAccount(Accounts.newCreateRequestFor(account).build());
     * </pre>
     *
     * <p>If you would like to force disabling the backing directory's account registration workflow:
     * <pre>
     * organization.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build());
     * </pre>
     * If you would like to force the execution of the registration workflow, no matter what the backing directory
     * configuration is:
     * <pre>
     * organization.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
     * </pre>
     * If you would like to retrieve the account's custom data in the response of the account creation.
     * <pre>
     * organization.createAccount(Accounts.newCreateRequestFor(account).withResponseOptions(Accounts.options().withCustomData()).build());
     * </pre>
     * </p>
     *
     * @param request the account creation request
     * @return a new Account that may login to applications related to this organization.
     * @throws ResourceException if the Organization does not have a designated {@link #getDefaultAccountStore()
     *                           defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be
     *                           created.
     */
    Account createAccount(CreateAccountRequest request) throws ResourceException;
}