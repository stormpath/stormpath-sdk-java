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
package com.stormpath.sdk.authc;

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.OAuthAuthenticationResult;

/**
 * AuthenticationResultVisitorAdapter is an implementation of the {@link AuthenticationResultVisitor} that throws {@link
 * UnsupportedOperationException} in all methods by default.
 *
 * <p>The purpose of this adapter is to provide a default implementation that can be adapted to execute the desired
 * behavior in the expected result.</p>
 *
 * <pre>
 *  AuthenticationResult authResult = application.authenticateApiRequest(httpRequest).execute();
 *
 *  authResult.accept(new AuthenticationResultVisitorAdapter() {
 *
 *      &#64;Override
 *      public void visit(ApiAuthenticationResult result) {
 *          Account account = result.getAccount();
 *          ApiKey apiKey = result.getApiKey();
 *          ...
 *      }
 *
 *      &#64;Override
 *      public void visit(OAuthAuthenticationResult result) {
 *          Account account = result.getAccount();
 *          Set<String> scope = result.getScope();
 *          ...
 *      }
 *
 *      &#64;Override
 *      public void visit(AccessTokenResult result) {
 *          TokenResponse tokenResponse = result.getTokenResponse();
 *          ...
 *      }
 *  });
 * </pre>
 *
 * @since 1.0.RC
 */
public class AuthenticationResultVisitorAdapter implements AuthenticationResultVisitor {

    @Override
    public void visit(AuthenticationResult result) {
        throw new UnsupportedOperationException("visit(AuthenticationResult) is not expected.");
    }

    @Override
    public void visit(ApiAuthenticationResult result) {
        throw new UnsupportedOperationException("visit(ApiAuthenticationResult) is not expected.");
    }

    @Override
    public void visit(OAuthAuthenticationResult result) {
        throw new UnsupportedOperationException("visit(OAuthAuthenticationResult) is not expected.");
    }

    @Override
    public void visit(AccessTokenResult result) {
        throw new UnsupportedOperationException("visit(AccessTokenResult) is not expected.");
    }

}
