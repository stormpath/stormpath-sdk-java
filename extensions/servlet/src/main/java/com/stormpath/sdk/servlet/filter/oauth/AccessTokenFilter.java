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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.Mutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokenFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenFilter.class);

    protected static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    protected static final String ACCESS_TOKEN_RESULT_FACTORY = "stormpath.servlet.filter.accessToken.resultFactory";

    protected static final String ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY =
        "stormpath.servlet.filter.accessToken.authenticationRequestFactory";

    protected static final String ACCESS_TOKEN_REQUEST_AUTHORIZER =
        "stormpath.servlet.filter.accessToken.requestAuthorizer";

    protected static final String ACCOUNT_STATE_STORE = "stormpath.web.account.state.store";

    private AccessTokenRequestAuthorizer requestAuthorizer;
    private AccessTokenAuthenticationRequestFactory authenticationRequestFactory;
    private AccessTokenResultFactory resultFactory;
    private Mutator<AuthenticationResult> accountStateStore;

    public AccessTokenRequestAuthorizer getRequestAuthorizer() {
        return this.requestAuthorizer;
    }

    public AccessTokenAuthenticationRequestFactory getAuthenticationRequestFactory() {
        return this.authenticationRequestFactory;
    }

    public AccessTokenResultFactory getResultFactory() {
        return this.resultFactory;
    }

    public Mutator<AuthenticationResult> getAccountStateStore() {
        return this.accountStateStore;
    }

    @Override
    protected void onInit() throws ServletException {

        String className = getConfig().get(ACCESS_TOKEN_REQUEST_AUTHORIZER);
        Assert.hasText(className, ACCESS_TOKEN_REQUEST_AUTHORIZER + " class name value is required.");
        this.requestAuthorizer = Classes.newInstance(className);

        className = getConfig().get(ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY);
        Assert.hasText(className, ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY + " class name value is required.");
        this.authenticationRequestFactory = Classes.newInstance(className);

        className = getConfig().get(ACCESS_TOKEN_RESULT_FACTORY);
        Assert.hasText(className, ACCESS_TOKEN_RESULT_FACTORY + " class name value is required.");
        this.resultFactory = Classes.newInstance(className);

        className = getConfig().get(ACCOUNT_STATE_STORE);
        Assert.hasText(className, ACCOUNT_STATE_STORE + " class name value is required.");
        this.accountStateStore = Classes.newInstance(className);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String json;

        try {

            assertAuthorizedAccessTokenRequest(request);

            AuthenticationRequest authcRequest = createTokenAuthenticationRequest(request);

            AuthenticationResult ar;
            try {
                Application app = getApplication(request);
                ar = app.authenticateAccount(authcRequest);
            } catch (ResourceException e) {
                log.debug("Unable to authenticate access token request: " + e.getMessage(), e);
                throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_CLIENT);
            }

            AccessTokenResult result = createAccessTokenResult(request, response, ar);

            saveResult(request, response, result);

            json = result.getTokenResponse().toJson();

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (AccessTokenRequestException e) {

            log.debug("Unable to process OAuth token request.", e);

            json = toJson(e);

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    protected void assertAuthorizedAccessTokenRequest(HttpServletRequest request) {

        //POST is required: https://tools.ietf.org/html/rfc6749#section-3.2
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            String msg = "HTTP POST is required.";
            throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_REQUEST, msg, null);
        }

        //Secure connections are required: https://tools.ietf.org/html/rfc6749#section-3.2
        if (!request.isSecure()) {
            String msg = "A secure HTTPS connection is required.";
            throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_REQUEST, msg, null);
        }

        //grant_type is always required for all token requests:
        String grantType = Strings.clean(request.getParameter(GRANT_TYPE_PARAM_NAME));
        if (grantType == null) {
            String msg = "Missing grant_type value.";
            throw new AccessTokenRequestException(AccessTokenErrorCode.INVALID_REQUEST, msg, null);
        }

        getRequestAuthorizer().assertAuthorizedAccessTokenRequest(request);
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
    }

    protected String toJson(AccessTokenRequestException e) {

        String json = "{" + toJson("error", e.getErrorCode());

        String val = e.getDescription();
        if (Strings.hasText(val)) {
            json += "," + toJson("error_description", val);
        }

        val = e.getUri();
        if (Strings.hasText(val)) {
            json += "," + toJson("error_uri", val);
        }

        json += "}";

        return json;
    }

    protected static String toJson(String name, Object value) {
        String stringValue = String.valueOf(value);
        return quote(name) + ":" + quote(stringValue);
    }

    protected static String quote(String val) {
        return "\"" + val + "\"";
    }

    protected AuthenticationRequest createTokenAuthenticationRequest(HttpServletRequest request)
        throws AccessTokenRequestException {
        return getAuthenticationRequestFactory().createAccessTokenAuthenticationRequest(request);
    }

    protected AccessTokenResult createAccessTokenResult(final HttpServletRequest request,
                                                        final HttpServletResponse response,
                                                        final AuthenticationResult result) {
        return getResultFactory().createAccessTokenResult(request, response, result);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAccountStateStore().set(request, response, result);
    }
}
