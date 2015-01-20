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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationException;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationResult;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class AuthorizationHeaderAccountResolver implements Resolver<Account> {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationHeaderAccountResolver.class);

    private HttpAuthenticator httpAuthenticator;

    public AuthorizationHeaderAccountResolver(HttpAuthenticator authenticator) {
        Assert.notNull(authenticator, "HttpAuthenticator argument cannot be null.");
        this.httpAuthenticator = authenticator;
    }

    @Override
    public Account get(HttpServletRequest request, HttpServletResponse response) {

        String authzHeaderValue = request.getHeader("Authorization");

        if (authzHeaderValue != null) {

            try {

                HttpAuthenticationResult result = httpAuthenticator.authenticate(request, response);

                return result.getAuthenticationResult().getAccount();

            } catch (HttpAuthenticationException e) {

                if (log.isDebugEnabled()) {
                    String msg = "Unable to authenticate HTTP request Authorization header: " + e.getMessage();
                    msg += " The header will be ignored and not be used to identify a request account.";
                    log.debug(msg, e);
                }

                //retain the exception in case any downstream filters want to inspect the cause:
                request.setAttribute(HttpAuthenticationException.class.getName(), e);
            }
        }

        return null;
    }
}
