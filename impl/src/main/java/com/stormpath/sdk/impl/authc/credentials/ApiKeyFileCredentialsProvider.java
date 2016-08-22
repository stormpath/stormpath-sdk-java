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

public class ApiKeyFileCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFileCredentialsProvider.class);
    private static final String ERROR_MSG = "Unable to find or load default api key properties file [{}]. " +
            "This can be safely ignored as this is a fallback location - " +
            "other more specific locations will be checked.";

    private final String apiKeyPropertiesFileLocation;

    public ApiKeyFileCredentialsProvider() {
        this(DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION);
    }

    public ApiKeyFileCredentialsProvider(String apiKeyPropertiesFileLocation) {
        this.apiKeyPropertiesFileLocation = apiKeyPropertiesFileLocation;
    }

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        if (Strings.hasText(apiKeyPropertiesFileLocation)) {
            try {
                Reader reader = createFileReader(this.apiKeyPropertiesFileLocation);
                props = toProperties(reader);
            } catch (IOException ignored) {
                log.debug(
                        ERROR_MSG,
                        this.apiKeyPropertiesFileLocation, ignored
                );
            }
        }

        return props;
    }

}
