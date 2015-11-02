/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.JwtAuthenticationResult;

/**
 * @since 1.0.RC5.1
 */
public class DefaultJwtAuthenticationResult implements JwtAuthenticationResult {

    private final AccessToken accessToken;

    public DefaultJwtAuthenticationResult(DefaultJwtAuthenticationResultBuilder builder) {
        this.accessToken = builder.getAccessToken();
    }

    @Override
    public Account getAccount() {
        return accessToken.getAccount();
    }

    @Override
    public Application getApplication() {
        return accessToken.getApplication();
    }

    @Override
    public String getHref() {
        return accessToken.getHref();
    }

    @Override
    public String getJwt() {
        return accessToken.getJwt();
    }
}
