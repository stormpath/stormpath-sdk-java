/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.impl.api.ClientApiKey;
import com.stormpath.sdk.impl.io.DefaultResourceFactory;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.lang.Strings;

import java.io.*;
import java.util.Properties;

public abstract class AbstractApiKeyCredentialsProvider implements ClientCredentialsProvider {

    public static final String DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION =
            System.getProperty("user.home") + File.separatorChar + ".stormpath" + File.separatorChar + "apiKey.properties";

    public static final String DEFAULT_ID_PROPERTY_NAME = "apiKey.id";
    public static final String DEFAULT_SECRET_PROPERTY_NAME = "apiKey.secret";

    private ResourceFactory resourceFactory = new DefaultResourceFactory();

    public ClientCredentials getClientCredentials() {

        Properties props = loadProperties();

        String id = getPropertyValue(props, DEFAULT_ID_PROPERTY_NAME);
        String secret = getPropertyValue(props, DEFAULT_SECRET_PROPERTY_NAME);

        ApiKey apiKey = createApiKey(id, secret);

        return new ApiKeyCredentials(apiKey);
    }

    protected abstract Properties loadProperties();

    protected ApiKey createApiKey(String id, String secret) {

        if (!Strings.hasText(id)) {
            String msg = "Unable to find an API Key 'id', either from explicit configuration (for example, " +
                    ApiKeyBuilder.class.getSimpleName() + ".setApiKeyId) or from fallback locations:\n\n" +
                    "1) system property stormpath.client.apiKey.id\n" +
                    "2) resource file path or URL specified by system property stormpath.client.apiKey.file\n" +
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
                    "1) system property stormpath.client.apiKey.secret\n" +
                    "2) resource file path or URL specified by system property stormpath.client.apiKey.file\n" +
                    "3) resource file path or URL specified by environment variable STORMPATH_API_KEY_FILE\n" +
                    "4) environment variable STORMPATH_API_KEY_SECRET\n" +
                    "5) default apiKey.properties file location " + DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION +
                    ".\n\n" +
                    "Please ensure you manually configure an API Key Secret or ensure that it exists in one of " +
                    "these fallback locations.";
            throw new IllegalStateException(msg);
        }

        return new ClientApiKey(id, secret);
    }


    protected Reader createFileReader(String apiKeyFileLocation) throws IOException {
        InputStream is = this.resourceFactory.createResource(apiKeyFileLocation).getInputStream();
        return toReader(is);
    }

    private static Reader toReader(InputStream is) throws IOException {
        return new InputStreamReader(is, "ISO-8859-1");
    }

    protected static Properties toProperties(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    protected static String getPropertyValue(Properties properties, String propName) {

        String value = properties.getProperty(propName);
        if (value != null) {
            value = value.trim();
            if ("".equals(value)) {
                value = null;
            }
        }
        return value;
    }

}
