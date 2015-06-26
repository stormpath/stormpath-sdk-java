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
package com.stormpath.sdk.api;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link ApiKey} instances.
 *
 * <p>The {@code ApiKeyBuilder} is especially useful for constructing Client ApiKey instances with Stormpath API Key
 * information loaded from an external {@code .properties} file (or Properties instance) to ensure the API Key secret
 * (password) does not reside in plaintext in code.</p>
 *
 * <h3>Usage</h3>
 *
 * <p>The simplest usage is to just call the {@link #build() build()} method, which will automatically attempt to find
 * your API Key values in a number of default/conventional locations.  For example:</p>
 *
 * <pre>
 * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().{@link #build() build()};
 * </pre>
 *
 * <p>Without any other configuration, the following locations will be each be checked, in order:</p>
 *
 * <ol>
 *     <li>The default apiKey.properties file location of
 *         <code>System.getProperty("user.home") + "/.stormpath/apiKey.properties"</code> as
 *         recommended/documented in the <a href="https://docs.stormpath.com/java/quickstart/">Stormpath Java
 *         Quickstart</a>.</li>
 *     <li>A properties file that exists at the file path or URL specified by the {@code STORMPATH_API_KEY_FILE}
 *         variable.  If this file exists and contains either the apiKey id or secret properties, these values
 *         override any values found in the default apiKey.properties file.  The {@code STORMPATH_API_KEY_FILE}
 *         String can be an absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
 *         {@code classpath:} prefixes respectively.</li>
 *     <li>The environment variables {@code STORMPATH_API_KEY_ID} and {@code STORMPATH_API_KEY_SECRET}.  If either of
 *         these values are present, they override any previously discovered value.</li>
 *     <li>A properties file that exists at the file path or URL specified by the {@code stormpath.apiKey.file}
 *         system property.  If this file exists and any values are present, the values override any
 *         previously discovered value.  The {@code stormpath.apiKey.file} system property String can be an
 *         absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
 *         {@code classpath:} prefixes respectively.</li>
 *     <li>The system properties {@code stormpath.apiKey.id} and {@code stormpath.apiKey.secret}.  If either of
 *         these values are present, they override any previously discovered values.</li>
 * </ol>
 *
 * <p><b>SECURITY NOTICE:</b> While the {@code stormpath.apiKey.secret} system property may be used to represent your
 * API Key Secret as mentioned above, this is not recommended: process listings on a machine will expose process
 * arguments (like system properties) and expose the secret value to anyone that can read process listings.  As
 * always, secret values should never be exposed to anyone other than the person that owns the API Key.</p>
 *
 * <p>While an API Key ID may be configured anywhere (and be visible by anyone), it is recommended to use a private
 * read-only file or an environment variable to represent API Key secrets.  <b>Never</b> commit secrets to source code
 * or version control.</p>
 *
 * <h4>Explicit Configuration</h4>
 *
 * <p>If the above default locations do not meet your needs, or you would like to override any of the discovered values,
 * you can configure id and secret values via specific builder properties:</p>
 *
 * <ul>
 *     <li>{@link #setFileLocation(String) fileLocation}:  Any properties discovered in this .properties file
 *         will override any previously automatically discovered values.  The {@code fileLocation} String can be an
 *         absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
 *         {@code classpath:} prefixes respectively.</li>
 *     <li>{@link #setInputStream(java.io.InputStream) inputStream}: Properties discovered after reading the input
 *         stream will override any previously discovered values.</li>
 *     <li>{@link #setReader(java.io.Reader) reader}: Properties discovered after reading the Reader will override any
 *         previously discovered values.</li>
 *     <li>{@link #setProperties(java.util.Properties) properties} instance: Properties discovered in the instance
 *         will override any previously discovered values.</li>
 *     <li>Directly specified {@link #setId(String) id} and {@link #setSecret(String) secret} property values.
 *         If specified, they will override any previously discovered value.</li>
 * </ul>
 *
 * <h3>Supporting the ClientBuilder</h3>
 *
 * <p>Once you have built your {@code ApiKey} instance, you can then build your {@link Client} instance that you
 * can use during your application's lifecycle.  For example:</p>
 *
 * <pre>
 * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder()
 *                 //optional specific configuration
 *                 .{@link #build build()};
 *
 * //use this same Client instance everywhere in your application:
 * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
 * </pre>
 *
 * @see #setFileLocation(String)
 * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
 * @since 1.0.RC
 */
public interface ApiKeyBuilder extends com.stormpath.sdk.client.ApiKeyBuilder{ // temporarily extending the client package's ApiKeyBuilder before deleting it

    public static final String DEFAULT_ID_PROPERTY_NAME = "apiKey.id";
    public static final String DEFAULT_SECRET_PROPERTY_NAME = "apiKey.secret";

    /**
     * Allows specifying the client's API Key {@code id} value directly instead of relying on the
     * default location + override/fallback behavior defined in the {@link ApiKeyBuilder documentation above}.
     *
     * @param id the {@link com.stormpath.sdk.api.ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @return the ApiKeyBuilder instance for method chaining.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     */
    ApiKeyBuilder setId(String id);

    /**
     * Allows specifying the client's API Key {@code secret} value directly instead of relying on the
     * default location + override/fallback behavior defined in the {@link ApiKeyBuilder documentation above}.
     *
     * <h3>Usage Warning</h3>
     *
     * <p>It is strongly recommended that you never embed raw API Key secret values in source code!  API Key Secret
     * values are tied to an individual <em>person</em> and should never be shared with anyone or embedded in source
     * code that can be viewed by multiple people.  For example:</p>
     *
     * <p>
     * <span color="red"><b>THIS IS AN ANTI-PATTERN! DO NOT DO THIS! THIS IS A SECURITY RISK!</b></span>
     * <pre color="red">
     * String id = "myRawApiKeyId";
     * String secret = "secretValueThatAnyoneCouldSeeIfTheyCheckedOutMySourceCode";
     * ApiKey apiKey = ApiKeys.builder().setId(id).setSecret(secret).build();
     * Client client = Clients.builder().setApiKey(apiKey).build();
     * </pre>
     * </p>
     *
     * <p>Because of this, it is recommended to rely on the default location + override/fallback behavior defined in
     * the {@link ApiKeyBuilder documentation above}.  However, if the default environment variable or system property
     * names are not suitable, and you would prefer to use your own names, or you would prefer to obtain your secret
     * value in a different secure manner, setting this property explicitly can be useful.  For example:</p>
     *
     * <pre>
     * String id = System.getenv("MY_STORMPATH_API_KEY_ID"); //alternative (non-default) env var name
     * String secret = System.getenv("MY_STORMPATH_API_KEY_SECRET"); //alternative (non-default) env var name
     * ApiKey apiKey = {@link com.stormpath.sdk.api.ApiKeys ApiKeys}.builder().setId(id).setSecret(secret).build();
     * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
     * </pre>
     *
     * @param secret the {@link com.stormpath.sdk.api.ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @return the ApiKeyBuilder instance for method chaining.
     * @see #setId(String)
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     */
    ApiKeyBuilder setSecret(String secret);

    /**
     * Allows usage of a Properties instance instead of loading a {@code .properties} file via
     * {@link #setFileLocation(String) fileLocation} configuration.
     *
     * <p>The {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.</p>
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setProperties(Properties properties);

    /**
     * Creates an API Key Properties instance based on the specified Reader instead of loading a {@code .properties}
     * file via  {@link #setFileLocation(String) fileLocation} configuration.
     *
     * <p>The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.</p>
     *
     * @param reader the reader to use to construct a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setReader(Reader reader);

    /**
     * Creates an API Key Properties instance based on the specified InputStream
     * instead of loading a {@code .properties} file via
     * {@link #setFileLocation(String) fileLocation} configuration.
     *
     * <p>The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.</p>
     *
     * @param is the InputStream to use to construct a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setInputStream(InputStream is);

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
     * ApiKey apiKey = ApiKeys.builder().setFileLocation(location).build();
     * Client client = Clients.builder().setApiKey(apiKey).build();
     * </pre>
     * <h3>Custom Property Names</h3>
     * If you want to control the property names used in the file, you may configure them via
     * {@link #setIdPropertyName(String) setIdPropertyName} and
     * {@link #setSecretPropertyName(String) setSecretPropertyName}.
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
     *     .setApiKey(
     *          ApiKeys.builder()
     *          .setFileLocation(location)
     *          .setIdPropertyName("myStormpathApiKeyId")
     *          .setSecretPropertyName("myStormpathApiKeySecret")
     *          .build())
     *     .build();
     * </pre>
     *
     * @param location the file, classpath or url location of the API Key {@code .properties} file to load when
     *                 constructing the API Key to use for communicating with the Stormpath REST API.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setFileLocation(String location);

    /**
     * Sets the name used to query for the API Key ID from a Properties instance.  That is:
     * <pre>
     * String apiKeyId = properties.getProperty(<b>idPropertyName</b>);
     * </pre>
     *
     * @param idPropertyName the name used to query for the API Key ID from a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setIdPropertyName(String idPropertyName);

    /**
     * Sets the name used to query for the API Key Secret from a Properties instance.  That is:
     * <pre>
     * String apiKeySecret = properties.getProperty(<b>secretPropertyName</b>);
     * </pre>
     *
     * @param secretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setSecretPropertyName(String secretPropertyName);

    /**
     * Constructs a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.  This method
     * loads (and overrides) ID and secret values from the following locations, in order:
     *
     * <ol>
     *     <li>The default apiKey.properties file location of
     *         <code>System.getProperty("user.home") + "/.stormpath/apiKey.properties"</code> as
     *         recommended/documented in the <a href="https://docs.stormpath.com/java/quickstart/">Stormpath Java
     *         Quickstart</a>.</li>
     *     <li>A properties file that exists at the file path or URL specified by the {@code STORMPATH_API_KEY_FILE}
     *         environment variable.  If this file exists and contains either the apiKey id or secret properties, these
     *         values override any values found in the default apiKey.properties file.  The {@code STORMPATH_API_KEY_FILE}
     *         String can be an absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
     *         {@code classpath:} prefixes respectively.</li>
     *     <li>The environment variables {@code STORMPATH_API_KEY_ID} and {@code STORMPATH_API_KEY_SECRET}.  If either of
     *         these values are present, they override any previously discovered value.</li>
     *     <li>A properties file that exists at the file path or URL specified by the {@code stormpath.apiKey.file}
     *         system property.  If this file exists and any values are present, the values override any
     *         previously discovered value.  The {@code stormpath.apiKey.file} system property String can be an
     *         absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
     *         {@code classpath:} prefixes respectively.</li>
     *     <li>The system properties {@code stormpath.apiKey.id} and {@code stormpath.apiKey.secret}.  If either of
     *         these values are present, they override any previously discovered values.</li>
     *     <li>A specified {@link #setFileLocation(String) fileLocation}:  Any properties discovered in this .properties
     *         file will override any previously automatically discovered values.  The {@code fileLocation} String can
     *         be an absolute file path, or it can be a URL or a classpath value by using the {@code url:} or
     *         {@code classpath:} prefixes respectively.</li>
     *     <li>A specified {@link #setInputStream(java.io.InputStream) inputStream}: Properties discovered while
     *         reading the input stream will override any previously discovered values.</li>
     *     <li>A specified {@link #setReader(java.io.Reader) reader}: Properties discovered after reading the Reader
     *         will override any previously discovered values.</li>
     *     <li>A specified {@link #setProperties(java.util.Properties) properties} instance: Properties discovered in
     *         the instance will override any previously discovered values.</li>
     *     <li>Directly specified {@link #setId(String) id} and {@link #setSecret(String) secret} property values.
     *         If specified, they will override any previously discovered value.</li>
     * </ol>
     *
     * @return a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.
     */
    ApiKey build();
}
