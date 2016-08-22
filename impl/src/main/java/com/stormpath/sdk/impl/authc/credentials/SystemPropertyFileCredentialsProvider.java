/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class SystemPropertyFileCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(SystemPropertyFileCredentialsProvider.class);
    private static final String API_KEY_FILE_LOCATION_SYSTEM_PROPERTY = "stormpath.client.apiKey.file";
    private static final String ERROR_MSG = "Unable to load api key properties file [{}] specified by system property stormpath.client.apiKey.file. " +
            "This can be safely ignored as this is a fallback location - " +
            "other more specific locations will be checked.";

    @SuppressWarnings("Duplicates")
    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String location = System.getProperty(API_KEY_FILE_LOCATION_SYSTEM_PROPERTY);
        if (Strings.hasText(location)) {
            try {
                Reader reader = createFileReader(location);
                props = toProperties(reader);
            } catch (IOException ignored) {
                log.debug(
                        ERROR_MSG,
                        location, ignored
                );
            }
        }

        return props;
    }
}
