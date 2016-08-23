/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.impl.config.ClientConfiguration;
import com.stormpath.sdk.lang.Assert;

public class DefaultClientCredentialsProviderChain extends ClientCredentialsProviderChain {

    public DefaultClientCredentialsProviderChain(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");

        addClientCredentialsProviders(
                new ConfigurationCredentialsProvider(clientConfiguration),
                new ApiKeyFileCredentialsProvider(clientConfiguration.getApiKeyFile()),
                new SystemPropertiesApiKeyCredentialsProvider(),
                new SystemPropertyFileCredentialsProvider(),
                new EnvironmentVariableCredentialsProvider(),
                new EnvironmentVariableFileCredentialsProvider(),
                new ApiKeyFileCredentialsProvider()
        );

    }

}
