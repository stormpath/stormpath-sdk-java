/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * @since 1.0.4
 */
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
