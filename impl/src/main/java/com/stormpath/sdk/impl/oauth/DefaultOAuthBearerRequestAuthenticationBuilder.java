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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticationBuilder;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthBearerRequestAuthenticationBuilder implements OAuthBearerRequestAuthenticationBuilder {

    private String jwt;

    public String getJwt() {
        return jwt;
    }

    @Override
    public OAuthBearerRequestAuthenticationBuilder setJwt(String jwt) {
        Assert.notNull(jwt, "jwt is a required value and must not be null or empty.");
        this.jwt = jwt;
        return this;
    }

    @Override
    public OAuthBearerRequestAuthentication build() {
        Assert.notNull(jwt, "jwt has not been set. It is a required value.");
        return new DefaultOAuthBearerRequestAuthentication(this.jwt);
    }
}
