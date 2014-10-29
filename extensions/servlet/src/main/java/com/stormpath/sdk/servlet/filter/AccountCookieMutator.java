/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.CookieConfig;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountCookieMutator extends AccountCookieHandler implements Mutator<AuthenticationResult> {

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {

        String jwt;

        CookieConfig cfg = getAccountCookieConfig(request);

        if (value instanceof AccessTokenResult) {
            jwt = ((AccessTokenResult) value).getTokenResponse().getAccessToken();
        } else {
            Client client = getClient(request);
            String secret = ClientApiKeyResolver.INSTANCE.apply(client).getSecret();

            SessionCookieConfig sessionCookieConfig = request.getServletContext().getSessionCookieConfig();

            int maxAge = cfg.getMaxAge();
            if (maxAge == Integer.MIN_VALUE) { //fall back to session config if any:
                maxAge = sessionCookieConfig.getMaxAge();
            }

            AccountToJwtConverter converter = new AccountToJwtConverter(secret, maxAge);
            jwt = converter.apply(value.getAccount());
        }

        Mutator<String> mutator = new CookieMutator(cfg);
        mutator.set(request, response, jwt);
    }
}
