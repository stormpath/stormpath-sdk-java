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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.http.CookieMutator;
import com.stormpath.sdk.servlet.http.Mutator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountCookieMutator extends AccountCookieHandler implements Mutator<AuthenticationResult> {

    public static final Mutator<AuthenticationResult> INSTANCE = new AccountCookieMutator();

    protected AccountJwtFactory getAccountJwtFactory(HttpServletRequest request) {
        String accountJwtFactoryClassName = getConfig(request).get("stormpath.web.account.jwt.factory");
        return Classes.newInstance(accountJwtFactoryClassName);
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {

        String jwt;

        if (value instanceof AccessTokenResult) {
            jwt = ((AccessTokenResult) value).getTokenResponse().getAccessToken();
        } else {
            jwt = getAccountJwtFactory(request).createAccountJwt(request, response, value.getAccount());
        }

        CookieConfig cfg = getAccountCookieConfig(request);
        Mutator<String> mutator = new CookieMutator(cfg);
        mutator.set(request, response, jwt);
    }
}
