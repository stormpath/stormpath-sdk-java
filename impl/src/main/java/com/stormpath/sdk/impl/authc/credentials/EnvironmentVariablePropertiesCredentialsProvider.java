/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.lang.Strings;

import java.util.Properties;

public class EnvironmentVariablePropertiesCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final String API_KEY_ID_ENVIRONMENT_VARIABLE = "STORMPATH_API_KEY_ID";
    private static final String API_KEY_SECRET_ENVIRONMENT_VARIABLE = "STORMPATH_API_KEY_SECRET";

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String value = System.getenv(API_KEY_ID_ENVIRONMENT_VARIABLE);
        if (Strings.hasText(value)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, value);
        }

        value = System.getenv(API_KEY_SECRET_ENVIRONMENT_VARIABLE);
        if (Strings.hasText(value)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, value);
        }

        return props;
    }
}
