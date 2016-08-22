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

public class EnvironmentVariableFileCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentVariableFileCredentialsProvider.class);
    private static final String API_KEY_FILE_LOCATION_ENVIRONMENT_VARIABLE = "STORMPATH_API_KEY_FILE";
    private static final String ERROR_MSG = "Unable to load api key properties file [{}] specified by environment variable STORMPATH_API_KEY_FILE. " +
            "This can be safely ignored as this is a fallback location - other more specific locations will be checked.";

    @SuppressWarnings("Duplicates")
    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String location = System.getenv(API_KEY_FILE_LOCATION_ENVIRONMENT_VARIABLE);
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
