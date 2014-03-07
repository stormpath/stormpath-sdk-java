/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.lang.Classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
 * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
 * client as follows:
 * <pre>
 * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
 *
 * Client = new ClientBuilder().setApiKeyFileLocation(location).build();
 * </pre>
 * <p/>
 * You may load files from the filesystem, classpath, or URLs by prefixing the path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  See
 * {@link #setApiKeyFileLocation(String)} for more information.
 *
 * @see #setApiKeyFileLocation(String)
 * @since 0.3
 */
public class ClientBuilder {

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

    /**
     * Constructs a new {@code ClientBuilder} instance, ready to be configured via various {@code set}ter methods.
     */
    public ClientBuilder() {
    }

    /**
     * Allows specifying the client's API Key {@code id} and {@code secret} values directly instead of reading the key
     * from a stream-based resource (e.g. File, Reader, Properties or InputStream).
     * <h3>Usage Warning</h3>
     * It is almost always advisable to NOT use this method and instead use methods that accept a
     * stream-based resource (File, Reader, Properties or InputStream): these other methods would ideally acquire the
     * API Key from a secure and private {@code apiKey.properties} file that is readable only by the process that
     * uses the Stormpath SDK.
     * <p/>
     * This builder method is provided however for environments that do not have access to stream resources or files,
     * such as in certain application hosting providers or Platform-as-a-Service environments like Heroku.
     * <h4>Environment Variables</h4>
     * In these restricted environments, the ApiKey {@code id} and {@code secret} would almost always be obtained from
     * environment variables, for example:
     * <pre>
     * String apiKeyId = System.getenv("STORMPATH_API_KEY_ID");
     * String apiKeySecret = System.getenv("STORMPATH_API_KEY_SECRET");
     * Client client = new ClientBuilder().setApiKey(apiKeyId, apiKeySecret).build();
     * </pre>
     * <h4>System Properties</h4>
     * It is <em>not</em> recommended to load the ApiKey id and secret from a system property, for example:
     * <p/>
     * <span color="red"><b>THIS IS NOT RECOMMENDED. THIS COULD BE A SECURITY RISK:</b></span>
     * <pre color="red">
     * String apiKeySecret = System.getProperty("STORMPATH_API_KEY_SECRET");
     * </pre>
     * This is not recommended because System properties are visible in process listings, e.g. on Unix/Linux/MacOS:
     * <pre><code>
     * $ ps aux
     * </code></pre>
     * You do not want your API Key Secret visible by anyone who can do a process listing!
     * <h4>Hard Coding</h4>
     * It is <b>NEVER</b> recommended to embed the raw ApiKey values in source code that would be committed to
     * version control (like Git or Subversion):
     * <p/>
     * <span color="red"><b>THIS IS AN ANTI-PATTERN! DO NOT DO THIS! THIS IS A SECURITY RISK!</b></span>
     * <pre color="red">
     * String apiKeyId = "myRawApiKeyId";
     * String apiKeySecret = "secretValueThatAnyoneCouldSeeIfTheyCheckedOutMySourceCode";
     * Client client = new ClientBuilder().setApiKey(apiKeyId, apiKeySecret).build();
     * </pre>
     *
     * @param apiKeyId     the {@link ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @param apiKeySecret the {@link ApiKey#getSecret() ApiKey secret} value to use when communicating with Stormpath.
     * @return the ClientBuilder instance for method chaining.
     * @see #setApiKey(ApiKey)
     * @since 0.8
     */
    public ClientBuilder setApiKey(String apiKeyId, String apiKeySecret) {
        ApiKey apiKey = new DefaultApiKey(apiKeyId, apiKeySecret);
        return setApiKey(apiKey);
    }

    /**
     * Allows specifying an {@code ApiKey} instance directly instead of reading the key from a stream-based resource
     * (e.g. File, Reader, Properties or InputStream).
     * <h3>Usage Warning</h3>
     * It is almost always advisable to NOT use this method and instead use methods that accept a
     * stream-based resource (File, Reader, Properties or InputStream): these other methods would ideally acquire the
     * API Key from a secure and private {@code apiKey.properties} file that is readable only by the process that
     * uses the Stormpath SDK.
     * <p/>
     * This builder method is provided however for environments that do not have access to stream resources or files,
     * such as in certain application hosting providers or Platform-as-a-Service environments like Heroku.
     * <h4>Environment Variables</h4>
     * In these restricted environments, the ApiKey {@code id} and {@code secret} would almost always be obtained from
     * environment variables, for example:
     * <pre>
     * String apiKeyId = System.getenv("STORMPATH_API_KEY_ID");
     * String apiKeySecret = System.getenv("STORMPATH_API_KEY_SECRET");
     * ApiKey apiKey = new DefaultApiKey(apiKeyId, apiKeySecret);
     * Client client = new ClientBuilder().setApiKey(anApiKey).build();
     * </pre>
     * <h4>System Properties</h4>
     * It is <em>not</em> recommended to load the ApiKey id and secret from a system property, for example:
     * <p/>
     * <span color="red"><b>THIS IS NOT RECOMMENDED. THIS COULD BE A SECURITY RISK:</b></span>
     * <pre color="red">
     * String apiKeySecret = System.getProperty("STORMPATH_API_KEY_SECRET");
     * </pre>
     * This is not recommended because System properties are visible in process listings, e.g. on Unix/Linux/MacOS:
     * <pre><code>
     * $ ps aux
     * </code></pre>
     * You do not want your API Key Secret visible by anyone who can do a process listing!
     * <h4>Hard Coding</h4>
     * It is <b>NEVER</b> recommended to embed the raw ApiKey values in source code that would be committed to
     * version control (like Git or Subversion):
     * <p/>
     * <span color="red"><b>THIS IS AN ANTI-PATTERN! DO NOT DO THIS! THIS IS A SECURITY RISK!</b></span>
     * <pre color="red">
     * String apiKeyId = "myRawApiKeyId";
     * String apiKeySecret = "secretValueThatAnyoneCouldSeeIfTheyCheckedOutMySourceCode";
     * ApiKey apiKey = new DefaultApiKey(apiKeyId, apiKeySecret);
     * Client client = new ClientBuilder().setApiKey(anApiKey).build();
     * </pre>
     *
     * @param apiKey the ApiKey to use to authenticate requests to the Stormpath API server.
     * @return the ClientBuilder instance for method chaining.
     * @see #setApiKey(String, String)
     * @since 0.8
     */
    public ClientBuilder setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
        return this;
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
     *
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
     *
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
     * <tr>
     * <th>Key</th>
     * <th>Value</th>
     * </tr>
     * <tr>
     * <td>apiKey.id</td>
     * <td>An individual account's API Key ID</td>
     * </tr>
     * <tr>
     * <td>apiKey.secret</td>
     * <td>The API Key Secret (password) that verifies the paired API Key ID.</td>
     * </tr>
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
     * Sets the HTTP proxy to be used when communicating with the Stormpath API server.
     *
     * @param proxy the {@code Proxy} you need to use.
     * @return the ClientBuilder instance for method chaining.
     */
    public ClientBuilder setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy argument cannot be null.");
        }
        this.proxy = proxy;
        return this;
    }

    /**
     * Sets the {@link CacheManager} that should be used to cache Stormpath REST resources, reducing round-trips to the
     * Stormpath API server and enhancing application performance.
     * <p/>
     * <h3>Single JVM Applications</h3>
     * If your application runs on a single JVM-based applications, the
     * {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder} should be sufficient for your needs.  You
     * create a {@code CacheManagerBuilder} by using the {@link com.stormpath.sdk.cache.Caches Caches} utility class,
     * for example:
     * <pre>
     * import static com.stormpath.sdk.cache.Caches.*;
     *
     * ...
     *
     * Client client = new ClientBuilder()...
     *     .setCacheManager(
     *         {@link com.stormpath.sdk.cache.Caches#newCacheManager() newCacheManager()}
     *         .withDefaultTimeToLive(1, TimeUnit.DAYS) //general default
     *         .withDefaultTimeToIdle(2, TimeUnit.HOURS) //general default
     *         .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Account.class) //Account-specific cache settings
     *             .withTimeToLive(1, TimeUnit.HOURS)
     *             .withTimeToIdle(30, TimeUnit.MINUTES))
     *         .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Group.class) //Group-specific cache settings
     *             .withTimeToLive(2, TimeUnit.HOURS))
     *         .build() //build the CacheManager
     *     )
     *     .build(); //build the Client
     * </pre>
     * <em>The above TTL and TTI times are just examples showing API usage - the times themselves are not
     * recommendations.  Choose TTL and TTI times based on your application requirements.</em>
     * <h3>Multi-JVM / Clustered Applications</h3>
     * The default {@code CacheManager} instances returned by the
     * {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder} might not be sufficient for a
     * multi-instance application that runs on multiple JVMs and/or hosts/servers, as there could be cache-coherency
     * problems across the JVMs.  See the {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder}
     * JavaDoc for additional information.
     * <p/>
     * In these multi-JVM environments, you will likely want to create a simple CacheManager implementation that wraps
     * your distributed Caching API/product of choice and then plug that implementation in to the Stormpath SDK via
     * this method.
     *
     * @param cacheManager the {@link CacheManager} that should be used to cache Stormpath REST resources, reducing
     *                     round-trips to the Stormpath API server and enhancing application performance.
     * @since 0.8
     */
    public ClientBuilder setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        return this;
    }

    /**
     * Sets the HTTP authentication scheme to be used when communicating with the Stormpath API server.
     * </pre>
     * This setting is helpful in cases where the code is run in a platform where the header information for
     * outgoing HTTP requests is modified and thus causing communication issues. For example, for Google App Engine you
     * need to set {@link AuthenticationScheme#BASIC} in order for your code to properly communicate with Stormpath API server.
     * </pre>
     * There are currently two authentication schemes available: <a href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP
     * Basic Authentication</a> and <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest">Digest Authentication</a>.
     * When no authentication scheme is explicitly defined, {@link AuthenticationScheme#SAUTHC1} is used by default.
     * </pre>
     * For example, the basic authentication scheme is defined this way:
     * </pre>
     * Client client = new ClientBuilder()...
     *    .setAuthenticationScheme(AuthenticationScheme.BASIC) //set the basic authentication scheme
     *    .build(); //build the Client
     * </pre>
     *
     * @param authenticationScheme the type of authentication to be used for communication with the Stormpath API server.
     * @return the ClientBuilder instance for method chaining
     * @since 0.9.3
     */
    public ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
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

        ApiKey apiKey = this.apiKey;

        if (apiKey == null) {
            apiKey = loadApiKey();
        }

        return new Client(apiKey, this.baseUrl, proxy, cacheManager, authenticationScheme);
    }

    //since 0.8
    protected ApiKey loadApiKey() {

        Properties properties = loadApiKeyProperties();

        String apiKeyId = getRequiredPropertyValue(properties, this.apiKeyIdPropertyName, "apiKeyId");

        String apiKeySecret = getRequiredPropertyValue(properties, this.apiKeySecretPropertyName, "apiKeySecret");

        return createApiKey(apiKeyId, apiKeySecret);
    }

    //since 0.8
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

    //since 0.5
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
         * @since 0.8
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
