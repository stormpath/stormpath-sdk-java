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
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.Servlets;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultFailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.Saver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokenFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenFilter.class);

    protected static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    protected static final String ACCESS_TOKEN_RESULT_FACTORY = "stormpath.web.accessToken.resultFactory";

    protected static final String ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY =
        "stormpath.web.accessToken.authenticationRequestFactory";

    protected static final String ACCESS_TOKEN_REQUEST_AUTHORIZER = "stormpath.web.accessToken.requestAuthorizer";

    protected static final String ACCOUNT_SAVER = "stormpath.web.authc.saver";

    protected static final String SECURE = "stormpath.web.accessToken.secure";

    protected static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";

    private AccessTokenRequestAuthorizer requestAuthorizer;
    private AccessTokenAuthenticationRequestFactory authenticationRequestFactory;
    private AccessTokenResultFactory resultFactory;
    private Saver<AuthenticationResult> accountSaver;
    private Publisher<RequestEvent> eventPublisher;
    private boolean secure = true;

    public AccessTokenRequestAuthorizer getRequestAuthorizer() {
        return this.requestAuthorizer;
    }

    public AccessTokenAuthenticationRequestFactory getAuthenticationRequestFactory() {
        return this.authenticationRequestFactory;
    }

    public AccessTokenResultFactory getResultFactory() {
        return this.resultFactory;
    }

    public Saver<AuthenticationResult> getAccountSaver() {
        return this.accountSaver;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    @Override
    protected void onInit() throws ServletException {
        Config config = getConfig();
        this.requestAuthorizer = config.getInstance(ACCESS_TOKEN_REQUEST_AUTHORIZER);
        this.authenticationRequestFactory = config.getInstance(ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY);
        this.resultFactory = config.getInstance(ACCESS_TOKEN_RESULT_FACTORY);
        this.accountSaver = config.getInstance(ACCOUNT_SAVER);
        this.eventPublisher = config.getInstance(EVENT_PUBLISHER);

        String val = getConfig().get(SECURE);
        this.secure = Boolean.parseBoolean(val);
    }

    protected void publish(RequestEvent e) {
        getEventPublisher().publish(e);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        String json;

        AuthenticationRequest authcRequest = null;

        try {
            assertAuthorizedAccessTokenRequest(request);

            authcRequest = createTokenAuthenticationRequest(request);

            AuthenticationResult ar;
            try {
                Application app = getApplication(request);
                ar = app.authenticateAccount(authcRequest);
            } catch (ResourceException e) {
                log.debug("Unable to authenticate access token request: " + e.getMessage(), e);
                throw new OauthException(OauthErrorCode.INVALID_CLIENT);
            }

            AccessTokenResult result = createAccessTokenResult(request, response, ar);

            saveResult(request, response, result);

            json = result.getTokenResponse().toJson();

            response.setStatus(HttpServletResponse.SC_OK);

            SuccessfulAuthenticationRequestEvent e = createSuccessEvent(request, response, authcRequest, result);
            publish(e);

        } catch (OauthException e) {

            log.debug("OAuth Access Token request failed.", e);

            json = e.toJson();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            try {
                FailedAuthenticationRequestEvent evt =
                    new DefaultFailedAuthenticationRequestEvent(request, response, authcRequest, e);
                publish(evt);
            } catch (Throwable t) {
                log.warn("Unable to publish failed authentication request event due to exception: {}.  " +
                         "Ignoring and handling original authentication exception {}.", t, e);
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    protected SuccessfulAuthenticationRequestEvent createSuccessEvent(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      AuthenticationRequest authcRequest,
                                                                      AuthenticationResult result) {
        return new DefaultSuccessfulAuthenticationRequestEvent(request, response, authcRequest, result);
    }

    protected void assertAuthorizedAccessTokenRequest(HttpServletRequest request) {

        //POST is required: https://tools.ietf.org/html/rfc6749#section-3.2
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            String msg = "HTTP POST is required.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        //Secure connections are required: https://tools.ietf.org/html/rfc6749#section-3.2
        if (isSecureConnectionRequired(request) && !request.isSecure()) {
            String msg = "A secure HTTPS connection is required for token requests - this is " +
                         "a requirement of the OAuth 2 specification.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        //grant_type is always required for all token requests:
        String grantType = Strings.clean(request.getParameter(GRANT_TYPE_PARAM_NAME));
        if (grantType == null) {
            String msg = "Missing grant_type value.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        getRequestAuthorizer().assertAuthorizedAccessTokenRequest(request);
    }

    //allow localhost development to not require an SSL certificate:
    protected boolean isSecureConnectionRequired(HttpServletRequest request) {

        if (!secure) {
            return false;
        }

        String serverName = request.getServerName();

        boolean localhost = serverName.equalsIgnoreCase("localhost") ||
                            serverName.equals("127.0.0.1") ||
                            serverName.equals("::1") ||
                            serverName.equals("0:0:0:0:0:0:0:1");

        return !localhost;
    }

    protected Application getApplication(HttpServletRequest request) {
        return Servlets.getApplication(request);
    }

    protected AuthenticationRequest createTokenAuthenticationRequest(HttpServletRequest request)
        throws OauthException {
        return getAuthenticationRequestFactory().createAccessTokenAuthenticationRequest(request);
    }

    protected AccessTokenResult createAccessTokenResult(final HttpServletRequest request,
                                                        final HttpServletResponse response,
                                                        final AuthenticationResult result) {
        return getResultFactory().createAccessTokenResult(request, response, result);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAccountSaver().set(request, response, result);
    }
}
