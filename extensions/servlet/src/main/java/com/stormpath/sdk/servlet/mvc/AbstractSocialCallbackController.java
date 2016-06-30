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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public abstract class AbstractSocialCallbackController extends AbstractController {

    protected Saver<AuthenticationResult> authenticationResultSaver;

    private final Publisher<RequestEvent> requestEventPublisher;

    public AbstractSocialCallbackController(String loginNextUri,
                                            Saver<AuthenticationResult> authenticationResultSaver,
                                            Publisher<RequestEvent> requestEventPublisher) {
        this.nextUri = loginNextUri;
        this.authenticationResultSaver = authenticationResultSaver;
        this.requestEventPublisher = requestEventPublisher;

        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver cannot be null.");
        Assert.hasLength(this.nextUri, "nextUri cannot be null.");
        Assert.notNull(this.requestEventPublisher, "requestEventPublisher cannot be null");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    protected abstract ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request);

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProviderAccountRequest providerRequest = getAccountProviderRequest(request);
        ProviderAccountResult result = getApplication(request).getAccount(providerRequest);
        AuthenticationResult authcResult = new TransientAuthenticationResult(result.getAccount());
        authenticationResultSaver.set(request, response, authcResult);

        if (result.isNewAccount()) {
            this.requestEventPublisher.publish(createRegisteredEvent(request, response, result.getAccount()));
        } else {
            this.requestEventPublisher.publish(createSuccessEvent(request, response, null, authcResult));
        }


        return new DefaultViewModel(nextUri).setRedirect(true);
    }

    protected RegisteredAccountRequestEvent createRegisteredEvent(HttpServletRequest request,
                                                                  HttpServletResponse response, Account account) {
        return new DefaultRegisteredAccountRequestEvent(request, response, account);
    }

    protected SuccessfulAuthenticationRequestEvent createSuccessEvent(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      AuthenticationRequest authenticationRequest,
                                                                      AuthenticationResult authcResult) {
        return new DefaultSuccessfulAuthenticationRequestEvent(request, response, authenticationRequest, authcResult);
    }
}
