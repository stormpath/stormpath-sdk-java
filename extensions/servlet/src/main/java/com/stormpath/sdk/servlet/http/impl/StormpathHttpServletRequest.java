/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticationBuilder;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultFailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultLogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.PasswordGrantAccessTokenResult;
import com.stormpath.sdk.servlet.http.AccountPrincipal;
import com.stormpath.sdk.servlet.http.EmailPrincipal;
import com.stormpath.sdk.servlet.http.GivenNamePrincipal;
import com.stormpath.sdk.servlet.http.HrefPrincipal;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UsernamePrincipal;
import com.stormpath.sdk.servlet.oauth.OAuthTokenResolver;
import com.stormpath.sdk.servlet.oauth.impl.AccessTokenResolver;
import com.stormpath.sdk.servlet.oauth.impl.RefreshTokenResolver;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class StormpathHttpServletRequest extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(StormpathHttpServletRequest.class.getName());

    public static final String AUTH_TYPE_REQUEST_ATTRIBUTE_NAME =
            StormpathHttpServletRequest.class.getName() + ".authType";

    public static final String AUTH_TYPE_BEARER = "Bearer";

    public static final String ACCOUNT = "account";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String GIVEN_NAME = "givenName";
    public static final String HREF = "href";
    public static final String BYPASS = "bypass";

    private static boolean remoteUserWarned = false; //static on purpose - we only want to print this once.
    private static boolean userPrincipalWarned = false; //static on purpose - we only want to print this once.

    private final HttpServletResponse response; //response paired with the request.

    private final UsernamePasswordRequestFactory usernamePasswordRequestFactory;
    private final Saver<AuthenticationResult> authenticationResultSaver;
    private final Publisher<RequestEvent> eventPublisher;
    private final String userPrincipalStrategyName;
    private final String remoteUserStrategyName;
    private final Resolver<String> organizationNameKeyResolver;

    public StormpathHttpServletRequest(HttpServletRequest request, HttpServletResponse response,
                                       UsernamePasswordRequestFactory usernamePasswordRequestFactory,
                                       Publisher<RequestEvent> eventPublisher,
                                       Saver<AuthenticationResult> authenticationResultSaver,
                                       Resolver<String> organizationNameKeyResolver,
                                       String userPrincipalStrategyName, String remoteUserStrategyName) {
        super(request);
        Assert.notNull(response, "HttpServletResponse cannot be null.");
        this.response = response;
        Assert.notNull(usernamePasswordRequestFactory, "UsernamePasswordRequestFactory cannot be null.");
        this.usernamePasswordRequestFactory = usernamePasswordRequestFactory;
        Assert.notNull(authenticationResultSaver, "AuthenticationResultSaver cannot be null.");
        this.authenticationResultSaver = authenticationResultSaver;
        Assert.notNull(eventPublisher, "EventPublisher cannot be null.");
        this.eventPublisher = eventPublisher;
        Assert.hasText(userPrincipalStrategyName, "userPrincipalStrategyName argument cannot be null or empty.");
        this.userPrincipalStrategyName = userPrincipalStrategyName;
        Assert.hasText(remoteUserStrategyName, "remoteUserStrategyName argument cannot be null or empty.");
        this.remoteUserStrategyName = remoteUserStrategyName;
        Assert.notNull(organizationNameKeyResolver, "organizationNameKeyResolver cannot be null.");
        this.organizationNameKeyResolver = organizationNameKeyResolver;
    }

    public UsernamePasswordRequestFactory getUsernamePasswordRequestFactory() {
        return usernamePasswordRequestFactory;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return authenticationResultSaver;
    }

    public String getUserPrincipalStrategyName() {
        return userPrincipalStrategyName;
    }

    public String getRemoteUserStrategyName() {
        return remoteUserStrategyName;
    }

    @Override
    public Object getAttribute(String name) {
        Object o = super.getAttribute(name);
        if (o != null) {
            return o;
        }
        if (name.startsWith("stormpath.")) {
            Config config = getConfig();
            if (config != null) { //null in Spring environments
                return config.get(name);
            }
        }
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {

        final Enumeration<String> enumeration = super.getAttributeNames();

        Map<String, ?> config = getConfig();
        if (config == null) { //spring environments
            config = new HashMap<String, Object>();
        }

        final Set<String> keys = config.keySet();
        final Iterator<String> configIterator = keys.iterator();

        return new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return enumeration.hasMoreElements() || configIterator.hasNext();
            }

            @Override
            public String nextElement() {
                if (enumeration.hasMoreElements()) {
                    return enumeration.nextElement();
                }
                return configIterator.next();
            }
        };
    }

    protected Config getConfig() {
        ServletContext servletContext = getServletContext();
        return (Config) servletContext.getAttribute(Config.class.getName());
    }

    protected boolean hasAccount() {
        return AccountResolver.INSTANCE.hasAccount(this);
    }

    protected Account getRequiredAccount() {
        return AccountResolver.INSTANCE.getRequiredAccount(this);
    }

    @Override
    public HttpSession getSession(boolean create) {
        //need to wrap the session so get/setAttribute and get/putValue implementations handle Resources efficiently:
        HttpSession session = super.getSession(create);
        if (session != null) {
            session = new StormpathHttpSession(session);
        }
        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String getRemoteUser() {

        String strategy = getRemoteUserStrategyName();

        if (BYPASS.equals(strategy)) {
            return super.getRemoteUser();
        }

        if (!hasAccount()) {
            return null;
        }

        Account account = getRequiredAccount();

        if (!Strings.hasText(strategy) || USERNAME.equals(strategy)) {
            return account.getUsername();
        }

        if (EMAIL.equals(strategy)) {
            return account.getEmail();
        }

        if (GIVEN_NAME.equals(strategy)) {
            return account.getGivenName();
        }

        if (HREF.equals(strategy)) {
            return account.getHref();
        }

        //strategy not recognized - warn and return:
        if (!remoteUserWarned) {
            String msg = "Unrecognized remote user strategy name [" + strategy + "].  Ignoring and " +
                    "defaulting to [" + USERNAME + "].  Please check your configuration.";
            log.warn(msg);
            remoteUserWarned = true;
        }

        return account.getUsername();
    }

    @Override
    public Principal getUserPrincipal() {

        String strategy = getUserPrincipalStrategyName();

        if (BYPASS.equals(strategy)) {
            return super.getUserPrincipal();
        }

        if (!hasAccount()) {
            return null;
        }

        Account account = getRequiredAccount();

        if (!Strings.hasText(strategy) || USERNAME.equals(strategy)) {
            return new UsernamePrincipal(account.getUsername());
        }

        if (ACCOUNT.equals(strategy)) {
            return new AccountPrincipal(account);
        }

        if (EMAIL.equals(strategy)) {
            return new EmailPrincipal(account.getEmail());
        }

        if (GIVEN_NAME.equals(strategy)) {
            return new GivenNamePrincipal(account.getGivenName());
        }

        if (HREF.equals(strategy)) {
            return new HrefPrincipal(account.getHref());
        }

        //strategy not recognized - warn and return:
        if (!userPrincipalWarned) {
            String msg = "Unrecognized user principal strategy name [" + strategy + "].  Ignoring and " +
                    "defaulting to [" + USERNAME + "].  Please check your configuration.";
            log.warn(msg);
            userPrincipalWarned = true;
        }

        return new UsernamePrincipal(account.getUsername());
    }

    @Override
    public boolean isUserInRole(String role) {

        if (!hasAccount()) {
            return false;
        }

        Assert.hasText(role, "Role name cannot be null or empty.");

        Account account = getRequiredAccount();

        //todo: make this customizable, i.e. AccountRoleResolver

        //TODO: enable account->groups collection caching to keep this fast

        GroupList groups = account.getGroups();
        for (Group group : groups) {
            if (role.equals(group.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getAuthType() {
        if (hasAccount()) {
            Object value = getAttribute(AUTH_TYPE_REQUEST_ATTRIBUTE_NAME);
            String sval = null;
            if (value != null) {
                sval = String.valueOf(value);
            }
            Assert.hasText(sval, "An authenticated account must be represented with a specific request authType.  " +
                    "This must be set by a Resolver<Account> on account discovery or immediately after " +
                    "login.  This is an implementation bug and should be reported.");
            return sval;
        }

        return null;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException("The HttpServletRequest.authenticate(response) method is not " +
                "supported.  Various HTTP-based authentication mechanisms " +
                "(Basic, OAuth Bearer, Form-based authentication, etc) are supported " +
                "via other url (path)-based mechanisms by the StormpathFilter " +
                "automatically.  Ensure you use those instead of calling " +
                "HttpServletRequest.authenticate(response) directly.");
    }

    @Override
    public void login(String username, String password) throws ServletException {

        final AuthenticationRequest authcRequest = createAuthenticationRequest(username, password);

        if (hasAccount()) {
            Account account = getRequiredAccount();
            String msg = "The current request is already associated with an authenticated user [" + account.getEmail() +
                    "].  Login attempt for submitted username [" + username + "] is denied.";

            ServletException ex = new ServletException(msg);

            FailedAuthenticationRequestEvent e = createEvent(authcRequest, ex);

            publish(e);

            throw ex;
        }

        AccessTokenResult result;
        try {
            OAuthPasswordGrantRequestAuthenticationBuilder requestBuilder = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder()
                    .setPassword(password)
                    .setLogin(username);

            AccountStore accountStore = authcRequest.getAccountStore();

            if (accountStore != null) {
                requestBuilder.setAccountStore(accountStore);
            }

            //@since 1.2.0
            //https://github.com/stormpath/stormpath-sdk-java/issues/742
            String organizationNameKey = organizationNameKeyResolver.get(this, null);
            if(Strings.hasText(organizationNameKey)) {
                requestBuilder.setOrganizationNameKey(organizationNameKey);
            }

            OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR
                    .forApplication(getApplication())
                    .authenticate(requestBuilder.build());

            result = createAccessTokenResult(authenticationResult);
        } catch (ResourceException e) {
            FailedAuthenticationRequestEvent evt = createEvent(authcRequest, e);
            publish(evt);

            String msg = "Unable to authenticate account for submitted username [" + username + "].";
            throw new ServletException(msg, e);
        }

        setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, "LOGIN_METHOD");

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);

        setAttribute(AccessTokenResolver.REQUEST_ATTR_NAME, result.getTokenResponse().getAccessToken());
        setAttribute(RefreshTokenResolver.REQUEST_ATTR_NAME, result.getTokenResponse().getRefreshToken());

        setAttribute(OAuthTokenResolver.REQUEST_ATTR_NAME, result);

        SuccessfulAuthenticationRequestEvent e = createEvent(authcRequest, result);
        publish(e);
    }

    protected AccessTokenResult createAccessTokenResult(final OAuthGrantRequestAuthenticationResult result) {
        final TokenResponse tokenResponse =
                DefaultTokenResponse.tokenType(TokenType.BEARER)
                        .accessToken(result.getAccessTokenString())
                        .refreshToken(result.getRefreshTokenString())
                        .applicationHref(getApplication().getHref())
                        .expiresIn(String.valueOf(result.getExpiresIn())).build();
        return new PasswordGrantAccessTokenResult(result.getAccessToken().getAccount(), tokenResponse);
    }

    protected FailedAuthenticationRequestEvent createEvent(AuthenticationRequest authcRequest, Exception ex) {
        return new DefaultFailedAuthenticationRequestEvent(this, this.response, authcRequest, ex);
    }

    protected SuccessfulAuthenticationRequestEvent createEvent(AuthenticationRequest authcRequest,
                                                               AuthenticationResult result) {
        return new DefaultSuccessfulAuthenticationRequestEvent(this, this.response, authcRequest, result);
    }

    protected LogoutRequestEvent createLogoutEvent() {
        Account account = hasAccount() ? getRequiredAccount() : null;
        return new DefaultLogoutRequestEvent(this, this.response, account);
    }

    protected void publish(RequestEvent e) throws ServletException {

        try {
            this.eventPublisher.publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish request event: " + ex.getMessage();
            throw new ServletException(msg, ex);
        }
    }

    protected AuthenticationRequest createAuthenticationRequest(String username, String password) {
        return getUsernamePasswordRequestFactory().createUsernamePasswordRequest(this, response, username, password);
    }

    protected Application getApplication() {
        return (Application) getAttribute(Application.class.getName());
    }

    @Override
    public void logout() throws ServletException {

        LogoutRequestEvent e = createLogoutEvent();
        publish(e);

        //remove authc state:
        Saver<AuthenticationResult> saver = getAuthenticationResultSaver();
        saver.set(this, response, null);

        //clear out attributes such that getRemoteUser(), getAuthType() and getUserPrincipal() all return null
        //per the Servlet spec:
        removeAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME);

        removeAttribute(Account.class.getName());

        HttpSession session = getSession(false);
        if (session != null) {
            session.removeAttribute(Account.class.getName());
        }
    }
}
