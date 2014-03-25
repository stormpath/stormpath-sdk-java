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

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link ApiKey} instances.
 * <p/>
 * The {@code ApiKeyBuilder} is especially useful for constructing ApiKey instances with Stormpath API Key
 * information loaded from an external {@code .properties} file (or Properties instance) to ensure the API Key secret
 * (password) does not reside in plaintext in code.
 * <p/>
 * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
 * api key object as follows:
 * <pre>
 * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
 *
 * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setApiKeyFileLocation(location).build();
 * </pre>
 * Then, you will create your {@link Client} instance as follows:
 * <pre>
 * Client client = {@link Clients Clients}.builder().setApiKeyFileLocation(apiKey).build();
 * </pre>
 * <p/>
 * You may load files from the filesystem, classpath, or URLs by prefixing the path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  See
 * {@link #setApiKeyFileLocation(String)} for more information.
 *
 * @see #setApiKeyFileLocation(String)
 * @see ClientBuilder#setApiKey(ApiKey)
 * @since 0.9.4
 */

public interface ApiKeyBuilder {

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
     * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setApiKey(apiKeyId, apiKeySecret).build();
     * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
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
     * ApiKey apiKey = ApiKeys.builder().setApiKey(apiKeyId, apiKeySecret).build();
     * Client client = Clients.builder().setApiKey(apiKey).build();
     * </pre>
     *
     * @param apiKeyId     the {@link ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @param apiKeySecret the {@link ApiKey#getSecret() ApiKey secret} value to use when communicating with Stormpath.
     * @return the ApiKeyBuilder instance for method chaining.
     * @see ClientBuilder#setApiKey(ApiKey)
     */
    ApiKeyBuilder setApiKey(String apiKeyId, String apiKeySecret);

    /**
     * Allows usage of a Properties instance instead of loading a {@code .properties} file via
     * {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeyProperties(Properties properties);

    /**
     * Creates an API Key Properties instance based on the specified Reader instead of loading a {@code .properties}
     * file via  {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     *
     * @param reader the reader to use to construct a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeyReader(Reader reader);

    /**
     * Creates an API Key Properties instance based on the specified InputStream
     * instead of loading a {@code .properties} file via
     * {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     *
     * @param is the InputStream to use to construct a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeyInputStream(InputStream is);

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
     * ApiKey apiKey = ApiKeys.builder().setApiKeyFileLocation(location).build();
     * Client client = Clients.builder().setApiKey(apiKey).build();
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
     *     .setApiKey(
     *          ApiKeys.builder()
     *          .setApiKeyFileLocation(location)
     *          .setApiKeyIdPropertyName("myStormpathApiKeyId")
     *          .setApiKeySecretPropertyName("myStormpathApiKeySecret")
     *          .build())
     *     .build();
     * </pre>
     *
     * @param location the file, classpath or url location of the API Key {@code .properties} file to load when
     *                 constructing the API Key to use for communicating with the Stormpath REST API.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeyFileLocation(String location);

    /**
     * Sets the name used to query for the API Key ID from a Properties instance.  That is:
     * <pre>
     * String apiKeyId = properties.getProperty(<b>apiKeyIdPropertyName</b>);
     * </pre>
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeyIdPropertyName(String apiKeyIdPropertyName);

    /**
     * Sets the name used to query for the API Key Secret from a Properties instance.  That is:
     * <pre>
     * String apiKeySecret = properties.getProperty(<b>apiKeySecretPropertyName</b>);
     * </pre>
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setApiKeySecretPropertyName(String apiKeySecretPropertyName);

    /**
     * Constructs a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.
     *
     * @return a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.
     */
    ApiKey build();

}
