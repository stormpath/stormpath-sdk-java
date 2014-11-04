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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.RequestAccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.DefaultConfig;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.DefaultForm;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LoginFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);

    protected static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    /**
     * Returns the context-relative URL where a user can be redirected to login.
     *
     * @return the context-relative URL where a user can be redirected to login.
     */
    public String getLoginUrl() {
        return getConfig().getLoginUrl();
    }

    public String getLoginNextUrl() {
        return getConfig().getLoginNextUrl();
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String method = request.getMethod();

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            showForm(request, response);
        } else if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if (isTokenRequest(request)) {
                handleTokenRequest(request, response);
            } else {
                handleFormSubmission(request, response);
            }
        } else {
            ServletUtils.issueRedirect(request, response, getLoginUrl(), null, true, true);
        }
    }

    protected boolean isTokenRequest(HttpServletRequest request) {
        return Strings.hasText(request.getParameter(GRANT_TYPE_PARAM_NAME)) &&
               request.getContentType().toLowerCase().startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    protected boolean handleTokenRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Application app = getApplication(request);

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        String json;

        try {
            //TODO: THIS MUST ONLY BE ACCEPTED BY CLIENTS WITH KNOWN/TRUSTED JAVASCRIPT ORIGIN URIs

            String uri = ServerUriResolver.INSTANCE.getServerUri(request);

            String origin = request.getHeader("Origin");
            if (!Strings.hasText(origin) || !origin.startsWith(uri)) {
                throw new OauthException("unauthorized_client");
            }

            String grantType = request.getParameter(GRANT_TYPE_PARAM_NAME);
            if (!"password".equals(grantType)) {
                throw new OauthException(OauthAuthenticationException.INVALID_GRANT);
            }

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            UsernamePasswordRequest upRequest = new UsernamePasswordRequest(username, password, request.getRemoteHost());

            AuthenticationResult ar;
            try {
                ar = app.authenticateAccount(upRequest);
            } catch (ResourceException e) {
                throw new OauthException("invalid_client");
            }

            AccessTokenResult result = createAccessTokenResult(request, response, ar);

            response.setStatus(HttpServletResponse.SC_OK);

            saveAuthenticationState(request, response, result);

            json = result.getTokenResponse().toJson();

        } catch (OauthException e) {
            log.debug("Unable to authenticate OAuth token request.", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            json = "{\"error\":\"" + e.getOauthStatus() + "\"}";
        }

        response.getWriter().print(json);
        response.getWriter().flush();

        return false;
    }

    protected AccessTokenResult createAccessTokenResult(final HttpServletRequest request,
                                                        final HttpServletResponse response,
                                                        final AuthenticationResult result) {

        final Account account = result.getAccount();

        Client client = ClientResolver.INSTANCE.getClient(request.getServletContext());
        Application application = ApplicationResolver.INSTANCE.getApplication(request.getServletContext());

        ApiKey apiKey = ClientApiKeyResolver.INSTANCE.apply(client);
        String secret = apiKey.getSecret();

        int ttl = getConfig().getAccountCookieJwtTtl();

        String jwt = new AccountToJwtConverter(secret, ttl).apply(account);

        final TokenResponse tokenResponse = DefaultTokenResponse
            .tokenType(TokenType.BEARER)
            .accessToken(jwt)
            .applicationHref(application.getHref())
            .expiresIn(String.valueOf(ttl))
            .build();

        return new LoginFilterAccessTokenResult(account, tokenResponse);
    }

    protected void saveAuthenticationState(HttpServletRequest request, HttpServletResponse response,
                                           AuthenticationResult result) {

        List<String> locations = getConfig().getAccountStore();

        if (locations.contains("disabled")) {
            return;
        }

        for (String location : locations) {
            if ("cookie".equalsIgnoreCase(location)) {
                setAccountCookie(request, response, result);
            } else if ("session".equalsIgnoreCase(location)) {
                HttpSession session = request.getSession();
                session.setAttribute("account", result.getAccount());
            } else {
                throw new IllegalArgumentException("Unrecognized " + DefaultConfig.ACCOUNT_STORE + " config value: " +
                                                   location);
            }
        }
    }

    protected void setAccountCookie(HttpServletRequest request, HttpServletResponse response,
                                    AuthenticationResult result) {
        AccountCookieMutator.INSTANCE.set(request, response, result);
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
    }

    private void setForm(HttpServletRequest request, Form form) {
        request.setAttribute("form", form);
    }

    @SuppressWarnings("unchecked")
    protected Form createForm(HttpServletRequest request, boolean retainPassword) {

        DefaultForm form = new DefaultForm();

        String value = Strings.clean(request.getParameter("csrfToken"));
        value = value != null ? value : UUID.randomUUID().toString().replace("-", ""); //TODO add to cache
        form.setCsrfToken(value);

        value = Strings.clean(request.getParameter("next"));
        if (value != null) {
            form.setNext(value);
        }

        String[] fieldNames = new String[]{ "login", "password" };

        for (String fieldName : fieldNames) {

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setRequired(true);
            field.setType("text");
            String param = request.getParameter(fieldName);
            field.setValue(param != null ? param : "");

            String label;

            if ("login".equals(fieldName)) {
                label = "Email";
            } else if ("password".equals(fieldName)) {
                field.setType("password");
                label = "Password";
                if (!retainPassword) {
                    field.setValue("");
                }
            } else {
                //unrecognized property
                continue;
            }

            field.setLabel(label).setPlaceholder(label);

            form.addField(field);
        }

        form.autofocus();

        return form;
    }

    protected void showForm(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //addConfigProperties(request);
        Form form = createForm(request, false);
        setForm(request, form);
        String status = Strings.clean(request.getParameter("status"));
        if (status != null) {
            request.setAttribute("status", status);
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void handleFormSubmission(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        Form form = createForm(request, true);

        try {
            login(request, response, form);
        } catch (Exception e) {
            log.debug("Unable to login user.", e);

            List<String> errors = new ArrayList<String>(1);
            errors.add("Invalid username or password.");
            request.setAttribute("errors", errors);

            //addConfigProperties(request);

            //do not retain submitted password (not save to have in the DOM text):
            ((DefaultField) form.getField("password")).setValue("");
            setForm(request, form);

            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    protected Account getAccount(HttpServletRequest req) {
        return RequestAccountResolver.INSTANCE.getAccount(req);
    }

    protected void validate(Form form) throws ServletException, IOException {

        //validate CSRF
        Assert.hasText(form.getCsrfToken(), "CSRF Token must be specified."); //TODO check cache

        //ensure required fields are present:
        List<Field> fields = form.getFields();
        for (Field field : fields) {
            if (field.isRequired()) {
                String value = Strings.clean(field.getValue());
                if (value == null) {
                    //TODO: i18n
                    throw new IllegalArgumentException(Strings.capitalize(field.getName()) + " is required.");
                }
            }
        }
    }

    protected void login(HttpServletRequest req, HttpServletResponse resp, Form form)
        throws ServletException, IOException {

        validate(form);

        String usernameOrEmail = form.getFieldValue("login");
        String password = form.getFieldValue("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        final Account account = getAccount(req);

        //simulate a result for the benefit of the 'saveAuthenticationState' method signature:
        AuthenticationResult result = new AuthenticationResult() {
            @Override
            public Account getAccount() {
                return account;
            }

            @Override
            public void accept(AuthenticationResultVisitor visitor) {
                visitor.visit(this);

            }

            @Override
            public String getHref() {
                return account.getHref();
            }
        };

        saveAuthenticationState(req, resp, result);

        String next = form.getNext();

        if (!Strings.hasText(next)) {
            next = getLoginNextUrl();
        }

        ServletUtils.issueRedirect(req, resp, next, null, true, true);
    }

    private static class OauthException extends RuntimeException {
        private final String OAUTH_STATUS;
        public OauthException(String status) {
            Assert.hasText(status, "status cannot be null or empty.");
            this.OAUTH_STATUS = status;
        }

        public String getOauthStatus() {
            return OAUTH_STATUS;
        }
    }

    private static class LoginFilterAccessTokenResult implements AccessTokenResult {

        private final TokenResponse tokenResponse;
        private final Set<String> scope;
        private final Account account;

        private LoginFilterAccessTokenResult(Account account, TokenResponse tokenResponse) {
            Assert.notNull(tokenResponse);
            Assert.notNull(account);
            this.tokenResponse = tokenResponse;
            this.scope = Collections.emptySet(); //not supported at the moment
            this.account = account;
        }

        @Override
        public TokenResponse getTokenResponse() {
            return tokenResponse;
        }

        @Override
        public Set<String> getScope() {
            return scope;
        }

        @Override
        public ApiKey getApiKey() {
            return null;
        }

        @Override
        public Account getAccount() {
            return account;
        }

        @Override
        public void accept(AuthenticationResultVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public String getHref() {
            return null;
        }
    }
}
