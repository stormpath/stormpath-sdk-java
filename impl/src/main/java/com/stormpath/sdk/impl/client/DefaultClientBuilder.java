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
package com.stormpath.sdk.impl.client;

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.http.support.UserAgent;
import com.stormpath.sdk.lang.Assert;

/**
 * The default {@link ClientBuilder} implementation.
 *
 * @since 1.0.alpha
 */
public class DefaultClientBuilder implements ClientBuilder {

    private com.stormpath.sdk.api.ApiKey apiKey;
    private String baseUrl = "https://api.stormpath.com/v1";
    private Proxy proxy;
    private AuthenticationScheme authenticationScheme;
    private CacheManager cacheManager;
    private String userAgent;

    @Override
    public ClientBuilder setApiKey(ApiKey apiKey) {
        return setApiKey((com.stormpath.sdk.api.ApiKey) apiKey);
    }

    @Override
    public ClientBuilder setApiKey(com.stormpath.sdk.api.ApiKey apiKey) {
        Assert.notNull(apiKey, "apiKey cannot be null.");
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public ClientBuilder setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        this.proxy = proxy;
        return this;
    }

    @Override
    public ClientBuilder setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }

    @Override
    public ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
        return this;
    }

    @Override
    public Client build() {
        Assert.state(this.apiKey != null, "No ApiKey has been set. It is required to properly build the Client. See 'setApiKey(ApiKey)'.");

        return new DefaultClient(this.apiKey, this.baseUrl, this.proxy, this.cacheManager, this.authenticationScheme, userAgent);
    }

    //For internal Stormpath needs only and not intended for public consumption
    public ClientBuilder setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl argument cannot be null.");
        }
        this.baseUrl = baseUrl;
        return this;
    }

    //For internal Stormpath needs only and not intended for public consumption
    public ClientBuilder setUserAgentString(String userAgent) {
        Assert.notNull(userAgent, "userAgent argument cannot be null.");
        this.userAgent = baseUrl;
        return this;
    }

}
