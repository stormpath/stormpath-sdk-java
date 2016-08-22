/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.impl.config.ClientConfiguration;
import com.stormpath.sdk.lang.Strings;

import java.util.Properties;

public class ConfigurationCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private ClientConfiguration clientConfiguration;

    public ConfigurationCredentialsProvider(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String keyId = clientConfiguration.getApiKeyId();
        if (Strings.hasText(keyId)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, keyId);
        }

        String secret = clientConfiguration.getApiKeySecret();
        if (Strings.hasText(secret)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, secret);
        }

        return props;
    }

}
