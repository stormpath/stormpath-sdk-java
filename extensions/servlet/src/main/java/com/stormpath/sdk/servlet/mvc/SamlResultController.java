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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.RC8
 */
public class SamlResultController extends AbstractController {

    private String loginNextUri;
    private Controller logoutController;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private Publisher<RequestEvent> eventPublisher;

    private List<SamlResultListener> samlResultListeners = new ArrayList<SamlResultListener>();

    public void setLoginNextUri(String loginNextUri) {
        this.loginNextUri = loginNextUri;
    }

    public void setLogoutController(Controller logoutController) {
        this.logoutController = logoutController;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public void addSamlResultListener(SamlResultListener resultListener) {
        Assert.notNull(resultListener, "resultListener cannot be null");
        samlResultListeners.add(resultListener);
    }

    public void init() {
        Assert.hasText(loginNextUri, "loginNextUri must be configured.");
        Assert.notNull(logoutController, "logoutController must be configured.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver must be configured.");
        Assert.notNull(eventPublisher, "request event publisher must be configured.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    @Override
    protected ViewModel doGet(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final Application app = getApplication(request);

        final ViewModel[] viewModel = new ViewModel[1];

        SamlCallbackHandler samlCallbackHandler = app.newSamlCallbackHandler(request).setResultListener(new SamlResultListener() {
            @Override
            public void onAuthenticated(com.stormpath.sdk.idsite.AuthenticationResult result) {
                viewModel[0] = SamlResultController.this.onAuthentication(request, response, app, result);
            }

            @Override
            public void onLogout(LogoutResult result) {
                viewModel[0] = SamlResultController.this.onLogout(request, response, app, result);

            }
        });

        for (SamlResultListener resultListener : samlResultListeners) {
            samlCallbackHandler.addResultListener(resultListener);
        }

        samlCallbackHandler.getAccountResult();

        return viewModel[0];
    }

    protected ViewModel onAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         Application application,
                                         com.stormpath.sdk.idsite.AuthenticationResult result) {

        //simulate a result for the benefit of the 'saveResult' method signature:
        AuthenticationResult authcResult = new TransientAuthenticationResult(result.getAccount());
        saveResult(request, response, authcResult);

        return new DefaultViewModel(loginNextUri).setRedirect(true);
    }

    protected ViewModel onLogout(HttpServletRequest request, HttpServletResponse response, Application application,
                                 LogoutResult result) {

        //let the IdSiteLogoutController know this is a reply from ID site and to not redirect to ID site again:
        request.setAttribute(LogoutResult.class.getName(), result);

        try {
            return logoutController.handleRequest(request, response);
        } catch (Exception e) {
            String msg = "Unable to successfully handle logout: " + e.getMessage();
            throw new RuntimeException(msg, e);
        }
    }

    protected RegisteredAccountRequestEvent createRegisteredEvent(HttpServletRequest request,
                                                                  HttpServletResponse response, Account account) {
        return new DefaultRegisteredAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) {
        try {
            getEventPublisher().publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish registered account request event: " + ex.getMessage();
            throw new RuntimeException(msg, ex);
        }
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }
}
