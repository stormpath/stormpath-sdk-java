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
package com.stormpath.sdk.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * An {@code Application} instance represents a Stormpath
 * <a href="https://www.stormpath.com/docs/managing-applications">registered application</a>.
 *
 * @since 0.1
 */
public interface Application extends Resource, Saveable, Deletable {

    /**
     * Returns the Application's name.  An application's name must be unique across all other applications in the
     * owning Tenant.
     *
     * @return the Application's name
     */
    String getName();

    /**
     * Sets the application's name.  Application names must be unique within a Tenant.
     *
     * @param name tenant-unique name of the application.
     */
    void setName(String name);

    /**
     * Returns the application description.
     *
     * @return the application description.
     */
    String getDescription();

    /**
     * Sets the application description.
     *
     * @param description the application description.
     */
    void setDescription(String description);

    /**
     * Returns the application's status.  Application users may login to an enabled application.  They may not login
     * to a disabled application.
     *
     * @return the application's status.
     */
    ApplicationStatus getStatus();

    /**
     * Sets the application's status.  Application users may login to an enabled application.  They may not login
     * to a disabled application.
     *
     * @param status the application's status.
     */
    void setStatus(ApplicationStatus status);

    /**
     * Returns a paginated list of all accounts that may login to the application.
     * <p/>
     * Tip: Instead of iterating over all accounts, it might be more convenient (and practical) to execute a search
     * for one or more accounts using the {@link #getAccounts(com.stormpath.sdk.account.AccountCriteria)} or
     * {@link #getAccounts(java.util.Map)} methods instead of this one.
     *
     * @return a paginated list of all accounts that may login to the application.
     * @see #getAccounts(com.stormpath.sdk.account.AccountCriteria)
     * @see #getAccounts(java.util.Map)
     */
    AccountList getAccounts();

    /**
     * Returns a paginated list of the accounts that may login to the application that also match the specified query
     * criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../applications/applicationId/accounts?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the application's accounts that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the accounts that may login to the application that also match the specified query
     * criteria.
     * The {@link com.stormpath.sdk.account.Accounts Accounts} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * application.getAccounts(Accounts
     *     .where(Accounts.surname().containsIgnoreCase("Smith"))
     *     .and(Accounts.givenName().eqIgnoreCase("John"))
     *     .orderBySurname().descending()
     *     .withGroups(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.account.Accounts.*;
     *
     * ...
     *
     * application.getAccounts(where(
     *     surname().containsIgnoreCase("Smith"))
     *     .and(givenName().eqIgnoreCase("John"))
     *     .orderBySurname().descending()
     *     .withGroups(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the application's accounts that match the specified query criteria.
     * @since 0.8
     */
    AccountList getAccounts(AccountCriteria criteria);

    /**
     * Creates a new Account that may login to this application.
     * <p/>
     * This is mostly a convenience method; it delegates creation to the Application's designated
     * {@link #getDefaultAccountStore() defaultAccountStore}, and functions as follows:
     * <ul>
     * <li>If the {@code defaultAccountStore} is a Directory: the account is created in the Directory and returned.</li>
     * <li>If the {@code defaultAccountStore} is a Group: the account is created in the Group's Directory, assigned to
     * the Group, and then returned.</li>
     * </ul>
     *
     * @param account the account to create/persist
     * @return a new Account that may login to this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultAccountStore() defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be created.
     * @since 0.9
     */
    Account createAccount(Account account) throws ResourceException;

    /**
     * Creates a new Account that may login to this application according to the request criteria.
     * <p/>
     * This is mostly a convenience method; it delegates creation to the Application's designated
     * {@link #getDefaultAccountStore() defaultAccountStore}, and functions as follows:
     * <ul>
     * <li>If the {@code defaultAccountStore} is a Directory: the account is created in the Directory and returned.</li>
     * <li>If the {@code defaultAccountStore} is a Group: the account is created in the Group's Directory, assigned to
     * the Group, and then returned.</li>
     * </ul>
     * <h2>Example</h2>
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).build());
     * </pre>
     * <p/>
     * If you would like to force disabling the backing directory's account registration workflow:
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build());
     * </pre>
     * If you would like to force the execution of the registration workflow, no matter what the backing directory
     * configuration is:
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
     * </pre>
     *
     * @param request the account creation request
     * @return a new Account that may login to this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultAccountStore() defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be created.
     * @since 0.9
     */
    Account createAccount(CreateAccountRequest request) throws ResourceException;

    /**
     * Returns all Groups accessible to the application (based on the Application's associated Account stores).
     * <p/>
     * Tip: Instead of iterating over all groups, it might be more convenient (and practical) to execute a search
     * for one or more groups using the {@link #getGroups(java.util.Map)} method instead of this one.
     *
     * @return all Groups accessible to the application (based on the Application's associated Account stores).
     * @see #getGroups(java.util.Map)
     * @since 0.8
     */
    GroupList getGroups();

    /**
     * Returns a paginated list of the groups accessible to the application (based on the app's mapped Account stores)
     * that also match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../applications/applicationId/groups?param1=value1&param2=value2&...
     * </pre>
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the application's groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList getGroups(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the groups accessible to the application (based on the app's mapped Account stores)
     * that also match the specified query criteria.
     * The {@link com.stormpath.sdk.group.Groups Groups} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * application.getGroups(Groups
     *     .where(Groups.description().containsIgnoreCase("foo"))
     *     .and(Groups.name().startsWithIgnoreCase("bar"))
     *     .orderByName().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.group.Groups.*;
     *
     * ...
     *
     * application.getGroups(where(
     *     description().containsIgnoreCase("foo"))
     *     .and(name().startsWithIgnoreCase("bar"))
     *     .orderByName().descending()
     *     .withAccounts(10, 10)
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the application's accessible groups that match the specified query criteria.
     * @since 0.8
     */
    GroupList getGroups(GroupCriteria criteria);

    /**
     * Creates a new Group that may be used by this application in the application's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method.  It merely delegates to the Application's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     *
     * @param group the Group to create/persist
     * @return a new Group that may be used by this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultGroupStore() defaultGroupStore}
     *                           or if the designated {@code defaultGroupStore} does not allow new groups to be created.
     * @since 0.9
     */
    Group createGroup(Group group) throws ResourceException;

    /**
     * Returns the application's parent (owning) Tenant.
     *
     * @return the application's parent (owning) Tenant.
     */
    Tenant getTenant();

    /**
     * Sends a password reset email for the specified account username or email address.  The email will contain
     * a password reset link that the user can click or copy into their browser address bar.
     * <p/>
     * This method merely sends the password reset email that contains the link and nothing else.  You will need to
     * handle the link requests and then reset the account's password as described in the
     * {@link #verifyPasswordResetToken(String)} JavaDoc.
     *
     * @param accountUsernameOrEmail a username or email address of an Account that may login to the application.
     * @return the account corresponding to the specified username or email address.
     * @see #verifyPasswordResetToken(String)
     */
    Account sendPasswordResetEmail(String accountUsernameOrEmail);

    /**
     * Verifies a password reset token in a user-clicked link within an email.
     * <p/>
     * <h2>Base Link Configuration</h2>
     * You need to define the <em>Base</em> link that will process HTTP requests when users click the link in the
     * email as part of your Application's Workflow Configuration within the Stormpath UI Console.  It must be a URL
     * served by your application's web servers.  For example:
     * <pre>
     * https://www.myApplication.com/passwordReset
     * </pre>
     * <h2>Runtime Link Processing</h2>
     * When an application user clicks on the link in the email at runtime, your web server needs to process the request
     * and look for an {@code spToken} request parameter.  You can then verify the {@code spToken}, and then finally
     * change the Account's password.
     * <p/>
     * Usage Example:
     * <p/>
     * Browser:
     * {@code GET https://www.myApplication/passwordReset?spToken=someTokenValueHere}
     * <p/>
     * Your code:
     * <pre>
     * String token = httpServletRequest.getParameter("sptoken");
     *
     * Account account = application.verifyPasswordResetToken(token);
     *
     * //token has been verified - now set the new password with what the end-user submits:
     * account.setPassword(user_submitted_new_password);
     * account.save();
     * </pre>
     *
     * @param token the verification token, usually obtained as a request parameter by your application.
     * @return the Account matching the specified token.
     * @since 0.4
     */
    Account verifyPasswordResetToken(String token);

    /**
     * Authenticates an account's submitted principals and credentials (e.g. username and password).  The account must
     * be in one of the Application's assigned {@link #getAccountStoreMappings() account stores}.  If not
     * in an assigned account store, the authentication attempt will fail.
     * <h2>Example</h2>
     * Consider the following username/password-based example:
     * <p/>
     * <pre>
     * AuthenticationRequest request = new UsernamePasswordRequest(email, submittedRawPlaintextPassword);
     * Account authenticated = appToTest.authenticateAccount(request).getAccount();
     * </pre>
     *
     * @param request the authentication request representing an account's principals and credentials (e.g.
     *                username/password) used to verify their identity.
     * @return the result of the authentication.  The authenticated account can be obtained from
     *         {@code result.}{@link com.stormpath.sdk.authc.AuthenticationResult#getAccount() getAccount()}.
     * @throws ResourceException if the authentication attempt fails.
     */
    AuthenticationResult authenticateAccount(AuthenticationRequest request) throws ResourceException;

    /**
     * Returns all AccountStoreMappings accessible to the application.
     * <p/>
     * Tip: Instead of iterating over all accountStoreMappings, it might be more convenient (and practical) to execute a search
     * for one or more accountStoreMappings using the {@link #getAccountStoreMappings(java.util.Map)} method or the
     * {@link #getAccountStoreMappings(AccountStoreMappingCriteria)} instead of this one.
     *
     * @return all AccountStoreMappings accessible to the application.
     * @see #getAccountStoreMappings(java.util.Map)
     * @see #getAccountStoreMappings(AccountStoreMappingCriteria)
     * @since 0.9
     */
    AccountStoreMappingList getAccountStoreMappings();

    /**
     * Returns a paginated list of the application's mapped Account stores
     * that also match the specified query criteria.
     * <p/>
     * Each {@code queryParams} key/value pair will be converted to String name to String value pairs and appended to
     * the resource URL as query parameters, for example:
     * <pre>
     * .../applications/applicationId/accountStoreMappings?param1=value1&param2=value2&...
     * </pre>
     * <p/>
     * This is a type-unsafe alternative to the
     * {@link #getAccountStoreMappings(AccountStoreMappingCriteria) getAccountStoreMappings(accountStoreMappingCriteria)}
     * method, and might be useful when using dynamic languages like Groovy or JRuby.  Users of compiled languages,
     * or those that like IDE-completion, might favor the type-safe method instead.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the application's mapped account stores that match the specified query criteria.
     * @since 0.9
     * @see #getAccountStoreMappings(AccountStoreMappingCriteria)
     */
    AccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the application's mapped Account stores that also match the specified query
     * criteria.
     * The {@link AccountStoreMappings AccountStoreMappings} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * application.getAccountStoreMappings(AccountStoreMappings.criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.account.AccountStoreMappings.*;
     *
     * ...
     *
     * application.getAccountStoreMappings(criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the application's mapped account stores that match the specified query criteria.
     * @since 0.9
     */
    AccountStoreMappingList getAccountStoreMappings(AccountStoreMappingCriteria criteria);

    /**
     * Returns the {@link AccountStore} (either a {@link Group} or a
     * {@link com.stormpath.sdk.directory.Directory Directory}) used to persist
     * new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Application}, or
     * {@code null} if no accountStore has been designated.
     * <p/>
     * Because an Application is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Application delegates
     * new account persistence.
     * <h3>Directory or Group?</h3>
     * As both Group and Directory are sub-interfaces of {@link AccountStore}, you can determine which of the two
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
     * You may set the defaultAccountStore by acquiring one of the Application's
     * {@link #getAccountStoreMappings() accountStoreMappings} and calling
     * {@link AccountStoreMapping#setDefaultAccountStore(boolean) setDefaultAccountStore}<code>(true)</code> or by
     * calling {@link #setDefaultAccountStore(com.stormpath.sdk.directory.AccountStore)}
     *
     * @return the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *         new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Application}, or
     *         {@code null} if no accountStore has been designated.
     * @since 0.9
     */
    AccountStore getDefaultAccountStore();

    /**
     * Sets the {@link AccountStore} (either a {@link Group} or a
     * {@link com.stormpath.sdk.directory.Directory Directory}) used to persist
     * new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Application}.
     * <p/>
     * Because an Application is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating accounts; this method sets the AccountStore to which the Application delegates
     * new account persistence.
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *                     new accounts {@link #createAccount(com.stormpath.sdk.account.Account) created by the Application}
     */
    void setDefaultAccountStore(AccountStore accountStore);

    /**
     * Returns the {@link AccountStore} used to persist
     * new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Application}, or
     * {@code null} if no accountStore has been designated. <b>Stormpath's current REST API requires this to be
     * a Directory. However, this could be a Group in the future, so do not assume it is always a
     * Directory if you want your code to be function correctly if/when this support is added.</b>  Avoid casting the
     * returned value directly to a Directory: use the Visitor pattern as explained below.
     * <p/>
     * Because an Application is not an {@code AccountStore} itself, it delegates to a Directory (or maybe a Group in
     * the future) when creating groups; this method returns the AccountStore to which the Application delegates
     * new group persistence.
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
     * Again, in practice, Stormpath's current REST API requires this to be a Directory.  However, this could be
     * a Group in the future, so do not assume it will always be a Directory if you want your code to be
     * forward compatible; use the Visitor pattern and do not cast directly to a Directory.
     * <h3>Setting the 'New Group Store'</h3>
     * You set the newGroupStore by acquiring one of the Application's
     * {@link #getAccountStoreMappings() accountStoreMappings} and calling
     * {@link AccountStoreMapping#setDefaultGroupStore(boolean) setDefaultGroupStore}<code>(true)</code> or by
     * calling {@link #setDefaultGroupStore(com.stormpath.sdk.directory.AccountStore)}.
     *
     * @return the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *         new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Application}, or
     *         {@code null} if no accountStore has been designated.
     * @since 0.9
     */
    AccountStore getDefaultGroupStore();

    /**
     * Sets the {@link AccountStore} (a {@link com.stormpath.sdk.directory.Directory Directory}) that will be used to
     * persist new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Application}.
     * <b>Stormpath's current REST API requires this to be
     * a Directory. However, this could be a Group in the future, so do not assume it is always a
     * Directory if you want your code to be function correctly if/when this support is added.</b>
     * <p/>
     * Because an Application is not an {@code AccountStore} itself, it delegates to a Group or Directory
     * when creating groups; this method sets the AccountStore to which the Application delegates
     * new group persistence.
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *                     new groups {@link #createGroup(com.stormpath.sdk.group.Group) created by the Application}
     */
    void setDefaultGroupStore(AccountStore accountStore);

    /**
     * Creates a new {@link AccountStoreMapping} for this Application, allowing the associated
     * {@link AccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the Application.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method will call the server immediately.
     * <h3>Authentication Process and AccountStoreMapping Order</h3>
     * During an authentication attempt, an Application consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code AccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you control where the new {@code AccountStoreMapping} will reside in the Application's
     * overall list by setting its (zero-based)
     * {@link AccountStoreMapping#setListIndex(int) listIndex} property before calling this
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
     * {@code AccountStoreMapping} at the end of the list.
     * <h4>Example</h4>
     * Setting a new {@code AccountStoreMapping}'s {@code listIndex} to {@code 500} and then adding the mapping to
     * an application with an existing 3-item list will automatically save the {@code AccountStoreMapping} at the end
     * of the list and set its {@code listIndex} value to {@code 3} (items at index 0, 1, 2 were the original items,
     * the new fourth item will be at index 3).
     * <pre>
     * AccountStore directoryOrGroup = getDirectoryOrGroupYouWantToUseForLogin();
     * AccountStoreMapping mapping = client.instantiate(AccountStoreMapping.class);
     * mapping.setAccountStore(directoryOrGroup);
     * mapping.setListIndex(3); //this is zero-based, so index 3 == 4th item
     * mapping = application.createAccountStoreMapping(mapping);
     * </pre>
     * Then, when {@link #authenticateAccount(com.stormpath.sdk.authc.AuthenticationRequest) authenticating} an
     * account, this AccountStore (directory or group) will be consulted if no others before it in the list
     * found a matching account.
     * <h3>New Account Storage</h3>
     * If {@link #createAccount }
     *
     * @param mapping the new AccountStoreMapping resource to add to the Application's AccountStoreMapping list.
     * @return the newly created
     * @throws ResourceException
     * @since 0.9
     */
    AccountStoreMapping createAccountStoreMapping(AccountStoreMapping mapping) throws ResourceException;

    /**
     * Creates a new {@link AccountStoreMapping} for this Application and appends that
     * AccountStoreMapping to the end of the Application's AccountStoreMapping list, allowing the associated
     * {@link AccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the Application.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method will call the server immediately.
     * <h3>Authentication Process and AccountStoreMapping Order</h3>
     * During an authentication attempt, an Application consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code AccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you are setting the new {@code AccountStoreMapping} to the end of the Application's
     * overall list.
     * <h4>Example</h4>
     * <pre>
     * AccountStore directoryOrGroup = getDirectoryOrGroupYouWantToUseForLogin();
     * AccountStoreMapping mapping = application.addAccountStore(directoryOrGroup);
     * </pre>
     * Then, when {@link #authenticateAccount(com.stormpath.sdk.authc.AuthenticationRequest) authenticating} an
     * account, this AccountStore (directory or group) will be consulted if no others before it in the list
     * found a matching account.
     *
     * @param accountStore the new AccountStore resource to add to the Application's AccountStoreMapping list.
     * @return the newly created AccountStoreMapping instance.
     * @throws ResourceException
     * @since 0.9
     */
    AccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException;
}
