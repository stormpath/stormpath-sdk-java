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
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public abstract class AbstractSocialCallbackController extends AbstractController {

    protected Saver<AuthenticationResult> authenticationResultSaver;

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    @Override
    public void init() throws Exception {
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        Assert.notNull(applicationResolver, "applicationResolver cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected abstract ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request);

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProviderAccountRequest providerRequest = getAccountProviderRequest(request);
        ProviderAccountResult result = getApplication(request).getAccount(providerRequest);

        // 751: Check if account is unverified and redirect to verifyUri if true
        Account account = result.getAccount();
        if (account.getStatus().equals(AccountStatus.UNVERIFIED)) {
            Config config = (Config) request.getServletContext().getAttribute(Config.class.getName());
            String loginUri = config.getLoginConfig().getUri();
            return new DefaultViewModel(loginUri + "?status=unverified").setRedirect(true);
        }

        AuthenticationResult authcResult = new TransientAuthenticationResult(result.getAccount());
        authenticationResultSaver.set(request, response, authcResult);

        eventPublisher.publish(new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, authcResult));

        return new DefaultViewModel(nextUri).setRedirect(true);
    }
}
