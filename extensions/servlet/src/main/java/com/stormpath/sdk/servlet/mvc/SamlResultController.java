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
public class SamlResultController extends CallbackController {

    private List<SamlResultListener> samlResultListeners = new ArrayList<SamlResultListener>();

    public void doInit() {
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

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }
}
