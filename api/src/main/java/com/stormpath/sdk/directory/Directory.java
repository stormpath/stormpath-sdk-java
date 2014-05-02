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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * A Directory is a top-level container of {@link Account}s and {@link Group}s.  Accounts and Groups are guaranteed to
 * be unique within a {@link Directory}, but not across multiple Directories.  A {@code Directory}'s name is guaranteed
 * to be unique across all of a {@link Tenant}'s directories.
 * <p/>
 * You can think of a Directory as an account 'store'.  You can map one or more Directories (or Groups within a
 * Directory) to an {@link com.stormpath.sdk.application.Application Application}.  This forms the Application's
 * effective 'user base' of all Accounts that may use the Application.
 *
 * @since 0.2
 */
public interface Directory extends Resource, Saveable, Deletable, AccountStore {

    /**
     * Returns this Directory's name.  The name is guaranteed to be non-null and unique among all other Directories in
     * the owning Tenant.
     *
     * @return this Directory's name
     */
    String getName();

    /**
     * Sets the directory's name.  The name is required and must be unique among all other directories in the owning
     * Tenant.
     *
     * @param name the name to set (must be non-null, non-empty and unique).
     */
    void setName(String name);

    /**
     * Returns the description.  This is an optional property and may be null or empty.
     *
     * @return the description.  This is an optional property and may be null or empty.
     */
    String getDescription();

    /**
     * Sets the description.  This is an optional property and may be null or empty.
     *
     * @param description the description to apply.
     */
    void setDescription(String description);

    /**
     * Returns the directory's status.
     * <p/>
     * An {@link DirectoryStatus#ENABLED enabled} directory may be used by applications to login accounts found within
     * the directory.  A {@link DirectoryStatus#DISABLED disabled} directory prevents its accounts from being used to
     * login to applications.
     *
     * @return the directory's status.
     */
    DirectoryStatus getStatus();

    /**
     * Sets the directory's status.
     * <p/>
     * An {@link DirectoryStatus#ENABLED enabled} directory may be used by applications to login accounts found within
     * the directory.  A {@link DirectoryStatus#DISABLED disabled} directory prevents its accounts from being used to
     * login to applications.
     *
     * @param status the status to apply.
     */
    void setStatus(DirectoryStatus status);

    /**
     * Creates a new account instance in the directory using the Directory's default registration workflow setting.
     * Whether a registration workflow is triggered or not for the account is based on the Directory's default setting.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getAccounts() account collection}.
     *
     * @param account the account instance to create in the directory.
     * @see #createAccount(com.stormpath.sdk.account.Account, boolean)
     */
    void createAccount(Account account);

    /**
     * Creates a new account instance in the directory with an explicit registration workflow directive.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code true}, the account registration workflow will be triggered
     * no matter what the Directory configuration is.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code false}, the account registration workflow will <b>NOT</b>
     * be triggered, no matter what the Directory configuration is.
     * <p/>
     * If you want to ensure the registration workflow behavior matches the Directory default, call the
     * {@link #createAccount(com.stormpath.sdk.account.Account)} method instead.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getAccounts() account collection} using the specified {@code registrationWorkflowEnabled} argument.
     *
     * @param account                     account the account instance to create in the directory.
     * @param registrationWorkflowEnabled whether or not the account registration workflow will be triggered, no matter
     *                                    what the Directory configuration is.
     */
    void createAccount(Account account, boolean registrationWorkflowEnabled);

    /**
     * Creates a new account instance in the directory with options to override the registration workflow directive
     * and retrieve account's references in the creation response.
     * <p/>
     * <h2>Example</h2>
     * <pre>
     * directory.createAccount(Accounts.newCreateRequestFor(account).build());
     * </pre>
     * <p/>
     * If you would like to force disabling the backing directory's account registration workflow:
     * <pre>
     * directory.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build());
     * </pre>
     * If you would like to force the execution of the registration workflow, no matter what the backing directory
     * configuration is:
     * <pre>
     * directory.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
     * </pre>
     * If you would like to retrieve the account's custom data in the response of the account creation.
     * <pre>
     * directory.createAccount(Accounts.newCreateRequestFor(account).withResponseOptions(Accounts.options().withCustomData()).build());
     * </pre>
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getAccounts() account collection} using the specified {@code registrationWorkflowEnabled} argument.
     *
     * @param request
     * @since 0.9
     */
    void createAccount(CreateAccountRequest request);

    /**
     * Returns a paginated list of all accounts in the Directory.
     * <p/>
     * Tip: Instead of iterating over all accounts, it might be more convenient (and practical) to execute a search
     * for one or more accounts using the {@link #getAccounts(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all accounts in the Directory.
     * @see #getAccounts(java.util.Map)
     */
    AccountList getAccounts();

    /**
     * Returns a paginated list of the directory's accounts that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../directories/directoryId/accounts?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the directory's accounts that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the directory's accounts that match the specified query criteria.  The
     * {@link com.stormpath.sdk.account.Accounts Accounts} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * directory.getAccounts(Accounts.where(
     *     Accounts.username().containsIgnoreCase("foo"))
     *     .and(Accounts.surname().startsWithIgnoreCase("bar"))
     *     .orderBySurname().descending()
     *     .orderByGivenName().ascending()
     *     .withGroups(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     * import static com.stormpath.sdk.account.Accounts.*;
     *
     * ...
     *
     * directory.getAccounts(where(
     *     username().containsIgnoreCase("foo"))
     *     .and(surname().startsWithIgnoreCase("bar"))
     *     .orderBySurname().descending()
     *     .orderByGivenName().ascending()
     *     .withGroups(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the directory's accounts that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(AccountCriteria criteria);

    /**
     * Returns a paginated list of all groups in the Directory.
     * <p/>
     * Tip: Instead of iterating over all groups, it might be more convenient (and practical) to execute a search
     * for one or more groups using the {@link #getGroups(java.util.Map)} method instead of this one.
     *
     * @return a paginated list of all groups in the directory.
     * @see #getGroups(java.util.Map)
     */
    GroupList getGroups();

    /**
     * Returns a paginated list of the directory's groups that match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../directories/directoryId/groups?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the directory's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList getGroups(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the directory's groups that match the specified query criteria.  The
     * {@link com.stormpath.sdk.group.Groups Groups} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * directory.getGroups(Groups.where(
     *     Groups.name().containsIgnoreCase("foo"))
     *     .and(Groups.description().startsWithIgnoreCase("bar"))
     *     .orderByName()
     *     .orderByDescription().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     * import static com.stormpath.sdk.account.Accounts.*;
     *
     * ...
     *
     * directory.getGroups(where(
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
     * @return a paginated list of the account's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList getGroups(GroupCriteria criteria);

    /**
     * Returns the Tenant to which this Directory belongs.
     *
     * @return the Tenant to which this Directory belongs.
     */
    Tenant getTenant();

    /**
     * Creates a new group instance in the directory.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getGroups() group collection}.
     *
     * @param group the group instance to create in the directory.
     * @since 0.6
     */
    void createGroup(Group group);

    /**
     * Creates a new group instance in the directory, with the option to retrieve groups references in the group
     * creation response.
     * <h2>Example</h2>
     * <pre>
     * directory.createGroup(Groups.newCreateRequestFor(group).build());
     * </pre>
     * <p/>
     * If you would like to retrieve the group's custom data in the response of the groups creation.
     * <pre>
     * directory.createGroup(Groups.newCreateRequestFor(group).withResponseOptions(Groups.options().withCustomData()).build());
     * </pre>
     *
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getGroups() group collection}.
     *
     * @param request the group creation request
     * @since 0.9
     */
    void createGroup(CreateGroupRequest request);

    Provider getProvider();

}
