/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.AbstractController;
import com.stormpath.sdk.servlet.mvc.Controller;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC8
 */
public class FacebookResultController extends AbstractController {

    private String loginNextUri;
    private Controller logoutController;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private Publisher<RequestEvent> eventPublisher;

//    private List<SamlResultListener> samlResultListeners = new ArrayList<SamlResultListener>();

    public void setLoginNextUri(String loginNextUri) {
        this.loginNextUri = loginNextUri;
    }

//    public void setLogoutController(Controller logoutController) {
//        this.logoutController = logoutController;
//    }

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

//    public void addSamlResultListener(SamlResultListener resultListener) {
//        Assert.notNull(resultListener, "resultListener cannot be null");
//        samlResultListeners.add(resultListener);
//    }

    public void setLogoutController(Controller logoutController) {
        this.logoutController = logoutController;
    }

    public void init() {
        Assert.hasText(loginNextUri, "loginNextUri must be configured.");
        Assert.notNull(logoutController, "logoutController must be configured.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver must be configured.");
        Assert.notNull(eventPublisher, "request event publisher must be configured.");
    }

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return true;
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    @Override
    protected ViewModel doGet(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String code = ServletUtils.getCleanParam(request, "code");
        ProviderAccountRequest providerRequest = Providers.FACEBOOK.account().setAccessToken(code).build();
        ProviderAccountResult result = getApplication(request).getAccount(providerRequest);
        AuthenticationResult authcResult = new TransientAuthenticationResult(result.getAccount());
        saveResult(request, response, authcResult);

        return new DefaultViewModel(loginNextUri).setRedirect(true);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }
}
