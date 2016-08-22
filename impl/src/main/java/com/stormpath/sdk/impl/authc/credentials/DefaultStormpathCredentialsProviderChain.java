/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.impl.config.ClientConfiguration;
import com.stormpath.sdk.lang.Assert;

public class DefaultStormpathCredentialsProviderChain extends StormpathCredentialsProviderChain {

    public DefaultStormpathCredentialsProviderChain(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");

        addStormpathCredentialProviders(
                new ConfigurationCredentialsProvider(clientConfiguration),
                new ApiKeyFileCredentialsProvider(clientConfiguration.getApiKeyFile()),
                new SystemPropertiesApiKeyCredentialsProvider(),
                new SystemPropertyFileCredentialsProvider(),
                new EnvironmentVariablePropertiesCredentialsProvider(),
                new EnvironmentVariableFileCredentialsProvider(),
                new ApiKeyFileCredentialsProvider()
        );

    }

}
