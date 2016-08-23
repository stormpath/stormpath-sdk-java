/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.lang.Assert;

public class ApiKeyCredentials implements ClientCredentials {

    private ApiKey apiKey;

    public ApiKeyCredentials(ApiKey apiKey) {
        Assert.notNull(apiKey);
        this.apiKey = apiKey;
    }

    @Override
    public String getId() {
        return apiKey.getId();
    }

    @Override
    public String getSecret() {
        return apiKey.getSecret();
    }

    public ApiKey getApiKey() {
        return apiKey;
    }
}
