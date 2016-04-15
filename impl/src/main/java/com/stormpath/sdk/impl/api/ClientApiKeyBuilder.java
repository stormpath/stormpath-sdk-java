/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.api;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.impl.io.DefaultResourceFactory;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/** @since 1.0.RC */
public class ClientApiKeyBuilder implements ApiKeyBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClientApiKeyBuilder.class);

    public static final String DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION =
        System.getProperty("user.home") + File.separatorChar + ".stormpath" + File.separatorChar + "apiKey.properties";

    //private ApiKey apiKey;
    private String      apiKeyId;
    private String      apiKeySecret;
    private String      apiKeyFileLocation;
    private InputStream apiKeyInputStream;
    private Reader      apiKeyReader;
    private Properties  apiKeyProperties;
    private String apiKeyIdPropertyName     = DEFAULT_ID_PROPERTY_NAME;
    private String apiKeySecretPropertyName = DEFAULT_SECRET_PROPERTY_NAME;
    private ResourceFactory resourceFactory = new DefaultResourceFactory();

    @Override
    public ApiKeyBuilder setId(String id) {
        this.apiKeyId = id;
        return this;
    }

    @Override
    public ApiKeyBuilder setSecret(String secret) {
        this.apiKeySecret = secret;
        return this;
    }

    @Override
    public ApiKeyBuilder setProperties(Properties properties) {
        this.apiKeyProperties = properties;
        return this;
    }

    @Override
    public ApiKeyBuilder setReader(Reader reader) {
        this.apiKeyReader = reader;
        return this;
    }

    @Override
    public ApiKeyBuilder setInputStream(InputStream is) {
        this.apiKeyInputStream = is;
        return this;
    }

    @Override
    public ApiKeyBuilder setFileLocation(String location) {
        this.apiKeyFileLocation = location;
        return this;
    }

    @Override
    public ApiKeyBuilder setIdPropertyName(String idPropertyName) {
        this.apiKeyIdPropertyName = idPropertyName;
        return this;
    }

    @Override
    public ApiKeyBuilder setSecretPropertyName(String secretPropertyName) {
        this.apiKeySecretPropertyName = secretPropertyName;
        return this;
    }

    protected Properties getDefaultApiKeyFileProperties() {
        Properties props = new Properties();

        try {
            Reader reader = createFileReader(DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION);
            props = toProperties(reader);
        } catch (IOException ignored) {
            log.debug(
                "Unable to find or load default api key properties file [{}]. This can be safely ignored as this is a fallback location - other more specific locations will be checked.",
                DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION, ignored
            );
        }
        return props;
    }

    protected Properties getEnvironmentVariableFileProperties() {
        Properties props = new Properties();

        String location = System.getenv("STORMPATH_API_KEY_FILE");
        if (Strings.hasText(location)) {
            try {
                Reader reader = createFileReader(location);
                props = toProperties(reader);
            } catch (IOException ignored) {
                log.debug(
                    "Unable to load api key properties file [{}] specified by environment variable STORMPATH_API_KEY_FILE. This can be safely ignored as this is a fallback location - other more specific locations will be checked.",
                    location, ignored
                );
            }
        }

        return props;
    }

    protected Properties getSystemPropertyFileProperties() {
        Properties props = new Properties();

        String location = System.getProperty("stormpath.apiKey.file");
        if (Strings.hasText(location)) {
            try {
                Reader reader = createFileReader(location);
                props = toProperties(reader);
            } catch (IOException ignored) {
                log.debug(
                    "Unable to load api key properties file [{}] specified by system property stormpath.apiKey.file. This can be safely ignored as this is a fallback location - other more specific locations will be checked.",
                    location, ignored
                );
            }
        }

        return props;
    }

    protected Properties getEnvironmentVariableProperties() {
        Properties props = new Properties();

        String value = System.getenv("STORMPATH_API_KEY_ID");
        if (Strings.hasText(value)) {
            props.put(this.apiKeyIdPropertyName, value);
        }

        value = System.getenv("STORMPATH_API_KEY_SECRET");
        if (Strings.hasText(value)) {
            props.put(this.apiKeySecretPropertyName, value);
        }

        return props;
    }

    protected Properties getSystemProperties() {
        Properties props = new Properties();

        String value = System.getProperty("stormpath.apiKey.id");
        if (Strings.hasText(value)) {
            props.put(this.apiKeyIdPropertyName, value);
        }

        value = System.getProperty("stormpath.apiKey.secret");
        if (Strings.hasText(value)) {
            props.put(this.apiKeySecretPropertyName, value);
        }

        return props;
    }

    @Override
    public ApiKey build() {

        //Issue 82 heuristics (see: https://github.com/stormpath/stormpath-sdk-java/labels/enhancement)

        //1. Try to load the default api key properties file.  All other config options have higher priority than this:
        Properties props = getDefaultApiKeyFileProperties();

        String id = getPropertyValue(props, this.apiKeyIdPropertyName);
        String secret = getPropertyValue(props, this.apiKeySecretPropertyName);

        //2. Try environment variable file:
        props = getEnvironmentVariableFileProperties();
        id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
        secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);

        //3. Try environment variables:
        props = getEnvironmentVariableProperties();
        id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
        secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);

        //4. Try system property file:
        props = getSystemPropertyFileProperties();
        id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
        secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);

        //5. Try system properties:
        props = getSystemProperties();
        id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
        secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);

        //6. Try any configured properties files:
        if (Strings.hasText(this.apiKeyFileLocation)) {
            try {
                Reader reader = createFileReader(this.apiKeyFileLocation);
                props = toProperties(reader);
            } catch (IOException e) {
                String msg = "Unable to read properties from specified apiKeyFileLocation [" + this.apiKeyFileLocation + "].";
                throw new IllegalArgumentException(msg, e);
            }

            id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
            secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);
        }

        //7.
        if (this.apiKeyInputStream != null) {
            try {
                Reader reader = toReader(this.apiKeyInputStream);
                props = toProperties(reader);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to read properties from specified apiKeyInputStream.", e);
            }

            id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
            secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);
        }

        //8.
        if (this.apiKeyReader != null) {
            try {
                props = toProperties(this.apiKeyReader);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to read properties from specified apiKeyReader.", e);
            }

            id = getPropertyValue(props, this.apiKeyIdPropertyName, id);
            secret = getPropertyValue(props, this.apiKeySecretPropertyName, secret);
        }

        //9.
        if (this.apiKeyProperties != null && !this.apiKeyProperties.isEmpty()) {
            id = getPropertyValue(this.apiKeyProperties, this.apiKeyIdPropertyName, id);
            secret = getPropertyValue(this.apiKeyProperties, this.apiKeySecretPropertyName, secret);
        }

        //10. Explicitly-configured values always take precedence:
        id = valueOf(this.apiKeyId, id);
        secret = valueOf(this.apiKeySecret, secret);

        if (!Strings.hasText(id)) {
            String msg = "Unable to find an API Key 'id', either from explicit configuration (for example, " +
                         ApiKeyBuilder.class.getSimpleName() + ".setApiKeyId) or from fallback locations:\n\n" +
                         "1) system property stormpath.apiKey.id\n" +
                         "2) resource file path or URL specified by system property stormpath.apiKey.file\n" +
                         "3) resource file path or URL specified by environment variable STORMPATH_API_KEY_FILE\n" +
                         "4) environment variable STORMPATH_API_KEY_ID\n" +
                         "5) default apiKey.properties file location " + DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION +
                         ".\n\n" +
                         "Please ensure you manually configure an API Key ID or ensure that it exists in one of these " +
                         "fallback locations.";
            throw new IllegalStateException(msg);
        }

        if (!Strings.hasText(secret)) {
            String msg = "Unable to find an API Key 'secret', either from explicit configuration (for example, " +
                         ApiKeyBuilder.class.getSimpleName() + ".setApiKeySecret) or from fallback locations:\n\n" +
                         "1) system property stormpath.apiKey.secret\n" +
                         "2) resource file path or URL specified by system property stormpath.apiKey.file\n" +
                         "3) resource file path or URL specified by environment variable STORMPATH_API_KEY_FILE\n" +
                         "4) environment variable STORMPATH_API_KEY_SECRET\n" +
                         "5) default apiKey.properties file location " + DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION +
                         ".\n\n" +
                         "Please ensure you manually configure an API Key Secret or ensure that it exists in one of " +
                         "these fallback locations.";
            throw new IllegalStateException(msg);
        }

        return createApiKey(id, secret);
    }

    //since 0.5
    protected ApiKey createApiKey(String id, String secret) {
        return new ClientApiKey(id, secret);
    }

    private static String getPropertyValue(Properties properties, String propName) {
        String value = properties.getProperty(propName);
        if (value != null) {
            value = value.trim();
            if ("".equals(value)) {
                value = null;
            }
        }
        return value;
    }

    private static String valueOf(String discoveredValue, String defaultValue) {
        if (!Strings.hasText(discoveredValue)) {
            return defaultValue;
        }
        return discoveredValue;

    }

    private static String getPropertyValue(Properties properties, String propName, String defaultValue) {
        String value = getPropertyValue(properties, propName);
        return valueOf(value, defaultValue);
    }

    protected Reader createFileReader(String apiKeyFileLocation) throws IOException {
        InputStream is = this.resourceFactory.createResource(apiKeyFileLocation).getInputStream();
        return toReader(is);
    }

    private static Reader toReader(InputStream is) throws IOException {
        return new InputStreamReader(is, "ISO-8859-1");
    }

    private static Properties toProperties(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

}
