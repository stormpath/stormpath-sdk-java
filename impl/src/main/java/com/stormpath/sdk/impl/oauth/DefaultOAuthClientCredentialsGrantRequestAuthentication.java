/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthentication implements OAuthClientCredentialsGrantRequestAuthentication {
    private final static String grant_type = "client_credentials";

    private String apiKeyId;
    private String apiKeySecret;

    public DefaultOAuthClientCredentialsGrantRequestAuthentication(String apiKeyId, String apiKeySecret) {
        Assert.hasText(apiKeyId, "apiKeyId cannot be null or empty.");
        Assert.hasText(apiKeySecret, "apiKeySecret cannot be null or empty.");

        this.apiKeyId = apiKeyId;
        this.apiKeySecret = apiKeySecret;
    }

    @Override
    public String getApiKeyId() {
        return apiKeyId;
    }

    @Override
    public String getApiKeySecret() {
        return apiKeySecret;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
