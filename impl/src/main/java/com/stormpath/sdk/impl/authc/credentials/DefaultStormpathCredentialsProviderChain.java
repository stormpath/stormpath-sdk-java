/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.impl.config.ClientConfiguration;
import com.stormpath.sdk.lang.Assert;

public class DefaultStormpathCredentialsProviderChain extends StormpathCredentialsProviderChain {

    public DefaultStormpathCredentialsProviderChain(ClientConfiguration clientConfiguration) {
        Assert.notNull(clientConfiguration, "clientConfiguration must not be null.");

        String idPropertyName = null;
        String secretPropertyName = null;

        addStormpathCredentialProviders(new SystemPropertiesApiKeyCredentialsProvider(),
                new SystemPropertyFileCredentialsProvider(idPropertyName, secretPropertyName),
                new EnvironmentVariablePropertiesCredentialsProvider(),
                new EnvironmentVariableFileCredentialsProvider(idPropertyName, secretPropertyName),
        new ApiKeyFileCredentialsProvider(idPropertyName, secretPropertyName, clientConfiguration.getApiKeyFile())
        );
    }

    /*@Override
    public StormpathCredentials getStormpathCredentials() {

        this.apiKey = ApiKeys.builder().build();

        // use client.apiKey.file, client.apiKey.id, and client.apiKey.secret if they're set
        if (this.clientConfig.getApiKeyFile() != null || this.clientConfig.getApiKeyId() != null || this.clientConfig.getApiKeySecret() != null) {
            ApiKeyBuilder apiKeyBuilder = ApiKeys.builder();
            if (this.clientConfig.getApiKeyFile() != null) {
                apiKeyBuilder.setFileLocation(this.clientConfig.getApiKeyFile());
            }
            if (this.clientConfig.getApiKeyId() != null) {
                apiKeyBuilder.setId(this.clientConfig.getApiKeyId());
            }
            if (this.clientConfig.getApiKeySecret() != null) {
                apiKeyBuilder.setSecret(this.clientConfig.getApiKeySecret());
            }
            this.apiKey = apiKeyBuilder.build();
        }

        return new ApiKeyCredentials(this.apiKey);

        return null;

    }*/
}
