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
package com.stormpath.sdk.client;

import com.stormpath.sdk.cache.CacheManager;

import java.io.*;
import java.util.Properties;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link com.stormpath.sdk.client.Client} instances.
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
 * Client client = {@link Clients Clients}.builder().setApiKeyFileLocation(location).build();
 * </pre>
 * <p/>
 * You may load files from the filesystem, classpath, or URLs by prefixing the path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  See
 * {@link #setApiKeyFileLocation(String)} for more information.
 *
 * @see #setApiKeyFileLocation(String)
 * @since 0.9.4
 */
public interface ClientBuilder {

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
     * Client client = {@link Clients Clients}.builder().setApiKey(apiKeyId, apiKeySecret).build();
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
     * Client client = Clients.builder().setApiKey(apiKeyId, apiKeySecret).build();
     * </pre>
     *
     * @param apiKeyId     the {@link ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @param apiKeySecret the {@link ApiKey#getSecret() ApiKey secret} value to use when communicating with Stormpath.
     * @return the ClientBuilder instance for method chaining.
     * @see #setApiKey(ApiKey)
     */
    ClientBuilder setApiKey(String apiKeyId, String apiKeySecret);

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
     * Client client = {@link Clients Clients}.builder().setApiKey(anApiKey).build();
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
     * Client client = Clients.builder().setApiKey(anApiKey).build();
     * </pre>
     *
     * @param apiKey the ApiKey to use to authenticate requests to the Stormpath API server.
     * @return the ClientBuilder instance for method chaining.
     * @see #setApiKey(String, String)
     */
    ClientBuilder setApiKey(ApiKey apiKey);

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
    ClientBuilder setApiKeyProperties(Properties properties);

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
    ClientBuilder setApiKeyReader(Reader reader);

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
    ClientBuilder setApiKeyInputStream(InputStream is);

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
     * Client client = Clients.builder().setApiKeyFileLocation(location).build();
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
     *     Clients.builder()
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
    ClientBuilder setApiKeyFileLocation(String location);

    /**
     * Sets the name used to query for the API Key ID from a Properties instance.  That is:
     * <pre>
     * String apiKeyId = properties.getProperty(<b>apiKeyIdPropertyName</b>);
     * </pre>
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setApiKeyIdPropertyName(String apiKeyIdPropertyName);

    /**
     * Sets the name used to query for the API Key Secret from a Properties instance.  That is:
     * <pre>
     * String apiKeySecret = properties.getProperty(<b>apiKeySecretPropertyName</b>);
     * </pre>
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setApiKeySecretPropertyName(String apiKeySecretPropertyName);

    /**
     * Sets the HTTP proxy to be used when communicating with the Stormpath API server.
     *
     * @param proxy the {@code Proxy} you need to use.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setProxy(Proxy proxy);

    /**
     * Sets the {@link CacheManager} that should be used to cache Stormpath REST resources, reducing round-trips to the
     * Stormpath API server and enhancing application performance.
     * <p/>
     * <h3>Single JVM Applications</h3>
     * If your application runs on a single JVM-based applications, the
     * {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder} should be sufficient for your needs. You
     * create a {@code CacheManagerBuilder} by using the {@link com.stormpath.sdk.cache.Caches Caches} utility class,
     * for example:
     * <pre>
     * import static com.stormpath.sdk.cache.Caches.*;
     *
     * ...
     *
     * Client client = Clients.builder()...
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
     */
    ClientBuilder setCacheManager(CacheManager cacheManager);

    /**
     * Overrides the default (very secure)
     * <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest">Stormpath SAuthc1 Digest Authentication Scheme</a>
     * used to authenticate every request sent to the Stormpath API server.
     * <p/>
     * It is not recommended that you override this setting <em>unless</em> your application is deployed in an
     * environment that - outside of the application's control - manipulates request headers on outgoing HTTP requests.
     * Google App Engine is one such environment, for example.
     * <p/>
     * As such, in these environments only, an alternative authentication mechanism is necessary, such as
     * <a href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP
     * Basic Authentication</a>.  You can enable Basic Authentication as follows (again, only do this if your
     * application runtime environment forces you to use it, like Google App Engine):
     * </pre>
     * Client client = Clients.builder()...
     *    // setApiKey, etc...
     *    .setAuthenticationScheme(AuthenticationScheme.BASIC) //set the basic authentication scheme
     *    .build(); //build the Client
     * </pre>
     *
     * @param authenticationScheme the type of authentication to be used for communication with the Stormpath API server.
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme);

    /**
     * Constructs a new {@link Client} instance based on the ClientBuilder's current configuration state.
     *
     * @return a new {@link Client} instance based on the ClientBuilder's current configuration state.
     */
    Client build();

}
