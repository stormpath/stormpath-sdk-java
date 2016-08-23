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