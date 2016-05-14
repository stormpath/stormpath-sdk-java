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
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticationBuilder;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC3
 */
public class DefaultClientCredentialsAuthenticationRequestFactory implements ClientCredentialsAuthenticationRequestFactory {

    protected static final String API_KEY_ID_PARAM_NAME = "apiKeyId";

    protected static final String API_KEY_SECRET_PARAM_NAME = "apiKeySecret";

    private AccountStoreResolver accountStoreResolver;

    public DefaultClientCredentialsAuthenticationRequestFactory(AccountStoreResolver accountStoreResolver) {
        Assert.notNull(accountStoreResolver, "AccountStoreResolver cannot be null.");
        this.accountStoreResolver = accountStoreResolver;
    }

    @Override
    public OAuthClientCredentialsGrantRequestAuthentication createClientCredentialsAuthenticationRequest(HttpServletRequest request)
            throws OAuthException {

        try {
            String apiKeyId = Strings.clean(request.getParameter(API_KEY_ID_PARAM_NAME));
            Assert.hasText(apiKeyId, "apiKeyId must not be null or empty.");

            String apiKeySecret = Strings.clean(request.getParameter(API_KEY_SECRET_PARAM_NAME));
            Assert.hasText(apiKeySecret, "apiKeySecret must not be null or empty.");

            AccountStore accountStore = accountStoreResolver.getAccountStore(request, null);

            OAuthClientCredentialsGrantRequestAuthenticationBuilder requestBuilder = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
                    .setApiKeyId(apiKeyId)
                    .setApiKeySecret(apiKeySecret);
            if (accountStore != null){
                requestBuilder.setAccountStore(accountStore);
            }

            return requestBuilder.build();
        } catch (Exception e){
            throw new OAuthException(OAuthErrorCode.INVALID_REQUEST);
        }
    }
}