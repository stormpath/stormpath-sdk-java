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
package com.stormpath.sdk.impl.client;

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.*;
import com.stormpath.sdk.lang.Classes;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * The default {@link ClientBuilder} implementation.
 *
 * @since 0.9.4
 */
public class DefaultClientBuilder implements ClientBuilder {

    private ApiKey apiKey;
    private String apiKeyFileLocation;
    private InputStream apiKeyInputStream;
    private Reader apiKeyReader;
    private Properties apiKeyProperties;
    private String apiKeyIdPropertyName = "apiKey.id";
    private String apiKeySecretPropertyName = "apiKey.secret";
    private String baseUrl = "https://api.stormpath.com/v1";
    private Proxy proxy;
    private AuthenticationScheme authenticationScheme;

    private CacheManager cacheManager;

    @Override
    public ClientBuilder setApiKey(String apiKeyId, String apiKeySecret) {
        ApiKey apiKey = new DefaultApiKey(apiKeyId, apiKeySecret);
        return setApiKey(apiKey);
    }

    @Override
    public ClientBuilder setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public ClientBuilder setApiKeyProperties(Properties properties) {
        this.apiKeyProperties = properties;
        return this;
    }

    @Override
    public ClientBuilder setApiKeyReader(Reader reader) {
        this.apiKeyReader = reader;
        return this;
    }

    @Override
    public ClientBuilder setApiKeyInputStream(InputStream is) {
        this.apiKeyInputStream = is;
        return this;
    }

    @Override
    public ClientBuilder setApiKeyFileLocation(String location) {
        this.apiKeyFileLocation = location;
        return this;
    }

    @Override
    public ClientBuilder setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.apiKeyIdPropertyName = apiKeyIdPropertyName;
        return this;
    }

    @Override
    public ClientBuilder setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.apiKeySecretPropertyName = apiKeySecretPropertyName;
        return this;
    }

    @Override
    public ClientBuilder setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        this.proxy = proxy;
        return this;
    }

    @Override
    public ClientBuilder setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }

    @Override
    public ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
        return this;
    }

    @Override
    public Client build() {

        ApiKey apiKey = this.apiKey;

        if (apiKey == null) {
            apiKey = loadApiKey();
        }

        return new DefaultClient(apiKey, this.baseUrl, proxy, cacheManager, authenticationScheme);
    }

    //For internal Stormpath needs only and not intended for public consumption
    public ClientBuilder setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl argument cannot be null.");
        }
        this.baseUrl = baseUrl;
        return this;
    }

    protected ApiKey loadApiKey() {

        Properties properties = loadApiKeyProperties();

        String apiKeyId = getRequiredPropertyValue(properties, this.apiKeyIdPropertyName, "apiKeyId");

        String apiKeySecret = getRequiredPropertyValue(properties, this.apiKeySecretPropertyName, "apiKeySecret");

        return createApiKey(apiKeyId, apiKeySecret);
    }

    protected Properties loadApiKeyProperties() {

        Properties properties = this.apiKeyProperties;

        if (properties == null || properties.isEmpty()) {

            //need to load the properties file:

            Reader reader = getAvailableReader();

            if (reader == null) {
                String msg = "No API Key properties could be found or loaded from a file location.  Please " +
                        "configure the 'apiKeyFileLocation' property or alternatively configure a " +
                        "Properties, Reader or InputStream instance.";
                throw new IllegalArgumentException(msg);
            }

            properties = new Properties();
            try {
                properties.load(reader);
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to load apiKey properties file.", e);
            }
        }

        return properties;
    }

    protected ApiKey createApiKey(String id, String secret) {
        return new DefaultApiKey(id, secret);
    }

    private String getPropertyValue(Properties properties, String propName) {
        String value = properties.getProperty(propName);
        if (value != null) {
            value = value.trim();
            if ("".equals(value)) {
                value = null;
            }
        }
        return value;
    }

    private String getRequiredPropertyValue(Properties props, String propName, String masterName) {
        String value = getPropertyValue(props, propName);
        if (value == null) {
            String msg = "There is no '" + propName + "' property in the " +
                    "configured apiKey properties.  You can either specify that property or " +
                    "configure the " + masterName + "PropertyName value on the ClientBuilder to specify a " +
                    "custom property name.";
            throw new IllegalArgumentException(msg);
        }
        return value;
    }

    private Reader getAvailableReader() {
        if (this.apiKeyReader != null) {
            return this.apiKeyReader;
        }

        InputStream is = this.apiKeyInputStream;

        if (is == null && this.apiKeyFileLocation != null) {
            try {
                is = ResourceUtils.getInputStreamForPath(apiKeyFileLocation);
            } catch (IOException e) {
                String msg = "Unable to load API Key using apiKeyFileLocation '" + this.apiKeyFileLocation + "'.  " +
                        "Please check and ensure that file exists or use the 'setApiKeyFileLocation' method to specify " +
                        "a valid location.";
                throw new IllegalStateException(msg, e);
            }
        }

        if (is != null) {
            return toReader(is);
        }

        //no configured input, just return null to indicate this:
        return null;
    }

    private Reader toReader(InputStream is) {
        try {
            return new InputStreamReader(is, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("ISO-8859-1 character set is not available on the current JVM.  " +
                    "This is required to read a Java-compatible Properties file. ", e);
        }
    }

    private static class ResourceUtils {

        /**
         * Resource path prefix that specifies to load from a classpath location, value is <b>{@code classpath:}</b>
         */
        public static final String CLASSPATH_PREFIX = "classpath:";
        /**
         * Resource path prefix that specifies to load from a url location, value is <b>{@code url:}</b>
         */
        public static final String URL_PREFIX = "url:";
        /**
         * Resource path prefix that specifies to load from a file location, value is <b>{@code file:}</b>
         */
        public static final String FILE_PREFIX = "file:";

        /**
         * Prevent instantiation.
         */
        private ResourceUtils() {
        }

        /**
         * Returns {@code true} if the resource path is not null and starts with one of the recognized
         * resource prefixes ({@link #CLASSPATH_PREFIX CLASSPATH_PREFIX},
         * {@link #URL_PREFIX URL_PREFIX}, or {@link #FILE_PREFIX FILE_PREFIX}), {@code false} otherwise.
         *
         * @param resourcePath the resource path to check
         * @return {@code true} if the resource path is not null and starts with one of the recognized
         *         resource prefixes, {@code false} otherwise.
         */
        @SuppressWarnings({"UnusedDeclaration"})
        public static boolean hasResourcePrefix(String resourcePath) {
            return resourcePath != null &&
                    (resourcePath.startsWith(CLASSPATH_PREFIX) ||
                            resourcePath.startsWith(URL_PREFIX) ||
                            resourcePath.startsWith(FILE_PREFIX));
        }

        /**
         * Returns the InputStream for the resource represented by the specified path, supporting scheme
         * prefixes that direct how to acquire the input stream
         * ({@link #CLASSPATH_PREFIX CLASSPATH_PREFIX},
         * {@link #URL_PREFIX URL_PREFIX}, or {@link #FILE_PREFIX FILE_PREFIX}).  If the path is not prefixed by one
         * of these schemes, the path is assumed to be a file-based path that can be loaded with a
         * {@link FileInputStream FileInputStream}.
         *
         * @param resourcePath the String path representing the resource to obtain.
         * @return the InputStraem for the specified resource.
         * @throws IOException if there is a problem acquiring the resource at the specified path.
         */
        public static InputStream getInputStreamForPath(String resourcePath) throws IOException {

            InputStream is;
            if (resourcePath.startsWith(CLASSPATH_PREFIX)) {
                is = loadFromClassPath(stripPrefix(resourcePath));

            } else if (resourcePath.startsWith(URL_PREFIX)) {
                is = loadFromUrl(stripPrefix(resourcePath));

            } else if (resourcePath.startsWith(FILE_PREFIX)) {
                is = loadFromFile(stripPrefix(resourcePath));

            } else {
                is = loadFromFile(resourcePath);
            }

            if (is == null) {
                throw new IOException("Resource [" + resourcePath + "] could not be found.");
            }

            return is;
        }

        private static InputStream loadFromFile(String path) throws IOException {
            return new FileInputStream(path);
        }

        private static InputStream loadFromUrl(String urlPath) throws IOException {
            URL url = new URL(urlPath);
            return url.openStream();
        }

        private static InputStream loadFromClassPath(String path) {
            return Classes.getResourceAsStream(path);
        }

        private static String stripPrefix(String resourcePath) {
            return resourcePath.substring(resourcePath.indexOf(":") + 1);
        }
    }


}
