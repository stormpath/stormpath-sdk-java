/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
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
 * <p/>
 * The {@code ApiKeyBuilder} is especially useful for constructing Client ApiKey instances with Stormpath API Key
 * information loaded from an external {@code .properties} file (or Properties instance) to ensure the API Key secret
 * (password) does not reside in plaintext in code.
 * <p/>
 * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
 * api key object as follows:
 * <pre>
 * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
 *
 * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setFileLocation(location).build();
 * </pre>
 * Then, you will create your {@link Client} instance as follows:
 * <pre>
 * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
 * </pre>
 * <p/>
 * You may load files from the filesystem, classpath, or URLs by prefixing the path with
 * {@code file:}, {@code classpath:}, or {@code url:} respectively.  See
 * {@link #setFileLocation(String)} for more information.
 *
 * @see #setFileLocation(String)
 * @see ClientBuilder#setApiKey(com.stormpath.sdk.client.ApiKey)
 * @since 1.1.beta
 */
public interface ApiKeyBuilder extends com.stormpath.sdk.client.ApiKeyBuilder{ // temporarily extending the client package's ApiKeyBuilder before deleting it

    /**
     * Allows specifying the client's API Key {@code id} value directly instead of reading it
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
     * String id = System.getenv("STORMPATH_API_KEY_ID");
     * String secret = System.getenv("STORMPATH_API_KEY_SECRET");
     * ApiKey apiKey = {@link com.stormpath.sdk.client.ApiKeys ApiKeys}.builder().setId(id).setSecret(secret).build();
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
     * String id = "myRawApiKeyId";
     * String secret = "secretValueThatAnyoneCouldSeeIfTheyCheckedOutMySourceCode";
     * ApiKey apiKey = ApiKeys.builder().setId(id).setSecret(secret).build();
     * Client client = Clients.builder().setApiKey(apiKey).build();
     * </pre>
     *
     * @param id the {@link com.stormpath.sdk.client.ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @return the ApiKeyBuilder instance for method chaining.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.client.ApiKey)
     */
    ApiKeyBuilder setId(String id);

    /**
     * Allows specifying the client's API Key {@code secret} value directly instead of reading it
     * from a stream-based resource (e.g. File, Reader, Properties or InputStream).
     * <p/>
     * For usage instructions and security precautions see {@link #setId(String)}
     *
     * @param secret the {@link com.stormpath.sdk.client.ApiKey#getId() ApiKey id} to use when communicating with Stormpath.
     * @return the ApiKeyBuilder instance for method chaining.
     * @see #setId(String)
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.client.ApiKey)
     */
    ApiKeyBuilder setSecret(String secret);

    /**
     * Allows usage of a Properties instance instead of loading a {@code .properties} file via
     * {@link #setFileLocation(String) fileLocation} configuration.
     * <p/>
     * The {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setProperties(Properties properties);

    /**
     * Creates an API Key Properties instance based on the specified Reader instead of loading a {@code .properties}
     * file via  {@link #setFileLocation(String) fileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.
     *
     * @param reader the reader to use to construct a Properties instance.
     * @return the ApiKeyBuilder instance for method chaining.
     */
    ApiKeyBuilder setReader(Reader reader);

    /**
     * Creates an API Key Properties instance based on the specified InputStream
     * instead of loading a {@code .properties} file via
     * {@link #setFileLocation(String) fileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setFileLocation(String) setFileLocation} JavaDoc.
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
     * Constructs a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.
     *
     * @return a new {@link ApiKey} instance based on the ApiKeyBuilder's current configuration state.
     */
    ApiKey build();
}
