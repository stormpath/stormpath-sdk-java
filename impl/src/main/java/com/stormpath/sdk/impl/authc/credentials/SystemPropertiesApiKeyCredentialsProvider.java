/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.lang.Strings;

import java.util.Properties;

public class SystemPropertiesApiKeyCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final String API_KEY_ID_SYSTEM_PROPERTY = "stormpath.client.apiKey.id";
    private static final String API_KEY_SECRET_SYSTEM_PROPERTY = "stormpath.client.apiKey.secret";

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String value = System.getProperty(API_KEY_ID_SYSTEM_PROPERTY);
        if (Strings.hasText(value)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, value);
        }

        value = System.getProperty(API_KEY_SECRET_SYSTEM_PROPERTY);
        if (Strings.hasText(value)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, value);
        }

        return props;
    }

}