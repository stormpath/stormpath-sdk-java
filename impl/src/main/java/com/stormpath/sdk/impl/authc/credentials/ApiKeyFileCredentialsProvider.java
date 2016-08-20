/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.impl.api.ApiKeyCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class ApiKeyFileCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFileCredentialsProvider.class);

    private final String apiKeyPropertiesFileLocation;

    public ApiKeyFileCredentialsProvider(String idPropertyName, String secretPropertyName, String apiKeyPropertiesFileLocation) {
        super(idPropertyName, secretPropertyName);
        this.apiKeyPropertiesFileLocation = (apiKeyPropertiesFileLocation != null)
                ? apiKeyPropertiesFileLocation
                : DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION;
    }

    @Override
    protected Properties loadProperties() {
        Properties props = loadPropertiesFromFile(apiKeyPropertiesFileLocation);

        if (!(props.containsKey(this.idPropertyName) && props.containsKey(secretPropertyName))
                && !apiKeyPropertiesFileLocation.equals(DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION)) {
            props = loadPropertiesFromFile(DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION);
        }

        return props;
    }

    private Properties loadPropertiesFromFile(String fileName) {
        Properties props = new Properties();

        try {
            Reader reader = createFileReader(fileName);
            props = toProperties(reader);
        } catch (IOException ignored) {
            log.debug(
                    "Unable to find or load default api key properties file [{}]. " +
                            "This can be safely ignored as this is a fallback location - " +
                            "other more specific locations will be checked.",
                    fileName, ignored
            );
        }
        return props;
    }
}
