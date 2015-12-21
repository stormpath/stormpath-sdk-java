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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.Oauth2Requests;
import com.stormpath.sdk.oauth.PasswordGrantRequest;
import com.stormpath.sdk.oauth.PasswordGrantRequestBuilder;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC3
 */
public class DefaultAccessTokenAuthenticationRequestFactory implements AccessTokenAuthenticationRequestFactory {

    protected static final String USERNAME_PARAM_NAME = "username";

    protected static final String PASSWORD_PARAM_NAME = "password";

    protected static final String ACCOUNT_STORE_PARAM_NAME = "accountStore";

    private AccountStoreResolver accountStoreResolver;

    public DefaultAccessTokenAuthenticationRequestFactory(AccountStoreResolver accountStoreResolver) {
        Assert.notNull(accountStoreResolver, "AccountStoreResolver cannot be null.");
        this.accountStoreResolver = accountStoreResolver;
    }

    @Override
    public PasswordGrantRequest createAccessTokenAuthenticationRequest(HttpServletRequest request)
            throws OauthException {

        try {
            String username = Strings.clean(request.getParameter(USERNAME_PARAM_NAME));
            Assert.hasText(username, "username must not be null or empty.");

            String password = Strings.clean(request.getParameter(PASSWORD_PARAM_NAME));
            Assert.hasText(password, "password must not be null or empty.");

            AccountStore accountStore = accountStoreResolver.getAccountStore(request, null);

            PasswordGrantRequestBuilder requestBuilder = Oauth2Requests.PASSWORD_GRANT_REQUEST.builder()
                    .setPassword(password)
                    .setLogin(username);
            if (accountStore != null){
                requestBuilder.setAccountStore(accountStore);
            }

            return requestBuilder.build();
        } catch (Exception e){
            throw new OauthException(OauthErrorCode.INVALID_REQUEST);
        }
    }
}
