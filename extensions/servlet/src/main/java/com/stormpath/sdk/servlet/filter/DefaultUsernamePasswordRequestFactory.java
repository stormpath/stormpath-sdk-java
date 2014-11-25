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

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.authc.AuthenticationAccountStoreResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultUsernamePasswordRequestFactory implements UsernamePasswordRequestFactory {

    private AuthenticationAccountStoreResolver authenticationAccountStoreResolver;

    public DefaultUsernamePasswordRequestFactory(
        AuthenticationAccountStoreResolver authenticationAccountStoreResolver) {
        Assert.notNull(authenticationAccountStoreResolver, "AuthenticationAccountStoreResolver cannot be null.");
        this.authenticationAccountStoreResolver = authenticationAccountStoreResolver;
    }

    protected AuthenticationAccountStoreResolver getAuthenticationAccountStoreResolver() {
        return this.authenticationAccountStoreResolver;
    }

    @Override
    public AuthenticationRequest createUsernamePasswordRequest(HttpServletRequest request, HttpServletResponse response,
                                                               String username, String password) {
        AccountStore accountStore =
            getAuthenticationAccountStoreResolver().getAuthenticationAccountStore(request, response);

        return new UsernamePasswordRequest(username, password, request.getRemoteHost(), accountStore);
    }
}
