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
