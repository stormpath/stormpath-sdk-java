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

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class SystemPropertyFileCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(SystemPropertyFileCredentialsProvider.class);

    public SystemPropertyFileCredentialsProvider(String idPropertyName, String secretPropertyName) {
        super(idPropertyName, secretPropertyName);
    }

    @Override
    protected Properties loadProperties() {
        Properties props = new Properties();

        String location = System.getProperty("stormpath.client.apiKey.file");
        if (Strings.hasText(location)) {
            try {
                Reader reader = createFileReader(location);
                props = toProperties(reader);
            } catch (IOException ignored) {
                log.debug(
                        "Unable to load api key properties file [{}] specified by system property stormpath.client.apiKey.file. " +
                                "This can be safely ignored as this is a fallback location - " +
                                "other more specific locations will be checked.",
                        location, ignored
                );
            }
        }

        return props;
    }
}
