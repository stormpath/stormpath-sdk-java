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
import com.stormpath.sdk.oauth.IdSiteAuthenticationRequest;
import com.stormpath.sdk.oauth.IdSiteAuthenticationRequestBuilder;

/**
 * @since 1.0.RC7
 */
public class DefaultIdSiteAuthenticationRequestBuilder implements IdSiteAuthenticationRequestBuilder {

    private String token;

    @Override
    public IdSiteAuthenticationRequestBuilder setToken(String token) {
        Assert.hasText(token, "token cannot be null or empty.");
        this.token = token;
        return this;
    }

    @Override
    public IdSiteAuthenticationRequest build() {
        Assert.state(this.token != null, "token has not been set. It is a required attribute.");
        IdSiteAuthenticationRequest request = new DefaultIdSiteAuthenticationRequest(token);
        return request;
    }
}
