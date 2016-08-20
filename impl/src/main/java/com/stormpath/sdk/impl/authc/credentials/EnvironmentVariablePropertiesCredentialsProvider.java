/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.impl.api.ApiKeyCredentials;
import com.stormpath.sdk.lang.Strings;

import java.util.Properties;

@SuppressWarnings("Duplicates")
public class EnvironmentVariablePropertiesCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    public EnvironmentVariablePropertiesCredentialsProvider(){
        super(DEFAULT_ID_PROPERTY_NAME, DEFAULT_SECRET_PROPERTY_NAME);
    }

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String value = System.getenv("STORMPATH_API_KEY_ID");
        if (Strings.hasText(value)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, value);
        }

        value = System.getenv("STORMPATH_API_KEY_SECRET");
        if (Strings.hasText(value)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, value);
        }

        return props;
    }
}
