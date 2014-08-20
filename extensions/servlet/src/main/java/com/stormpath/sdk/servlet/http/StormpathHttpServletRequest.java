package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;
import com.stormpath.sdk.servlet.config.DefaultPropertiesResolver;
import com.stormpath.sdk.servlet.config.PropertiesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Properties;

public class StormpathHttpServletRequest extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(StormpathHttpServletRequest.class.getName());

    public static final String REMOTE_USER_STRATEGY    = "stormpath.request.remoteUser.strategy";
    public static final String USER_PRINCIPAL_STRATEGY = "stormpath.request.userPrincipal.strategy";

    public static final String EMAIL      = "email";
    public static final String USERNAME   = "username";
    public static final String GIVEN_NAME = "givenName";
    public static final String HREF       = "href";
    public static final String BYPASS     = "bypass";

    private static boolean remoteUserWarned = false; //static on purpose - we only want to print this once.

    private static final AccountResolver ACCOUNT_RESOLVER = new DefaultAccountResolver();

    private static final ApplicationResolver APPLICATION_RESOLVER = new DefaultApplicationResolver();

    private static final PropertiesResolver CONFIG_RESOLVER = new DefaultPropertiesResolver();

    public StormpathHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRemoteUser() {

        Properties properties = CONFIG_RESOLVER.getConfig(getServletContext());
        String strategy = properties.getProperty(REMOTE_USER_STRATEGY);

        if (BYPASS.equals(strategy)) {
            return super.getRemoteUser();
        }

        if (ACCOUNT_RESOLVER.hasAccount(this)) {
            return null;
        }

        Account account = ACCOUNT_RESOLVER.getAccount(this);

        if (!Strings.hasText(strategy) || USERNAME.equals(strategy)) {
            return account.getUsername();
        }

        if (EMAIL.equals(strategy)) {
            return account.getEmail();
        }

        if (GIVEN_NAME.equals(strategy)) {
            return account.getGivenName();
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

        Properties properties = CONFIG_RESOLVER.getConfig(getServletContext());
        String strategy = properties.getProperty(USER_PRINCIPAL_STRATEGY);

        if (BYPASS.equals(strategy)) {
            return super.getUserPrincipal();
        }

        if (ACCOUNT_RESOLVER.hasAccount(this)) {
            return null;
        }

        Account account = ACCOUNT_RESOLVER.getAccount(this);

        if (!Strings.hasText(strategy) || USERNAME.equals(strategy)) {
            return new UsernamePrincipal(account.getUsername());
        }

        if (EMAIL.equals(strategy)) {
            return new EmailPrincipal(account.getEmail());
        }

        if (GIVEN_NAME.equals(strategy)) {
            return new GivenNamePrincipal(account.getGivenName());
        }

        //strategy not recognized - warn and return:
        if (!remoteUserWarned) {
            String msg = "Unrecognized " + USER_PRINCIPAL_STRATEGY + " value [" + strategy + "].  Ignoring and " +
                         "defaulting to [" + USERNAME + "].  Please check your configuration.";
            log.warn(msg);
            remoteUserWarned = true;
        }

        return new UsernamePrincipal(account.getUsername());
    }

    @Override
    public boolean isUserInRole(String role) {

        //TODO: complete implementation
        throw new UnsupportedOperationException("Not yet implemented!");

        /*

        //todo: make this a little more configurable, i.e. AccountRoleResolver

        if (!ACCOUNT_RESOLVER.hasAccount(this)) {
            return false;
        }

        Account account = ACCOUNT_RESOLVER.getAccount(this);

        GroupList groups = account.getGroups();


        for(Group group : groups) {

        }
        */
    }

    @Override
    public String getAuthType() {
        //TODO: complete implementation
        return super.getAuthType();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        if (ACCOUNT_RESOLVER.hasAccount(this)) {
            Account account = ACCOUNT_RESOLVER.getAccount(this);
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
        } catch (IllegalArgumentException e) {
            // Should *never* happen since the argument is this object
            // (guaranteed to be non null and an HttpServletRequest)
            throw new ServletException("Unable to authenticate API request.", e);
        } catch (ResourceException e) {
            throw new ServletException("Unable to authenticate API request.", e);
        }

        Account account = result.getAccount();
        setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);

        return true;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        if (ACCOUNT_RESOLVER.hasAccount(this)) {
            Account account = ACCOUNT_RESOLVER.getAccount(this);
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

    protected AuthenticationRequest createAuthenticationRequest(String username, String password, Application application) {
        return new UsernamePasswordRequest(username, password, getRemoteHost());
    }

    protected Application getApplication() {
        return APPLICATION_RESOLVER.getApplication(getServletContext());
    }

    @Override
    public void logout() throws ServletException {
        removeAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME);
    }
}
