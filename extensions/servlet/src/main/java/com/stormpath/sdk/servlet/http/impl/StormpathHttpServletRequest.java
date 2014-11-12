package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.http.AccountPrincipal;
import com.stormpath.sdk.servlet.http.EmailPrincipal;
import com.stormpath.sdk.servlet.http.GivenNamePrincipal;
import com.stormpath.sdk.servlet.http.HrefPrincipal;
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

    public static final String REMOTE_USER_STRATEGY    = "stormpath.servlet.request.remoteUser.strategy";
    public static final String USER_PRINCIPAL_STRATEGY = "stormpath.servlet.request.userPrincipal.strategy";

    public static final String ACCOUNT    = "account";
    public static final String EMAIL      = "email";
    public static final String USERNAME   = "username";
    public static final String GIVEN_NAME = "givenName";
    public static final String HREF       = "href";
    public static final String BYPASS     = "bypass";

    private static boolean remoteUserWarned    = false; //static on purpose - we only want to print this once.
    private static boolean userPrincipalWarned = false; //static on purpose - we only want to print this once.

    public StormpathHttpServletRequest(HttpServletRequest request) {
        super(request);
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

        Config config = getConfig();
        String strategy = config.get(REMOTE_USER_STRATEGY);

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
            String msg = "Unrecognized " + REMOTE_USER_STRATEGY + " value [" + strategy + "].  Ignoring and " +
                         "defaulting to [" + USERNAME + "].  Please check your configuration.";
            log.warn(msg);
            remoteUserWarned = true;
        }

        return account.getUsername();
    }

    @Override
    public Principal getUserPrincipal() {

        Config config = getConfig();
        String strategy = config.get(USER_PRINCIPAL_STRATEGY);

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
            String msg = "Unrecognized " + USER_PRINCIPAL_STRATEGY + " value [" + strategy + "].  Ignoring and " +
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
        //TODO: complete implementation
        return super.getAuthType();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {

        if (hasAccount()) {
            Account account = getAccount();
            String msg = "The current request is already associated with an authenticated user [" + account.getEmail()
                         + "].  Authentication attempt for the current request is denied.";
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

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);

        return true;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        if (hasAccount()) {
            Account account = getAccount();
            String msg = "The current request is already associated with an authenticated user [" + account.getEmail()
                         + "].  Login attempt for submitted username [" + username + "] is denied.";
            throw new ServletException(msg);
        }

        Application application = getApplication();

        AuthenticationRequest authcRequest = createAuthenticationRequest(username, password, application);

        AuthenticationResult result;
        try {
            result = application.authenticateAccount(authcRequest);
        } catch (ResourceException e) {
            String msg = "Unable to authenticate account for submitted username [" + username + "].";
            throw new ServletException(msg, e);
        }

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
    }

    protected AuthenticationRequest createAuthenticationRequest(String username, String password,
                                                                Application application) {
        return new UsernamePasswordRequest(username, password, getRemoteHost());
    }

    protected Application getApplication() {
        return ApplicationResolver.INSTANCE.getApplication(getServletContext());
    }

    @Override
    public void logout() throws ServletException {
        removeAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME);
    }
}
