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
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.idsite.RegistrationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.IdSiteAuthenticationRequest;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdSiteResultController extends CallbackController {

    private String registerNextUri = null;
    private List<IdSiteResultListener> idSiteResultListeners = new ArrayList<IdSiteResultListener>();

    public void addIdSiteResultListener(IdSiteResultListener resultListener) {
        Assert.notNull(resultListener, "resultListener cannot be null");
        idSiteResultListeners.add(resultListener);
    }

    public void doInit() {
        Assert.notNull(registerNextUri, "registerNextUri must be configured.");
    }

    public void setRegisterNextUri(String registerNextUri) {
        this.registerNextUri = registerNextUri;
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

        IdSiteCallbackHandler idSiteCallbackHandler = app.newIdSiteCallbackHandler(request).setResultListener(new IdSiteResultListener() {
            @Override
            public void onRegistered(RegistrationResult result) {
                viewModel[0] = IdSiteResultController.this.onRegistration(request, response, result);
            }

            @Override
            public void onAuthenticated(com.stormpath.sdk.idsite.AuthenticationResult result) {
                viewModel[0] = IdSiteResultController.this.onAuthentication(request, response, result);
            }

            @Override
            public void onLogout(LogoutResult result) {
                viewModel[0] = IdSiteResultController.this.onLogout(request, response, result);

            }
        });

        for (IdSiteResultListener resultListener : idSiteResultListeners) {
            idSiteCallbackHandler.addResultListener(resultListener);
        }

        idSiteCallbackHandler.getAccountResult();

        return viewModel[0];
    }

    private ViewModel onRegistration(final HttpServletRequest request, final HttpServletResponse response, RegistrationResult result) {

        final Account account = result.getAccount();

        AccountStatus status = account.getStatus();

        RequestEvent e = createRegisteredEvent(request, response, account);
        publish(e);

        if (status == AccountStatus.ENABLED) {
            //the user does not need to verify their email address, so just assume they are authenticated
            //(since they specified their password during registration):
            AuthenticationResult authcResult = new TransientAuthenticationResult(account);
            saveResult(request, response, authcResult);
        }
        // else - do we need to do anything else?

        //just redirect to post-register view:
        return new DefaultViewModel(registerNextUri).setRedirect(true);
    }

    private RegisteredAccountRequestEvent createRegisteredEvent(HttpServletRequest request,
                                                                HttpServletResponse response, Account account) {
        return new DefaultRegisteredAccountRequestEvent(request, response, account);
    }
}
