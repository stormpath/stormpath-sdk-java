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
package com.stormpath.sdk.application;


import com.stormpath.sdk.account.*;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequestBuilder;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.saml.SamlPolicy;
import com.stormpath.sdk.tenant.Tenant;

import java.util.List;
import java.util.Map;

/**
 * An {@code Application} instance represents a Stormpath
 * <a href="https://www.stormpath.com/docs/managing-applications">registered application</a>.
 *
 * @since 0.1
 */
public interface Application extends AccountStoreHolder<Application>, Resource, Saveable, Deletable, Extendable, Auditable, AccountLinker {

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
     * @return this instance for method chaining.
     */
    Application setName(String name);

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
     * @return this instance for method chaining.
     */
    Application setDescription(String description);

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
     * @return this instance for method chaining.
     */
    Application setStatus(ApplicationStatus status);

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
     * @since 0.9
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
     * application.createAccount(Accounts.newCreateRequestFor(account).build());
     * </pre>
     *
     * <p>If you would like to force disabling the backing directory's account registration workflow:
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(false).build());
     * </pre>
     * If you would like to force the execution of the registration workflow, no matter what the backing directory
     * configuration is:
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
     * </pre>
     * If you would like to retrieve the account's custom data in the response of the account creation.
     * <pre>
     * application.createAccount(Accounts.newCreateRequestFor(account).withResponseOptions(Accounts.options().withCustomData()).build());
     * </pre>
     * </p>
     *
     * @param request the account creation request
     * @return a new Account that may login to this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultAccountStore()
     *                           defaultAccountStore}
     *                           or if the designated {@code defaultAccountStore} does not allow new accounts to be
     *                           created.
     * @since 0.9
     */
    Account createAccount(CreateAccountRequest request) throws ResourceException;

    /**
     * Returns all Groups accessible to the application. It will not only return any group associated directly as an
     * {@link AccountStore} but also every group that exists inside every directory associated as an account store.
     * <p/>
     * These groups can be used role-based access control checks, for example, 'if a user is in the admin group, allow
     * them to delete a user'.
     * <p/>
     * If you want to control which accounts can login to an application, you control that via the application's AccountStoreMappings
     * collection, not this method.
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
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupStore} or if the designated {@code defaultGroupStore} does not allow new
     *                           groups to be created.
     * @since 0.9
     */
    Group createGroup(Group group) throws ResourceException;

    /**
     * Creates a new Group that may be used by this application in the application's
     * {@link #getDefaultGroupStore() defaultGroupStore}
     * <p/>
     * This is a convenience method. It merely delegates to the Application's designated
     * {@link #getDefaultGroupStore() defaultGroupStore}.
     * <h2>Example</h2>
     * <pre>
     * application.createGroup(Groups.newCreateRequestFor(group).build());
     * </pre>
     * <p/>
     * If you would like to retrieve the group's custom data in the response of the groups creation.
     * <pre>
     * application.createGroup(Groups.newCreateRequestFor(group).withResponseOptions(Groups.options().withCustomData()).build());
     * </pre>
     *
     * @param request the group creation request
     * @return a new Group that may be used by this application.
     * @throws ResourceException if the Application does not have a designated {@link #getDefaultGroupStore()
     *                           defaultGroupsStore} or if the designated {@code defaultGroupsStore} does not allow new
     *                           groups to be created.
     * @since 0.9
     */
    Group createGroup(CreateGroupRequest request);

    /**
     * Returns the application's parent (owning) Tenant.
     *
     * @return the application's parent (owning) Tenant.
     */
    Tenant getTenant();

    /**
     * Sends a password reset email for the specified account email address.  The email will contain
     * a password reset link that the user can click or copy into their browser address bar.
     * <p/>
     * This method merely sends the password reset email that contains the link and nothing else.  You will need to
     * handle the link requests and then reset the account's password as described in the
     * {@link #verifyPasswordResetToken(String)} JavaDoc.
     *
     * @param email an email address of an Account that may login to the application.
     * @return the {@code PasswordResetToken} created for the password reset email sent to the specified {@code email}
     * @see #verifyPasswordResetToken(String)
     * @see #resetPassword(String, String)
     * @throws ResourceException if there is no account that matches the specified email address
     */
    PasswordResetToken sendPasswordResetEmail(String email) throws ResourceException;

    /**
     * Sends a password reset email to an account in the specified {@code AccountStore} matching the specified
     * {@code email} address.  If the email does not match an account in the specified AccountStore, a
     * ResourceException will be thrown.  If you are unsure of which of the application's mapped account stores might
     * contain the account, use the more general
     * {@link #sendPasswordResetEmail(String) sendPasswordResetEmail(String email)} method instead.
     *
     * <p>This method is useful as a performance enhancement if the application might be mapped to many (dozens,
     * hundreds or thousands) of account stores.  This can be common in multi-tenant applications where each mapped
     * AccountStore represents a specific tenant or customer organization.  Specifying the AccountStore
     * in these scenarios bypasses the general email-only-based account search and performs a more-efficient direct
     * lookup directly against the specified AccountStore.  The AccountStore is usually discovered before calling this
     * method by inspecting a submitted tenant id or subdomain, e.g. http://ACCOUNT_STORE_NAME.foo.com </p>
     *
     * <p>Like the {@link #sendPasswordResetEmail(String)} method, this email merely sends the email that contains
     * a link that, when clicked, will take the user to a view (web page) that allows them to specify a new password.
     * When the new password is submitted, the {@link #verifyPasswordResetToken(String)} method is expected to be
     * called at that time.</p>
     *
     * @param email an email address of an Account that may login to the application.
     * @param accountStore the accountStore expected to contain an account with the specified email address
     * @return the {@code PasswordResetToken} created for the password reset email sent to the specified {@code email}
     * @see #sendPasswordResetEmail(String)
     * @see #verifyPasswordResetToken(String)
     * @see #resetPassword(String, String)
     * @throws ResourceException if the specified AccountStore is not mapped to this application or if the email address
     *                           is not in the specified Account store
     * @since 1.0.RC3
     */
    PasswordResetToken sendPasswordResetEmail(String email, AccountStore accountStore) throws ResourceException;

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
     * When an application user clicks on the link in the email at runtime, your web server needs to process the
     * request
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
     * Verifies the password reset token (received in the user's email) and immediately changes the password in the
     * same
     * request (if the token is valid).
     * <p/>
     * NOTE: Once the token has been successfully used, it is immediately invalidated and can't be used again. If you
     * need
     * to change the password again, you will previously need to execute {@link #sendPasswordResetEmail(String)} again
     * in order
     * to obtain a new password reset token.
     *
     * @param passwordResetToken the verification token, usually obtained as a request parameter by your application.
     * @param newPassword        the new password that will be set to the Account if the token is successfully
     *                           validated.
     * @return the Account matching the specified token.
     * @see #sendPasswordResetEmail(String)
     * @since 1.0.RC
     */
    Account resetPassword(String passwordResetToken, String newPassword);

    /**
     * Authenticates an account's submitted principals and credentials (e.g. username and password).  The account must
     * be in one of the Application's assigned {@link #getAccountStoreMappings() account stores}.  If not
     * in an assigned account store, the authentication attempt will fail.
     * <h2>Example</h2>
     * Consider the following username/password-based example:
     * <p/>
     * <pre>
     * AuthenticationRequest request = UsernamePasswordRequest.builder()
     *                         .setUsernameOrEmail(username)
     *                         .setPassword(submittedRawPlaintextPassword)
     *                         .build();
     * Account authenticated = appToTest.authenticateAccount(request).getAccount();
     * </pre>
     * Additionally, the Account can be requested to be expanded to avoid a new network transfer when obtaining it:
     * <p/>
     * <pre>
     * BasicAuthenticationOptions options = UsernamePasswordRequest.options().withAccount();
     * AuthenticationRequest request = UsernamePasswordRequest.builder()
     *                         .setUsernameOrEmail(username)
     *                         .setPassword(submittedRawPlaintextPassword)
     *                         .withResponseOptions(options)
     *                         .build();
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
     * Retrieves a Provider-based {@link Account}. The account must exist in one of the Provider-based {@link
     * com.stormpath.sdk.directory.Directory Directories}
     * assigned to the Application as an {@link #getAccountStoreMappings() account store}, the Directory must also be
     * Enabled. If not
     * in an assigned account store, the retrieval attempt will fail.
     * <h2>Example</h2>
     * Consider the following  example:
     * <p/>
     * <pre>
     * ProviderAccountRequest request = Providers.GOOGLE.account()
     *                  .setCode("4/mV9k80PpUB7XK_2RvOqOkNrI7I8C.krFm0WYFM_sY3pEBd8D1tNHT8u6jiwI")
     *                  .build();
     * ProviderAccountResult result = application.getAccount(request);
     * Account account = result.getAccount();
     * </pre>
     *
     * @param request the {@link ProviderAccountRequest} representing the Provider-specific account access data (e.g.
     *                <code>accessToken</code>) used to verify the identity.
     * @return the result of the access request. The {@link Account} can be obtained from
     *         {@code result.}{@link com.stormpath.sdk.provider.ProviderAccountResult#getAccount() getAccount()}.
     * @throws ResourceException if the access attempt fails.
     * @since 1.0.beta
     */
    ProviderAccountResult getAccount(ProviderAccountRequest request);

    /**
     * Returns all ApplicationAccountStoreMappings accessible to the application.
     * <p/>
     * Tip: Instead of iterating over all accountStoreMappings, it might be more convenient (and practical) to execute
     * a search for one or more accountStoreMappings using the {@link #getAccountStoreMappings(java.util.Map)} method
     * or the {@link #getAccountStoreMappings(ApplicationAccountStoreMappingCriteria)} instead of this one.
     *
     * @return all ApplicationAccountStoreMappings accessible to the application.
     * @see #getAccountStoreMappings(java.util.Map)
     * @see #getAccountStoreMappings(ApplicationAccountStoreMappingCriteria)
     * @since 0.9
     */
    ApplicationAccountStoreMappingList getAccountStoreMappings();

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
     * {@link #getAccountStoreMappings(ApplicationAccountStoreMappingCriteria) getAccountStoreMappings(accountStoreMappingCriteria)}
     * method, and might be useful when using dynamic languages like Groovy or JRuby.  Users of compiled languages,
     * or those that like IDE-completion, might favor the type-safe method instead.
     *
     * @param queryParams the query parameters to use when performing a request to the collection.
     * @return a paginated list of the application's mapped account stores that match the specified query criteria.
     * @see #getAccountStoreMappings(ApplicationAccountStoreMappingCriteria)
     * @since 0.9
     */
    ApplicationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams);

    /**
     * Returns a paginated list of the application's mapped Account stores that also match the specified query
     * criteria.
     * The {@link ApplicationAccountStoreMappings ApplicationAccountStoreMappings} utility class is available to help construct
     * the criteria DSL.  For example:
     * <pre>
     * application.getAccountStoreMappings(ApplicationAccountStoreMappings.criteria()
     *     .withAccountStore()
     *     .orderByListIndex();
     * </pre>
     * or, if using static imports:
     * <pre>
     * import static com.stormpath.sdk.application.ApplicationAccountStoreMappings.*;
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
     * @since 1.0.RC9
     */
    ApplicationAccountStoreMappingList getAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria);

    /**
     * Creates a new {@link com.stormpath.sdk.application.ApplicationAccountStoreMapping} for this Application, allowing the associated
     * {@link ApplicationAccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the Application.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method will call the server immediately.
     * <h3>Authentication Process and ApplicationAccountStoreMapping Order</h3>
     * During an authentication attempt, an Application consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code ApplicationAccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you control where the new {@code ApplicationAccountStoreMapping} will reside in the Application's
     * overall list by setting its (zero-based)
     * {@link ApplicationAccountStoreMapping#setListIndex(int) listIndex} property before calling this
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
     * {@code ApplicationAccountStoreMapping} at the end of the list.
     * <h4>Example</h4>
     * Setting a new {@code ApplicationAccountStoreMapping}'s {@code listIndex} to {@code 500} and then adding the mapping to
     * an application with an existing 3-item list will automatically save the {@code ApplicationAccountStoreMapping} at the end
     * of the list and set its {@code listIndex} value to {@code 3} (items at index 0, 1, 2 were the original items,
     * the new fourth item will be at index 3).
     * <pre>
     * AccountStore directoryOrGroup = getDirectoryOrGroupYouWantToUseForLogin();
     * ApplicationAccountStoreMapping mapping = client.instantiate(ApplicationAccountStoreMapping.class);
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
     * @param mapping the new ApplicationAccountStoreMapping resource to add to the Application's ApplicationAccountStoreMapping list.
     * @return the newly created ApplicationAccountStoreMapping instance.
     * @throws ResourceException
     * @since 0.9
     */
    ApplicationAccountStoreMapping createAccountStoreMapping(ApplicationAccountStoreMapping mapping) throws ResourceException;

    /**
     * Creates a new {@link ApplicationAccountStoreMapping} for this Application and appends that
     * ApplicationAccountStoreMapping to the end of the Application's ApplicationAccountStoreMapping list, allowing the associated
     * {@link ApplicationAccountStoreMapping#getAccountStore() accountStore} to be used as a source
     * of accounts that may login to the Application.
     * <p/>
     * <b>Usage Notice:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method will call the server immediately.
     * <h3>Authentication Process and ApplicationAccountStoreMapping Order</h3>
     * During an authentication attempt, an Application consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code ApplicationAccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you are setting the new {@code ApplicationAccountStoreMapping} to the end of the Application's
     * overall list.
     * <p/>
     * NOTE: If you already know the account store where the account resides, you can
     * specify it at the time the authentication request is created (for example,
     * {@link UsernamePasswordRequestBuilder#inAccountStore(AccountStore)}).
     * This way you will be avoiding the authentication attempt to cycle through the Application's account stores.
     * <p/>
     * <h4>Example</h4>
     * <pre>
     * AccountStore directoryGroupOrOrganization = getDirectoryGroupOrOrganizationYouWantToUseForLogin();
     * ApplicationAccountStoreMapping mapping = application.addAccountStore(directoryGroupOrOrganization);
     * </pre>
     * Then, when {@link #authenticateAccount(com.stormpath.sdk.authc.AuthenticationRequest) authenticating} an
     * account, this AccountStore (directory, organization or group) will be consulted if no others before it in the list
     * found a matching account.
     *
     * @param accountStore the new AccountStore resource to add to the Application's ApplicationAccountStoreMapping list.
     * @return the newly created ApplicationAccountStoreMapping instance.
     * @throws ResourceException
     * @since 0.9
     */
    ApplicationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException;

    /**
     * Gets an {@link ApiKey}, by its id, that belongs to an {@link Account} that has access to this application by a
     * mapped account store.
     * <p/>
     *
     * @param id the id of the {@link ApiKey} to be retrieved.
     * @return an {@link ApiKey}, by its id, that belongs to an {@link Account} that has access to this application by
     *         a
     *         mapped account store.
     * @throws ResourceException        when the ApiKey does not belong to the Account or the ApiKey does not exist.
     * @throws IllegalArgumentException if the {@code id} argument is null or empty.
     * @since 1.0.RC
     */
    ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException;

    /**
     * Gets an {@link ApiKey}, by its id, that belongs to an {@link Account} that has access to this application by a
     * mapped account store.
     * <p/>
     * A call to this method ensures that the returned {@link ApiKey} response reflects the specified {@link
     * ApiKeyOptions}.
     * </p>
     *
     * @param id      the id of the {@link ApiKey} to be retrieved.
     * @param options the {@link ApiKeyOptions} to use to customize the ApiKey resource upon retrieval.
     * @return an {@link ApiKey}, by its id, that belongs to an {@link Account} that has access to this application by
     *         a
     *         mapped account store
     *         with the specified {@link ApiKeyOptions}.
     * @throws ResourceException        when the ApiKey does not belong to the Account or the ApiKey does not exist.
     * @throws IllegalArgumentException if the {@code id} argument is null or empty, or if the {@code options} argument
     *                                  is null..
     * @since 1.0.RC
     */
    ApiKey getApiKey(String id, ApiKeyOptions options) throws ResourceException, IllegalArgumentException;

    /**
     * Creates a new {@link IdSiteUrlBuilder} that allows you to build a URL you can use to redirect your
     * application users to a hosted login/registration/forgot-password site - what Stormpath
     * calls an 'Identity Site' (or 'ID Site' for short) - for performing common user identity functionality.  When
     * the user is done (logging in, registering, etc), they will be redirected back to a {@code callbackUri} of
     * your choice.
     *
     * <p>This method is a complement to the {@link #newIdSiteCallbackHandler(Object)} method: you use this
     * {@code newIdSiteUrlBuilder} method to send the end-user to your ID Site.  When the end-user is finished on the ID
     * Site and they are returned to your application, you use the {@link #newIdSiteCallbackHandler(Object)} method to
     * process the result.</p>
     *
     * <h5>Example</h5>
     *
     * <p>Let's assume your application's end-users use a web browser to visit your web application at
     * {@code https://awesomeapp.com}.  When they login, you want to send them to something like
     * {@code https://my.awesomeapp.com} or {@code https://id.awesomeapp.com} so you don't have to build all the
     * login, registration, and forgot password screens from scratch.</p>
     *
     * <p>When your end-user clicks a 'login' link, you would redirect them to your ID Site.  That link click request
     * might be handled as follows:</p>
     *
     * <pre>
     * public void onLoginLinkClick(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    //this is the <b>fully qualified</b> URL that your end-user should return to after they are finished at the
     *    // ID Site.  It is usually a URL in your web application, for example: https://awesomeapp.com/id
     *    // For security reasons, <b>the callbackUri must equal a registered URI in the Stormpath Administration console</b>
     *    String callbackUri = "https://awesomeapp.com/id";
     *
     *    String redirectUrl = <b>application.newIdSiteUrlBuilder().{@link IdSiteUrlBuilder#setCallbackUri(String)
     * setCallbackUri}(callbackUri).build()</b>;
     *
     *    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
     *    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
     *    response.setHeader("Pragma", "no-cache");
     *
     *    //send a 302 redirect to your ID Site.  When they're done, they will return to your callbackUri:
     *    response.sendRedirect(redirectUrl);
     * }
     * </pre>
     *
     * <p>When the end-user is done using your ID Site, they will be redirected back to your specified
     * {@code callbackUri}.  <b>Requests submitted to your {@code callbackUri} should be handled via the
     * {@link #newIdSiteCallbackHandler(Object)} method.</b></p>
     *
     *
     *
     * @return a new {@link IdSiteUrlBuilder} that allows you to build a URL you can use to redirect your
     *         application users to a hosted login/registration/forgot-password 'ID Site'.
     * @see IdSiteUrlBuilder#setCallbackUri(String)
     * @see IdSiteUrlBuilder#setPath(String)
     * @see IdSiteUrlBuilder#setState(String)
     * @since 1.0.RC2
     */
    IdSiteUrlBuilder newIdSiteUrlBuilder();

    /**
     * Creates a new {@link SamlIdpUrlBuilder} that allows you to build a URL you can use to redirect your
     * application users to a SAML authentication site (Identity Provider or IdP) for performing common user identity functionality.  When
     * users are done (logging in, logging out, etc), they will be redirected back to a {@code callbackUri} of
     * your choice.
     *
     * <h5>Example</h5>
     *
     * <p>Let's assume your application's end-users use a web browser to visit your web application at
     * {@code https://awesomeapp.com}.  When they login, you want to send them to something like
     * {@code https://awesomeapp.com/userHome} so you don't have to build all the
     * login, registration, and forgot password screens from scratch.</p>
     *
     * <p>When your end-user clicks a 'login' link, you would redirect them to your SAML IdP page.  That link click request
     * might be handled as follows:</p>
     *
     * <pre>
     * public void onLoginLinkClick(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    // This is the <b>fully qualified</b> URL that your end-user should return to after they are finished at the
     *    // SAML IdP Site.  It is usually a URL in your web application, for example: https://my.awesomeapp.com
     *
     *    String callbackUri = "https://awesomeapp.com/userHome";
     *    String redirectUrl = <b>application.newSamlIdpUrlBuilder().{@link SamlIdpUrlBuilder#setCallbackUri(String)} setCallbackUri}(callbackUri).build()</b>;
     *
     *    response.setHeader("Cache-control", "no-cache, no-store");
     *    response.setHeader("Pragma", "no-cache");
     *
     *    //send a 302 redirect to your SAML IdP.  When they're done, they will return to your callbackUri:
     *    response.sendRedirect(redirectUrl);
     * }
     * </pre>
     *
     * <p>When the end-user is done using your ID Site, he will be redirected back to your specified
     * {@code callbackUri}.  <b>Requests submitted to your {@code callbackUri} should be handled via the
     * {@link #newSamlCallbackHandler(Object)} method.</b></p>
     *
     * @return a new {@link SamlIdpUrlBuilder} that allows you to build a URL you can use to redirect your
     * application users to a SAML authentication site (Identity Provider or IdP).
     *
     * @see SamlIdpUrlBuilder#setCallbackUri(String)
     *
     * @since 1.0.RC8
     */
    SamlIdpUrlBuilder newSamlIdpUrlBuilder();

    /**
     * Creates a new {@link IdSiteCallbackHandler} used to handle HTTP replies from your ID Site to your
     * application's {@code callbackUri}, as described in the {@link #newIdSiteUrlBuilder()} method.
     *
     * <p><b>This method should be called when processing an HTTP request sent by the ID Site to the
     * {@code callbackUri} specified via the {@link #newIdSiteUrlBuilder()} method.</b></p>
     *
     * <h5>Example</h5>
     *
     * <p>Assume that you previously {@link #newIdSiteUrlBuilder()}, built the URL, and redirected your end-user to
     * that URL.  When the end-user is finished interacting with your ID Site, they will be redirected back to the
     * {@code callbackUri} you specified when constructing the URL.  You would call this method when processing the
     * request to that {@code callbackUri}.</p>
     *
     * <p>For example, assume your callbackUri is {@code https://awesomeapp.com/id} and you process requests to that
     * URI with a (sample) {@code onIdSiteCallback} method below:</p>
     *
     * <pre>
     * public void onIdSiteCallback(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    <b>AccountResult result = application.newIdSiteCallbackHandler(request).getAccountResult();</b>
     *
     *    //get the account that signed-up or logged in successfully:
     *    Account account = result.getAccount();
     *
     *    //do whatever you want with the returned account :)
     *
     *    // result.isNewAccount() == true for a newly registered user
     *    // result.isNewAccount() == false for an existing user that logged in
     *
     *    // When you're done, you might want to redirect the end-user to a welcome page or
     *    // a 'my account' page, for example:
     *    response.sendRedirect("/myaccount");
     * }
     * </pre>
     *
     * <h5>Servlet Container or No Servlet Container?</h5>
     *
     * <p>This method will accept either a {@code javax.servlet.http.HttpServletRequest} instance if your app is running
     * in a Servlet container, or a manually-constructed {@link com.stormpath.sdk.http.HttpRequest} instance if it is
     * not.  See the {@link com.stormpath.sdk.http.HttpRequests} helper class to help build the request object if you
     * are running in a non-servlet environment.</p>
     *
     * @param httpRequest either an {@code javax.servlet.http.HttpServletRequest} instance (if your app runs in a
     *                    Servlet container) or a manually-constructed {@link com.stormpath.sdk.http.HttpRequest}
     *                    instance if it does not.
     * @return an {@link IdSiteCallbackHandler} that allows you customize how the {@code httpRequest} will be handled.
     * @throws IllegalArgumentException if the method argument is null or is not either a either a
     *                                  <a href="http://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequest.html">
     *                                  {@code javax.servlet.http.HttpServletRequest}</a> or
     *                                  {@link com.stormpath.sdk.http.HttpRequest} instance.
     * @see #newIdSiteUrlBuilder()
     * @see com.stormpath.sdk.http.HttpRequests
     * @see com.stormpath.sdk.idsite.IdSiteCallbackHandler
     * @see com.stormpath.sdk.idsite.IdSiteCallbackHandler#getAccountResult()
     *
     * @since 1.0.RC2
     */
    IdSiteCallbackHandler newIdSiteCallbackHandler(Object httpRequest);

    /**
     * Creates a new {@link SamlCallbackHandler} used to handle HTTP replies from your SAML Identity Provider (SAML IdP) to your
     * application's {@code callbackUri}, as described in the {@link #newSamlIdpUrlBuilder()} method.
     *
     * <p><b>This method should be called when processing an HTTP request sent by the SAML IdP to the
     * {@code callbackUri} specified via the {@link #newSamlIdpUrlBuilder()} method.</b></p>
     *
     * <h5>Example</h5>
     *
     * <p>Assume that you previously built the URL using the {@link #newSamlIdpUrlBuilder()}, and redirected your end-user to
     * that URL.  When the end-user is finished interacting with your SAML IdP Site, they will be redirected back to the
     * {@code callbackUri} you specified when constructing the URL. You would call this method when processing the
     * request to that {@code callbackUri}.</p>
     *
     * <p>For example, assume your callbackUri is {@code https://awesomeapp.com/userHome} and you process requests to that
     * URI with a (sample) {@code onSamlIdpCallback} method below:</p>
     *
     * <pre>
     * public void onSamlIdpCallback(HttpServletRequest request, HttpServletResponse response) {
     *
     *    Application application = client.getResource(myApplicationRestUrl, Application.class);
     *
     *    <b>AccountResult result = application.newSamlCallbackHandler(request).getAccountResult();</b>
     *
     *    //get the account that signed-up or logged in successfully:
     *    Account account = result.getAccount();
     *
     * }
     * </pre>
     *
     * <h5>Servlet Container or No Servlet Container?</h5>
     *
     * <p>This method will accept either a {@code javax.servlet.http.HttpServletRequest} instance if your app is running
     * in a Servlet container, or a manually-constructed {@link com.stormpath.sdk.http.HttpRequest} instance if it is
     * not.  See the {@link com.stormpath.sdk.http.HttpRequests} helper class to help build the request object if you
     * are running in a non-servlet environment.</p>
     *
     * @param httpRequest either an {@code javax.servlet.http.HttpServletRequest} instance (if your app runs in a
     *                    Servlet container) or a manually-constructed {@link com.stormpath.sdk.http.HttpRequest}
     *                    instance if it does not.
     * @return an {@link SamlCallbackHandler} that allows you customize how the {@code httpRequest} will be handled.
     * @throws IllegalArgumentException if the method argument is null or is not either a either a
     *                                  <a href="http://docs.oracle.com/javaee/7/api/javax/servlet/ServletRequest.html">
     *                                  {@code javax.servlet.http.HttpServletRequest}</a> or
     *                                  {@link com.stormpath.sdk.http.HttpRequest} instance.
     * @see #newSamlIdpUrlBuilder()
     * @see com.stormpath.sdk.http.HttpRequests
     * @see com.stormpath.sdk.idsite.IdSiteCallbackHandler
     * @see com.stormpath.sdk.idsite.IdSiteCallbackHandler#getAccountResult()
     *
     * @since 1.0.RC8
     */
    SamlCallbackHandler newSamlCallbackHandler(Object httpRequest);

    /**
     * Triggers the delivery of a new verification email for the specified account.
     * <p/>
     * This method is useful in scenarios where the <a href="http://docs.stormpath.com/console/product-guide/#workflow-automations">
     * Account Registration and Verification workflow</a> is enabled. If the welcome email has not been received by
     * a newly registered account, then the user will not be able to login until the account is verified.
     * <p/>
     * This method re-sends the verification email and allows the user to verify the account.
     * <p/>
     * The {@link com.stormpath.sdk.account.VerificationEmailRequest VerificationEmailRequest} resource must contain the email or the
     * username identifying the account. If the optional {@link com.stormpath.sdk.directory.AccountStore AccountStore} is
     * also specified, the desired Account will be sought only in that specific AccountStore.
     * Sample code:
     * <p/>
     * <pre>
     *      Directory dir = client.getResource("https://api.stormpath.com/v1/directories/7WcyHGlDa0V2Nk11Vum3Zd", Directory.class);
     *      VerificationEmailRequest verificationEmailRequest = Applications.verificationEmailBuilder()
     *                                          .setLogin("myaccountemail@mycompany.com")
     *                                          .setAccountStore(dir)
     *                                          .build();
     *      application.sendVerificationEmail(verificationEmailRequest);
     * </pre>
     *
     * @param verificationEmailRequest contains the required information for the verification email to be sent.
     * @since 1.0.0
     */
    public void sendVerificationEmail(VerificationEmailRequest verificationEmailRequest);

    /**
     * Convenience method to add a a new {@link AccountStore} to this application.
     * <p/>
     * The given String can be either an 'href' or a 'name' of a {@link Directory} or a {@link Group} belonging to the current Tenant.
     * <p/>
     * If the provided value is an 'href', this method will get the proper Resource and add it as a new AccountStore in this
     * Application without much effort. However, if the provided value is not an 'href', it will be considered as a 'name'. In this case,
     * this method will search for both a Directory and a Group whose names equal the provided <code>hrefOrName</code>. If only
     * one resource exists (either a Directory or a Group), then it will be added as a new AccountStore in this Application. However,
     * if there are two resources (a Directory and a Group) matching that name, a {@link com.stormpath.sdk.resource.ResourceException ResourceException}
     * will be thrown.
     * <p/>
     * At the end of this process, if a single matching resource is found, this method will then delegate the actual {@link ApplicationAccountStoreMapping}
     * creation to the {@link #addAccountStore(AccountStore)} method in order to fulfill its task.
     * </p>
     * Example providing an href:
     * <p/>
     * <pre>
     *      ApplicationAccountStoreMapping accountStoreMapping = application.addAccountStore("https://api.stormpath.com/v1/groups/2rwq022yMt4u2DwKLfzriP");
     * </pre>
     * Example providing a name:
     * <p/>
     * <pre>
     *      ApplicationAccountStoreMapping accountStoreMapping = application.addAccountStore("Foo Name");
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
     * @return the {@link ApplicationAccountStoreMapping} created after finding the actual resource described by <code>hrefOrName</code>. It returns
     * <code>null</code> if there is no group or directory matching the href or name given.
     * @throws ResourceException if the resource already exists as an account store in this application.
     * @throws IllegalArgumentException if the given hrefOrName matches more than one resource in the current Tenant.
     * @see #addAccountStore(com.stormpath.sdk.directory.DirectoryCriteria)
     * @see #addAccountStore(com.stormpath.sdk.group.GroupCriteria)
     * @since 1.0.RC3
     */
    ApplicationAccountStoreMapping addAccountStore(String hrefOrName);

    /**
     * Convenience method to add a new {@link Directory} as an {@link AccountStore} to this application.
     * <p/>
     * The provided {@link DirectoryCriteria} must match a single {@link Directory} in the current Tenant. If more than one
     * Directory matches the criteria, an {@link IllegalArgumentException} will be thrown. If no Directory matches the criteria,
     * this method will return <code>null</code>.
     * <p/>
     * When a single Directory is found, this method will then delegate the actual {@link ApplicationAccountStoreMapping} creation
     * to the {@link #addAccountStore(AccountStore)} method in order to fulfill its task.
     * </p>
     * Example:
     * <p/>
     * <pre>
     *      DirectoryCriteria criteria = Directories.criteria().add(Directories.name().eqIgnoreCase("Foo Dir Name"));
     *      ApplicationAccountStoreMapping accountStoreMapping = application.addAccountStore(criteria);
     * </pre>
     * <p/>
     * <b>USAGE NOTE 1:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method and will call the server immediately.
     *
     * @param criteria to search for the desired {@link Directory} to be added as an {@link AccountStore}.
     * @return the {@link ApplicationAccountStoreMapping} created after finding the actual resource matching the criteria. It returns
     * <code>null</code> if there is no Directory matching the criteria.
     * @throws ResourceException if the found {@link Directory} already exists as an account store in this application.
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     * @since 1.0.RC3
     */
    ApplicationAccountStoreMapping addAccountStore(DirectoryCriteria criteria);

    /**
     * Convenience method to add a new {@link Organization} as an {@link AccountStore} to this application.
     * <p/>
     * The provided {@link OrganizationCriteria} must match a single {@link Organization} in the current Tenant. If more than one
     * Organization matches the criteria, an {@link IllegalArgumentException} will be thrown. If no Organization matches the criteria,
     * this method will return <code>null</code>.
     * <p/>
     * When a single Organization is found, this method will then delegate the actual {@link ApplicationAccountStoreMapping} creation
     * to the {@link #addAccountStore(AccountStore)} method in order to fulfill its task.
     * </p>
     * Example:
     * <p/>
     * <pre>
     *      OrganizationCriteria criteria = Organizations.criteria().add(Organizations.name().eqIgnoreCase("Org Name"));
     *      ApplicationAccountStoreMapping accountStoreMapping = application.addAccountStore(criteria);
     * </pre>
     * <p/>
     * <b>USAGE NOTE 1:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method and will call the server immediately.
     *
     * @param criteria to search for the desired {@link Organization} to be added as an {@link AccountStore}.
     * @return the {@link ApplicationAccountStoreMapping} created after finding the actual resource matching the criteria. It returns
     * <code>null</code> if there is no Organization matching the criteria.
     * @throws ResourceException if the found {@link Organization} already exists as an account store in this application.
     * @throws IllegalArgumentException if the criteria matches more than one Organization in the current Tenant.
     * @since 1.0.RC7
     */
    ApplicationAccountStoreMapping addAccountStore(OrganizationCriteria criteria);

    /**
     * Convenience method to add a new {@link Group} as an {@link AccountStore} to this application.
     * <p/>
     * The provided {@link GroupCriteria} must match a single {@link Group} in the current Tenant. If more than one
     * Group matches the criteria, an {@link IllegalArgumentException} will be thrown. If no Group matches the criteria,
     * this method will return <code>null</code>.
     * <p/>
     * When a single Group is found, this method will then delegate the actual {@link ApplicationAccountStoreMapping} creation
     * to the {@link #addAccountStore(AccountStore)} method in order to fulfill its task.
     * </p>
     * Example:
     * <p/>
     * <pre>
     *      GroupCriteria criteria = Groups.criteria().add(Groups.name().containsIgnoreCase("Foo Group Name"));
     *      ApplicationAccountStoreMapping accountStoreMapping = application.addAccountStore(criteria);
     * </pre>
     * <p/>
     * <b>USAGE NOTE 1:</b> This method is not efficient as it will search for every Group within every Directory of this Tenant.
     * In order to do so, some looping takes place at the client side: groups exist within directories, therefore we need to loop through every
     * existing directory in order to find the required group. In contrast, if the Group's 'href' is already known, we suggest using
     * {@link #addAccountStore(String)} with the Group's 'href' since it is a more efficient mechanism where no actual search
     * needs to be carried out.
     * <p/>
     * <b>USAGE NOTE 2:</b> Unlike other methods in this class that require the {@link #save()} method to be called to
     * persist changes, this is a convenience method and will call the server immediately.
     *
     * @param criteria to search for the desired {@link Group} to be added as an {@link AccountStore}.
     * @return the {@link ApplicationAccountStoreMapping} created after finding the actual resource matching the criteria. It returns
     * <code>null</code> if there is no Group matching the criteria.
     * @throws ResourceException if the found {@link Group} already exists as an account store in this application.
     * @throws IllegalArgumentException if the criteria matches more than one Group in the current Tenant.
     * @see #addAccountStore(String)
     * @since 1.0.RC3
     */
    ApplicationAccountStoreMapping addAccountStore(GroupCriteria criteria);

    /**
     * Saves this {@link Application} resource and ensures the returned {@link Application} response reflects the specified options.  This
     * enhances performance by 'piggybacking' the response to return related resources you know you will use after
     * saving the application.
     *
     * @param responseOptions The {@code ApplicationOptions} to use to customize the Application resource returned in the save response.
     * @return this instance for method chaining.
     * @since 1.0.RC4.6
     */
    Application saveWithResponseOptions(ApplicationOptions responseOptions);

    /**
     * Returns the {@link com.stormpath.sdk.oauth.OAuthPolicy} associated with this application.
     * @return the {@link com.stormpath.sdk.oauth.OAuthPolicy} associated with this application.
     *
     * @since 1.0.RC7
     */
    OAuthPolicy getOAuthPolicy();

    /**
     * Returns the {@link SamlPolicy} associated with this application.
     * @return the {@link SamlPolicy} associated with this application.
     *
     * @since 1.0.RC8
     */
    SamlPolicy getSamlPolicy();

    /**
     * Returns the valid list of Valid Callback URIs for this application.
     * @return the list of String valid callback URIs for this application.
     *
     * @since 1.0.RC8
     */
    List<String> getAuthorizedCallbackUris();

    /**
     * Sets the list of Authorized Callback URIs for this application.
     * @return this instance for method chaining.
     *
     * @since 1.0.RC8
     */
    Application setAuthorizedCallbackUris(List<String> authorizedCallbackUris);

    /**
     * Adds a valid URI as an authorized callback URI for this application.
     * This is a convenience method and using it is equivalent to:
     * <pre>
     *     setAuthorizedCallbackUris(getAuthorizedCallbackUris().add(authorizedCallbackUri));
     * </pre>
     *
     * @return this instance for method chaining.
     * @see #setAuthorizedCallbackUris(java.util.List)
     *
     * @since 1.0.RC8
     */
    Application addAuthorizedCallbackUri(String authorizedCallbackUri);
}
