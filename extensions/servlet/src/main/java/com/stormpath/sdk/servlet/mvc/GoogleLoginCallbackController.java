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
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0
 */
public class GoogleLoginCallbackController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(GoogleLoginCallbackController.class);

    private String nextUri;
    private String logoutUri;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private Publisher<RequestEvent> eventPublisher;

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public String getLogoutUri() {
        return logoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        this.logoutUri = logoutUri;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void init() {
        Assert.hasText(this.nextUri, "nextUri property cannot be null or empty.");
        Assert.hasText(this.logoutUri, "logoutUri property cannot be null or empty.");
        Assert.notNull(this.authenticationResultSaver, "authenticationResultSaver property cannot be null.");
        Assert.notNull(this.eventPublisher, "eventPublisher cannot be null.");
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String code = Strings.clean(request.getParameter("code"));

        //invalid access - safest to logout:
        if (code == null) {
            String logoutUri = getLogoutUri();
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }

        ProviderAccountRequest providerAccountRequest = Providers.GOOGLE.account().setCode(code).build();

        Application application = getApplication(request);

        ProviderAccountResult result;
        Account account;
        boolean newAccount;

        try {
            result = application.getAccount(providerAccountRequest);
            account = result.getAccount();
            newAccount = result.isNewAccount();
        } catch (Exception e) {

            //TODO: fire event?

            //TODO: show an error view?
            log.info("Unable to obtain user account information from Stormpath during a Google login attempt.", e);

            //safest thing to do for now:
            String logoutUri = getLogoutUri();
            return new DefaultViewModel(logoutUri).setRedirect(true);
        }

        if (newAccount) {
            RegisteredAccountRequestEvent e = new DefaultRegisteredAccountRequestEvent(request, response, account);
            this.eventPublisher.publish(e);
        }

        //simulate a result for the benefit of the 'saveResult' method signature:
        AuthenticationResult authcResult = createAuthenticationResult(account);
        saveResult(request, response, authcResult);


        SuccessfulAuthenticationRequestEvent e =
            new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, authcResult);
        this.eventPublisher.publish(e);

        String next = getNextUri();
        return new DefaultViewModel(next).setRedirect(true);
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }

    protected AuthenticationResult createAuthenticationResult(final Account account) {
        return new AuthenticationResult() {
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

    }
}
