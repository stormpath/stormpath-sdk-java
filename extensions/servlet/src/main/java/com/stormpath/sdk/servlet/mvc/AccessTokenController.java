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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultFailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.OauthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OauthException;
import com.stormpath.sdk.servlet.http.Saver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC4
 */
public class AccessTokenController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenController.class);

    private RequestAuthorizer requestAuthorizer;
    private AccessTokenAuthenticationRequestFactory authenticationRequestFactory;
    private AccessTokenResultFactory resultFactory;
    private Saver<AuthenticationResult> accountSaver;
    private Publisher<RequestEvent> eventPublisher;

    public RequestAuthorizer getRequestAuthorizer() {
        return requestAuthorizer;
    }

    public void setRequestAuthorizer(RequestAuthorizer requestAuthorizer) {
        this.requestAuthorizer = requestAuthorizer;
    }

    public AccessTokenAuthenticationRequestFactory getAccessTokenAuthenticationRequestFactory() {
        return authenticationRequestFactory;
    }

    public void setAccessTokenAuthenticationRequestFactory(AccessTokenAuthenticationRequestFactory authenticationRequestFactory) {
        this.authenticationRequestFactory = authenticationRequestFactory;
    }

    public AccessTokenResultFactory getAccessTokenResultFactory() {
        return resultFactory;
    }

    public void setAccessTokenResultFactory(AccessTokenResultFactory resultFactory) {
        this.resultFactory = resultFactory;
    }

    public Saver<AuthenticationResult> getAccountSaver() {
        return accountSaver;
    }

    public void setAccountSaver(Saver<AuthenticationResult> accountSaver) {
        this.accountSaver = accountSaver;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void init() {
        Assert.notNull(requestAuthorizer, "requestAuthorizer cannot be null.");
        Assert.notNull(authenticationRequestFactory, "accessTokenAuthenticationRequestFactory cannot be null.");
        Assert.notNull(resultFactory, "accessTokenResultFactory cannot be null.");
        Assert.notNull(accountSaver, "accountSaver cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
    }

    protected void publish(RequestEvent e) {
        getEventPublisher().publish(e);
    }

    protected Application getApplication(HttpServletRequest request) {
        Application application = (Application)request.getAttribute(Application.class.getName());
        Assert.notNull(application, "request must have an application attribute.");
        return application;
    }

    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String json;

        AuthenticationRequest authcRequest = null;

        try {
            assertAuthorized(request, response);

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
        response.setHeader("Content-Length", String.valueOf(json.length()));
        response.getWriter().print(json);
        response.getWriter().flush();

        //we rendered the response directly - no need for a view to be resolved, so return null:
        return null;
    }

    protected SuccessfulAuthenticationRequestEvent createSuccessEvent(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      AuthenticationRequest authcRequest,
                                                                      AuthenticationResult result) {
        return new DefaultSuccessfulAuthenticationRequestEvent(request, response, authcRequest, result);
    }

    protected void assertAuthorized(HttpServletRequest request, HttpServletResponse response)
        throws OauthException {
        getRequestAuthorizer().assertAuthorized(request, response);
    }

    protected AuthenticationRequest createTokenAuthenticationRequest(HttpServletRequest request) throws OauthException {
        return getAccessTokenAuthenticationRequestFactory().createAccessTokenAuthenticationRequest(request);
    }

    protected AccessTokenResult createAccessTokenResult(final HttpServletRequest request,
                                                        final HttpServletResponse response,
                                                        final AuthenticationResult result) {
        return getAccessTokenResultFactory().createAccessTokenResult(request, response, result);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAccountSaver().set(request, response, result);
    }

}
