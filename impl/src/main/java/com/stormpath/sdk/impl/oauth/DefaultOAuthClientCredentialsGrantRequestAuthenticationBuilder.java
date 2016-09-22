/*
* Copyright 2016 Stormpath, Inc.
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
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticationBuilder;

/**
 * @since 1.1.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticationBuilder implements OAuthClientCredentialsGrantRequestAuthenticationBuilder {

    private String apiKeyId;
    private String apiKeySecret;

    @Override
    public OAuthClientCredentialsGrantRequestAuthenticationBuilder setApiKeyId(String apiKeyId) {
        Assert.hasText(apiKeyId, "apiKeyId cannot be null or empty.");
        this.apiKeyId = apiKeyId;
        return this;
    }

    @Override
    public OAuthClientCredentialsGrantRequestAuthenticationBuilder setApiKeySecret(String apiKeySecret) {
        Assert.notNull(apiKeySecret, "apiKeySecret cannot be null or empty.");
        this.apiKeySecret = apiKeySecret;
        return this;
    }

    @Override
    public OAuthClientCredentialsGrantRequestAuthentication build() {
        Assert.state(this.apiKeyId != null, "apiKeyId has not been set. It is a required attribute.");
        Assert.state(this.apiKeySecret != null, "apiKeySecret has not been set. It is a required attribute.");

        DefaultOAuthClientCredentialsGrantRequestAuthentication request = new DefaultOAuthClientCredentialsGrantRequestAuthentication(apiKeyId, apiKeySecret);

        return request;
    }
}
