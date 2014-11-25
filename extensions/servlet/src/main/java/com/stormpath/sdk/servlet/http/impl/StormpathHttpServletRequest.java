package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.Servlets;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.http.AccountPrincipal;
import com.stormpath.sdk.servlet.http.EmailPrincipal;
import com.stormpath.sdk.servlet.http.GivenNamePrincipal;
import com.stormpath.sdk.servlet.http.HrefPrincipal;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UsernamePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

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
    private final String userPrincipalStrategyName;
    private final String remoteUserStrategyName;

    public StormpathHttpServletRequest(HttpServletRequest request, HttpServletResponse response,
                                       UsernamePasswordRequestFactory usernamePasswordRequestFactory,
                                       Saver<AuthenticationResult> authenticationResultSaver,
                                       String userPrincipalStrategyName, String remoteUserStrategyName) {
        super(request);
        Assert.notNull(response, "HttpServletResponse cannot be null.");
        this.response = response;
        Assert.notNull(usernamePasswordRequestFactory, "UsernamePasswordRequestFactory cannot be null.");
        this.usernamePasswordRequestFactory = usernamePasswordRequestFactory;
        Assert.notNull(authenticationResultSaver, "AuthenticationResultSaver cannot be null.");
        this.authenticationResultSaver = authenticationResultSaver;
        Assert.hasText(userPrincipalStrategyName, "userPrincipalStrategyName argument cannot be null or empty.");
        this.userPrincipalStrategyName = userPrincipalStrategyName;
        Assert.hasText(remoteUserStrategyName, "remoteUserStrategyName argument cannot be null or empty.");
        this.remoteUserStrategyName = remoteUserStrategyName;
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
            return getConfig().get(name);
        }
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {

        final Enumeration<String> enumeration = super.getAttributeNames();
        final Set<String> keys = getConfig().keySet();
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
        return ConfigResolver.INSTANCE.getConfig(getServletContext());
    }

    protected boolean hasAccount() {
        return AccountResolver.INSTANCE.hasAccount(this);
    }

    protected Account getAccount() {
        return AccountResolver.INSTANCE.getAccount(this);
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

        Account account = getAccount();

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

        Account account = getAccount();

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

        Account account = getAccount();

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

        if (hasAccount()) {
            Account account = getAccount();
            String msg = "The current request is already associated with an authenticated user [" + account.getEmail() +
                         "].  Authentication attempt for the current request is denied.";
            throw new ServletException(msg);
        }

        Application application = getApplication();

        //TODO find out if the request is an API request or a user login request.

        //TODO: discover if request submission represents a form submission to a known login URL or HTTP basic
        //if so, this is a 'normal' user login, otherwise it probably represents an API or OAuth authentication.

        ApiAuthenticationResult result;

        try {
            result = application.authenticateApiRequest(this);
        } catch (Throwable t) {
            throw new ServletException("Unable to authenticate API request.", t);
        }

        setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, "AUTHENTICATE_METHOD");

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);

        return true;
    }

    @Override
    public void login(String username, String password) throws ServletException {

        if (hasAccount()) {
            Account account = getAccount();
            String msg = "The current request is already associated with an authenticated user [" + account.getEmail() +
                         "].  Login attempt for submitted username [" + username + "] is denied.";
            throw new ServletException(msg);
        }

        AuthenticationRequest authcRequest = createAuthenticationRequest(username, password);

        Application application = getApplication();

        AuthenticationResult result;
        try {
            result = application.authenticateAccount(authcRequest);
        } catch (ResourceException e) {
            String msg = "Unable to authenticate account for submitted username [" + username + "].";
            throw new ServletException(msg, e);
        }

        setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, "LOGIN_METHOD");

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
    }

    protected AuthenticationRequest createAuthenticationRequest(String username, String password) {
        return getUsernamePasswordRequestFactory().createUsernamePasswordRequest(this, response, username, password);
    }

    protected Application getApplication() {
        return Servlets.getApplication(getServletContext());
    }

    @Override
    public void logout() throws ServletException {
        //remove authc state:
        Saver<AuthenticationResult> saver = getAuthenticationResultSaver();
        saver.set(this, response, null);
        removeAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME);
    }
}
