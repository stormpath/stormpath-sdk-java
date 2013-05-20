/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.client;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> implementation used to
 * construct {@link Client} instances.
 * <p/>
 * The {@code ClientBuilder} is especially useful for constructing Client instances with Stormpath API Key
 * information loaded from an external {@code .properties} file (or Properties instance) to ensure the API Key secret
 * (password) does not reside in plaintext in code.
 * <p/>
 * Example usage:
 * <pre>
 * String location = "/home/jsmith/.stormpath/apiKey.properties";
 *
 * Client = new ClientBuilder().setApiKeyFileLocation(location).build();
 * </pre>
 * <p/>
 * You may load files from the filesystem, classpath, or URLs by prefixing the path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  See
 * {@link #setApiKeyFileLocation(String)} for more information.
 *
 * @see #setApiKeyFileLocation(String)
 * @see ClientApplicationBuilder
 *
 * @since 0.3
 */
public class ClientBuilder {

    private String apiKeyFileLocation;
    private InputStream apiKeyInputStream;
    private Reader apiKeyReader;
    private Properties apiKeyProperties;
    private String apiKeyIdPropertyName = "apiKey.id";
    private String apiKeySecretPropertyName = "apiKey.secret";
    private String baseUrl; //internal/private testing only
    private Proxy proxy = Proxy.NO_PROXY;

    /**
     * Constructs a new {@code ClientBuilder} instance, ready to be configured via various {@code set}ter methods.
     */
    public ClientBuilder() {
    }

    /**
     * Allows usage of a Properties instance instead of loading a {@code .properties} file via
     * {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeyProperties(Properties properties) {
        this.apiKeyProperties = properties;
        return this;
    }

    /**
     * Creates an API Key Properties instance based on the specified Reader instead of loading a {@code .properties}
     * file via  {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     * @param reader the reader to use to construct a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeyReader(Reader reader) {
        this.apiKeyReader = reader;
        return this;
    }

    /**
     * Creates an API Key Properties instance based on the specified InputStream
     * instead of loading a {@code .properties} file via
     * {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     * @param is the InputStream to use to construct a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeyInputStream(InputStream is) {
        this.apiKeyInputStream = is;
        return this;
    }

    /**
     * Sets the location of the {@code .properties} file to load containing the API Key (Id and secret) used by the
     * Client to communicate with the Stormpath REST API.
     * <p/>
     * You may load files from the filesystem, classpath, or URLs by prefixing the location path with
     * {@code file:}, {@code classpath:}, or {@code url:} respectively.  If no prefix is found, {@code file:}
     * is assumed by default.
     * <h3>File Contents</h3>
     * <p/>
     * When the file is loaded, the following name/value pairs are expected to be present by default:
     * <table>
     *     <tr>
     *         <th>Key</th>
     *         <th>Value</th>
     *     </tr>
     *     <tr>
     *         <td>apiKey.id</td>
     *         <td>An individual account's API Key ID</td>
     *     </tr>
     *     <tr>
     *         <td>apiKey.secret</td>
     *         <td>The API Key Secret (password) that verifies the paired API Key ID.</td>
     *     </tr>
     * </table>
     * <p/>
     * Assuming you were using these default property names, your {@code ClientBuilder} usage might look like the
     * following:
     * <pre>
     * String location = "/home/jsmith/.stormpath/apiKey.properties";
     *
     * Client = new ClientBuilder().setApiKeyFileLocation(location).build();
     * </pre>
     * <h3>Custom Property Names</h3>
     * If you want to control the property names used in the file, you may configure them via
     * {@link #setApiKeyIdPropertyName(String) setApiKeyIdPropertyName} and
     * {@link #setApiKeySecretPropertyName(String) setApiKeySecretPropertyName}.
     * <p/>
     * For example, if you had a {@code /home/jsmith/.stormpath/apiKey.properties} file with the following
     * name/value pairs:
     * <pre>
     * myStormpathApiKeyId = foo
     * myStormpathApiKeySecret = mySuperSecretValue
     * </pre>
     * Your {@code ClientBuilder} usage would look like the following:
     * <pre>
     * String location = "/home/jsmith/.stormpath/apiKey.properties";
     *
     * Client client =
     *     new ClientBuilder()
     *     .setApiKeyFileLocation(location)
     *     .setApiKeyIdPropertyName("myStormpathApiKeyId")
     *     .setApiKeySecretPropertyName("myStormpathApiKeySecret")
     *     .build();
     * </pre>
     *
     * @param location the file, classpath or url location of the API Key {@code .properties} file to load when
     *                 constructing the API Key to use for communicating with the Stormpath REST API.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeyFileLocation(String location) {
        this.apiKeyFileLocation = location;
        return this;
    }

    /**
     * Sets the name used to query for the API Key ID from a Properties instance.  That is:
     * <pre>
     * String apiKeyId = properties.getProperty(<b>apiKeyIdPropertyName</b>);
     * </pre>
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.apiKeyIdPropertyName = apiKeyIdPropertyName;
        return this;
    }

    /**
     * Sets the name used to query for the API Key Secret from a Properties instance.  That is:
     * <pre>
     * String apiKeySecret = properties.getProperty(<b>apiKeySecretPropertyName</b>);
     * </pre>
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.apiKeySecretPropertyName = apiKeySecretPropertyName;
        return this;
    }

    /**
     * Sets the proxy to be used for Stormpath's requests
     *
     * @param proxy the {@code Proxy} you need to use.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    //For internal Stormpath testing needs only:
    ClientBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Constructs a new {@link Client} instance based on the ClientBuilder's current configuration state.
     *
     * @return a new {@link Client} instance based on the ClientBuilder's current configuration state.
     */
    public Client build() {

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

        String apiKeyId = getRequiredPropertyValue(properties, this.apiKeyIdPropertyName, "apiKeyId");

        String apiKeySecret = getRequiredPropertyValue(properties, this.apiKeySecretPropertyName, "apiKeySecret");

        assert apiKeyId != null;
        assert apiKeySecret != null;

        ApiKey apiKey = createApiKey(apiKeyId, apiKeySecret);

        return createClient(apiKey, this.baseUrl, proxy);
    }

    //since 0.5
    protected ApiKey createApiKey(String id, String secret) {
        return new DefaultApiKey(id, secret);
    }

    //since 0.5
    protected Client createClient(ApiKey key, String baseUrl) {
        return createClient(key, baseUrl, proxy);
    }

    //since 0.8
    protected Client createClient(ApiKey key, String baseUrl, Proxy proxy) {
        return baseUrl != null ? new Client(key, baseUrl, proxy) : new Client(key, proxy);
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
                String msg = "Unable to load InputStream for specified apiKeyFileLocation";
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
         * @since 0.9
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
            return Client.ClassUtils.getResourceAsStream(path);
        }

        private static String stripPrefix(String resourcePath) {
            return resourcePath.substring(resourcePath.indexOf(":") + 1);
        }
    }
}
