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
package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.spring.security.authz.permission.Permission;
import com.stormpath.spring.security.util.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@code AuthenticationProvider} implementation that uses the <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity
 * Management service for authentication and authorization operations for a single Application.
 * <p/>
 * The Stormpath-registered
 * <a href="https://www.stormpath.com/docs/libraries/application-rest-url">Application's Stormpath REST URL</a>
 * must be configured as the {@code applicationRestUrl} property.
 * <h3>Authentication</h3>
 * Once your application's REST URL is configured, this provider implementation automatically executes authentication
 * attempts without any need of further configuration by interacting with the Application's
 * <a href="http://www.stormpath.com/docs/rest/api#ApplicationLoginAttempts">loginAttempts endpoint</a>.
 * <h3>Authorization</h3>
 * Stormpath Accounts and Groups can be translated to Spring Security granted authorities via the following components.  You
 * can implement implementations of these interfaces and plug them into this provider for custom translation behavior:
 * <ul>
 * <li>{@link AccountPermissionResolver AccountPermissionResolver}</li>
 * <li>{@link GroupPermissionResolver GroupPermissionResolver}</li>
 * <li>{@link AccountGrantedAuthorityResolver AccountGrantedAuthorityResolver}</li>
 * <li>{@link GroupGrantedAuthorityResolver GroupGrantedAuthorityResolver}</li>
 * </ul>
 * <p/>
 * This provider implementation comes pre-configured with the following default implementations, which should suit most
 * Spring Security+Stormpath use cases:
 *
 * <table>
 *     <tr>
 *         <th>Property</th>
 *         <th>Pre-configured Implementation</th>
 *         <th>Notes</th>
 *     </tr>
 *     <tr>
 *         <td>{@link #getGroupGrantedAuthorityResolver() groupGrantedAuthorityResolver}</td>
 *         <td>{@link DefaultGroupGrantedAuthorityResolver}</td>
 *         <td>Each Stormpath Group can be represented as up to three possible Spring Security granted authorities (with
 *             1-to-1 being the default).  See the {@link DefaultGroupGrantedAuthorityResolver} JavaDoc for more info.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getAccountGrantedAuthorityResolver() accountGrantedAuthorityResolver}</td>
 *         <td>None</td>
 *         <td>Most Spring Security+Stormpath applications should only need the above {@code DefaultGroupGrantedAuthorityResolver}
 *             when using Stormpath Groups as Spring Security granted authorities.  This authentication provider implementation
 *             already acquires the {@link com.stormpath.sdk.account.Account#getGroups() account's assigned groups} and resolves the group
 *             granted authorities via the above {@code groupGrantedAuthorityResolver}.  <b>You only need to configure this property
 *             if you need an additional way to represent an account's assigned granted authorities that cannot already be
 *             represented via Stormpath account &lt;--&gt; group associations.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getGroupPermissionResolver() groupPermissionResolver}</td>
 *         <td>{@link GroupCustomDataPermissionResolver}</td>
 *         <td>The {@code GroupCustomDataPermissionResolver} assumes the convention that a Group's assigned permissions
 *         are stored as a nested {@code Set&lt;String&gt;} field in the
 *         {@link com.stormpath.sdk.group.Group#getCustomData() group's CustomData resource}.  See the
 *         {@link GroupCustomDataPermissionResolver} JavaDoc for more information.</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #getAccountPermissionResolver() accountPermissionResolver}</td>
 *         <td>{@link AccountCustomDataPermissionResolver}</td>
 *         <td>The {@code AccountCustomDataPermissionResolver} assumes the convention that an Account's directly
 *         assigned permissions are stored as a nested {@code Set&lt;String&gt;} field in the
 *         {@link com.stormpath.sdk.account.Account#getCustomData() account's CustomData resource}.  See the
 *         {@link AccountCustomDataPermissionResolver} JavaDoc for more information.</td>
 *     </tr>
 * </table>
 * <h4>Transitive Permissions</h4>
 * This implementation represents an Account's granted permissions as all permissions that:
 * <ol>
 *     <li>Are assigned directly to the Account itself</li>
 *     <li>Are assigned to any of the Account's assigned Groups</li>
 * </ol>
 * <h4>Assigning Permissions</h4>
 * A Spring Security Authentication Provider is a read-only component - it typically does not support account/group/permission
 * updates directly.
 * Therefore, you make modifications to these components by interacting with the data store (e.g. Stormpath) directly.
 * <p/>
 * The {@link com.stormpath.spring.security.authz.CustomDataPermissionsEditor CustomDataPermissionsEditor} has been provided for
 * this purpose. For example, assuming the convention of storing permissions in an account or group's CustomData
 * resource:
 * <pre>
 * Account account = getAccount();
 * new CustomDataPermissionsEditor(account.getCustomData())
 *     .append("someResourceType:anIdentifier:anAction")
 *     .append("anotherResourceType:anIdentifier:*")
 *     .remove("oldPermission");
 * account.save();
 * </pre>
 * Again, the default {@link #getGroupPermissionResolver() groupPermissionResolver} and
 * {@link #getAccountPermissionResolver() accountPermissionResolver} instances assume this CustomData storage strategy,
 * so if you use them, the above {@code CustomDataPermissionsEditor} will work easily.
 * <p/>
 * When the given credentials are successfully authenticated an {@link AuthenticationTokenFactory AuthenticationTokenFactory} instance
 * is used to create an authenticated token to be returned to the provider's client. By default, the {@link UsernamePasswordAuthenticationTokenFactory}
 * is used, constructing {@code UsernamePasswordAuthenticationToken} objects. By default, the principal stored in this token is a {@link StormpathUserDetails}
 * containing Stormpath account's properties like href, given name, username, etc. It can be easily modified by creating a new
 * <code>AuthenticationTokenFactory</code> and setting it to this provider via {@link #setAuthenticationTokenFactory(AuthenticationTokenFactory)}.
 *
 * @see AccountGrantedAuthorityResolver
 * @see GroupGrantedAuthorityResolver
 * @see AuthenticationTokenFactory
 * @see StormpathUserDetails
 */
public class StormpathAuthenticationProvider implements AuthenticationProvider {

    private Client client;
    private String applicationRestUrl;
    private GroupGrantedAuthorityResolver groupGrantedAuthorityResolver;
    private GroupPermissionResolver groupPermissionResolver;
    private AccountGrantedAuthorityResolver accountGrantedAuthorityResolver;
    private AccountPermissionResolver accountPermissionResolver;
    private AuthenticationTokenFactory authenticationTokenFactory;

    private Application application; //acquired via the client at runtime, not configurable by the StormpathAuthenticationProvider user

    public StormpathAuthenticationProvider() {
        setGroupGrantedAuthorityResolver(new DefaultGroupGrantedAuthorityResolver());
        setGroupPermissionResolver(new GroupCustomDataPermissionResolver());
        setAccountPermissionResolver(new AccountCustomDataPermissionResolver());
        setAuthenticationTokenFactory(new UsernamePasswordAuthenticationTokenFactory());
    }

    /**
     * Returns the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @return the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @param client the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Returns the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Spring Security-enabled application communicating with Stormpath via this Provider must be
     * configured by this property.
     *
     * @return the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public String getApplicationRestUrl() {
        return applicationRestUrl;
    }

    /**
     * Sets the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Spring Security-enabled application communicating with Stormpath via this Provider must be
     * configured by this property.
     *
     * @param applicationRestUrl the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public void setApplicationRestUrl(String applicationRestUrl) {
        this.applicationRestUrl = applicationRestUrl;
    }

    /**
     * Returns the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     * Unless overridden via {@link #setGroupGrantedAuthorityResolver(GroupGrantedAuthorityResolver) setGroupGrantedAuthorityResolver},
     * the default instance is a {@link DefaultGroupGrantedAuthorityResolver}.
     *
     * @return the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     */
    public GroupGrantedAuthorityResolver getGroupGrantedAuthorityResolver() {
        return groupGrantedAuthorityResolver;
    }

    /**
     * Sets the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     * Unless overridden, the default instance is a {@link DefaultGroupGrantedAuthorityResolver}.
     *
     * @param groupGrantedAuthorityResolver the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into
     *                                      Spring Security granted authorities.
     */
    public void setGroupGrantedAuthorityResolver(GroupGrantedAuthorityResolver groupGrantedAuthorityResolver) {
        this.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver;
    }

    /**
     * Returns the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @return the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.
     */
    public AccountGrantedAuthorityResolver getAccountGrantedAuthorityResolver() {
        return accountGrantedAuthorityResolver;
    }

    /**
     * Sets the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @param accountGrantedAuthorityResolver the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's
     *                                        assigned permissions
     */
    public void setAccountGrantedAuthorityResolver(AccountGrantedAuthorityResolver accountGrantedAuthorityResolver) {
        this.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver;
    }

    /**
     * Returns the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  Unless
     * overridden via {@link #setGroupPermissionResolver(GroupPermissionResolver) setGroupPermissionResolver}, the
     * default instance is a {@link GroupCustomDataPermissionResolver}.
     *
     * @return the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions
     * @since 0.2.0
     */
    public GroupPermissionResolver getGroupPermissionResolver() {
        return groupPermissionResolver;
    }

    /**
     * Sets the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned permissions.  Unless
     * overridden, the default instance is a {@link GroupCustomDataPermissionResolver}.
     *
     * @param groupPermissionResolver the {@link GroupPermissionResolver} used to discover a Stormpath Groups' assigned
     *                                permissions
     * @since 0.2.0
     */
    public void setGroupPermissionResolver(GroupPermissionResolver groupPermissionResolver) {
        this.groupPermissionResolver = groupPermissionResolver;
    }

    /**
     * Returns the {@link AccountPermissionResolver} used to discover a Stormpath Account's directly-assigned
     * permissions.  Unless overridden via
     * {@link #setAccountPermissionResolver(AccountPermissionResolver) setAccountPermissionResolver}, the default
     * instance is a {@link AccountCustomDataPermissionResolver}.
     *
     * @return the {@link AccountPermissionResolver} used to discover a Stormpath Account's assigned permissions.
     * @since 0.2.0
     */
    public AccountPermissionResolver getAccountPermissionResolver() {
        return accountPermissionResolver;
    }

    /**
     * Sets the {@link AccountPermissionResolver} used to discover a Stormpath Account's assigned permissions.  Unless
     * overridden, the default instance is a {@link AccountCustomDataPermissionResolver}.
     *
     * @param accountPermissionResolver the {@link AccountPermissionResolver} used to discover a Stormpath Account's
     *                                  assigned permissions
     * @since 0.2.0
     */
    public void setAccountPermissionResolver(AccountPermissionResolver accountPermissionResolver) {
        this.accountPermissionResolver = accountPermissionResolver;
    }

    /**
     *
     * Returns the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions. Unless
     * overridden, the default instance is a {@link UsernamePasswordAuthenticationTokenFactory}.
     *
     * @return the token factory to be used when creating tokens for the successfully authenticated credentials.
     */
    public AuthenticationTokenFactory getAuthenticationTokenFactory() {
        return authenticationTokenFactory;
    }

    /**
     * Sets the {@link AuthenticationTokenFactory} used to create authenticated tokens. Unless overridden via
     * {@link #setAuthenticationTokenFactory(AuthenticationTokenFactory)} setAuthenticationTokenFactory},
     * the default instance is a {@link UsernamePasswordAuthenticationTokenFactory}.
     *
     * @param authenticationTokenFactory the token factory to be used when creating tokens for the successfully
     *                                   authenticated credentials.
     */
    public void setAuthenticationTokenFactory(AuthenticationTokenFactory authenticationTokenFactory) {
        if (authenticationTokenFactory == null) {
            throw new IllegalArgumentException("authenticationTokenFactory cannot be null.");
        }
        this.authenticationTokenFactory = authenticationTokenFactory;
    }

    private void assertState() {
        if (this.client == null) {
            throw new IllegalStateException("Stormpath SDK Client instance must be configured.");
        }
        if (this.applicationRestUrl == null) {
            throw new IllegalStateException("\n\nThis application's Stormpath REST URL must be configured.\n\n  " +
                    "You may get your application's Stormpath REST URL as shown here:\n\n " +
                    "http://www.stormpath.com/docs/application-rest-url\n\n" +
                    "Copy and paste the 'REST URL' value as the 'applicationRestUrl' property of this class.");
        }
    }

    /**
     * Performs actual authentication for the received authentication credentials using
     * <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity Management service for a single application.
     *
     * @param authentication the authentication request object.
     *
     * @return a fully authenticated object including credentials.
     *
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        assertState();
        AuthenticationRequest request = createAuthenticationRequest(authentication);
        Application application = ensureApplicationReference();

        Account account;

        try {
            account = application.authenticateAccount(request).getAccount();
        } catch (ResourceException e) {
            String msg = StringUtils.clean(e.getMessage());
            if (msg == null) {
                msg = StringUtils.clean(e.getDeveloperMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationServiceException(msg, e);
        } finally {
            //Clear the request data to prevent later memory access
            request.clear();
        }

        Authentication authToken = this.authenticationTokenFactory.createAuthenticationToken(
                authentication.getPrincipal(), null, getGrantedAuthorities(account), account);

        return authToken;
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the indicated
     * <Code>Authentication</code> object.
     *
     * @param authentication the class to validate this <Code>AuthenticationProvider</code> supports
     *
     * @return <code>true</code> if the given class is supported
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return Authentication.class.isAssignableFrom(authentication);
    }

    //This is not thread safe, but the Client is, and this is only executed during initial Application
    //acquisition, so it is negligible if this executes a few times instead of just once.
    protected final Application ensureApplicationReference() {
        if (this.application == null) {
            String href = getApplicationRestUrl();
            this.application = client.getDataStore().getResource(href, Application.class);
        }
        return this.application;
    }

    protected AuthenticationRequest createAuthenticationRequest(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        return new UsernamePasswordRequest(username, password);
    }

    protected Collection<GrantedAuthority> getGrantedAuthorities(Account account) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

        GroupList groups = account.getGroups();

        for (Group group : groups) {
                Set<GrantedAuthority> groupGrantedAuthorities = resolveGrantedAuthorities(group);
                grantedAuthorities.addAll(groupGrantedAuthorities);

                Set<Permission> groupPermissions = resolvePermissions(group);
                grantedAuthorities.addAll(groupPermissions);
        }

        Set<GrantedAuthority> accountGrantedAuthorities = resolveGrantedAuthorities(account);
        grantedAuthorities.addAll(accountGrantedAuthorities);

        Set<Permission> accountPermissions = resolvePermissions(account);
        for (GrantedAuthority permission : accountPermissions) {
            grantedAuthorities.add(permission);
        }

        return grantedAuthorities;
    }

    private Set<GrantedAuthority> resolveGrantedAuthorities(Group group) {
        if (groupGrantedAuthorityResolver != null) {
            return groupGrantedAuthorityResolver.resolveGrantedAuthorities(group);
        }
        return Collections.emptySet();
    }

    private Set<GrantedAuthority> resolveGrantedAuthorities(Account account) {
        if (accountGrantedAuthorityResolver != null) {
            return accountGrantedAuthorityResolver.resolveGrantedAuthorities(account);
        }
        return Collections.emptySet();
    }

    //since 0.1.1
    private Set<Permission> resolvePermissions(Group group) {
        if (groupPermissionResolver != null) {
            return groupPermissionResolver.resolvePermissions(group);
        }
        return Collections.emptySet();
    }

    //since 0.1.1
    private Set<Permission> resolvePermissions(Account account) {
        if (accountPermissionResolver != null) {
            return accountPermissionResolver.resolvePermissions(account);
        }
        return Collections.emptySet();
    }


}
