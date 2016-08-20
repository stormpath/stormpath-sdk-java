/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.impl.api.ApiKeyCredentials;
import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@SuppressWarnings("Duplicates")
public class SystemPropertiesApiKeyCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String value = System.getProperty("stormpath.client.apiKey.id");
        if (Strings.hasText(value)) {
            props.put(DEFAULT_ID_PROPERTY_NAME, value);
        }

        value = System.getProperty("stormpath.client.apiKey.secret");
        if (Strings.hasText(value)) {
            props.put(DEFAULT_SECRET_PROPERTY_NAME, value);
        }

        return props;
    }

}