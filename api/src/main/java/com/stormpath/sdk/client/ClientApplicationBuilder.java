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

import com.stormpath.sdk.application.Application;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * <h2>Deprecated</h2>
 * This class has been deprecated as of 0.8 and it will be removed before 1.0 final.  Instead of using this class,
 * use the {@link ClientBuilder ClientBuilder} and after built, call
 * <pre>
 *     client.getResource(appUrl, Application.class);
 * </pre>
 * to acquire an application instance resource.
 * <p/>
 * <p/>
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> implementation similar to
 * the {@link ClientBuilder}, but focused on single-application interaction with Stormpath.
 * <h2>Description</h2>
 * The {@code ClientBuilder} produces a {@link Client} instance useful for interacting with any aspect
 * of an entire Stormpath Tenant's data space.  However, a software application may only be interested in its own
 * functionality and not the entire Stormpath Tenant data space.
 * <p/>
 * The {@code ClientApplicationBuilder} provides a means to more easily acquire a single
 * {@link Application} instance.  From this {@code Application} instance, everything a particular Application needs to
 * perform can be based off of this instance and the wider-scoped concerns of an entire Tenant can be ignored.
 * <h2>Default Usage</h2>
 * <pre>
 * //this can be a classpath: or url: location as well:
 * String apiKeyFileLocation = "/home/jsmith/.stormpath/apiKey.properties";
 *
 * String appHref = "https://api.stormpath.com/v1/applications/YOUR_APP_UID_HERE";
 *
 * Application application = new ClientApplicationBuilder()
 *     <b>.setApplicationHref(appHref)</b>
 *     .build().getApplication();
 * </pre>
 * <p/>
 * After acquiring the {@code application} instance, you can interact with it to login accounts, reset passwords,
 * etc.
 * <h2>Service Provider Usage with only an Application URL</h2>
 * Some hosting service providers (e.g. like <a href="http://www.heroku.com">Heroku</a>) do not allow easy access to
 * a a configuration file and therefore it might be difficult to reference an API Key File.  If you cannot reference an
 * API Key File via the {@code file:}, {@code classpath:} or {@code url:}
 * {@link #setApiKeyFileLocation(String) resource locations}, the Application HREF URL must
 * contain the API Key embedded as the <em><a href="http://en.wikipedia.org/wiki/URI_scheme">user info</a></em> portion
 * of the URL.  For example:
 * <p/>
 * <pre>
 * https://<b>apiKeyId:apiKeySecret@</b>api.stormpath.com/v1/applications/YOUR_APP_UID_HERE
 * </pre>
 * <p/>
 * Notice this is just a normal Application HREF url with the <b>apiKeyId:apiKeySecret@</b> part added in.
 * <p/>
 * Example usage:
 * <pre>
 * String appHref = "https://<b>apiKeyId:apiKeySecret@</b>api.stormpath.com/v1/applications/YOUR_APP_UID_HERE";
 *
 * Application application = new ClientApplicationBuilder()
 *     <b>.setApplicationHref(appHref)</b>
 *     .build().getApplication();
 * </pre>
 * <p/>
 * <b>WARNING: ONLY use the embedded API Key technique if you do not have access to {@code file:}, {@code classpath:}
 * or {@code url:} {@link #setApiKeyFileLocation(String) resource locations}</b>.  File based API Key
 * storage is a more secure technique than embedding the key in the URL itself.  Also, again, NEVER share your API Key
 * Secret with <em>anyone</em> (not even co-workers).  Stormpath staff will never ask for your API Key Secret.
 *
 * @see #setApiKeyFileLocation(String)
 * @see #setApplicationHref(String)
 * @since 0.5
 * @deprecated in 0.8 and will be removed before 1.0 final.  Use the Client.Builder and then call <code>client.getResource(appUrl, Application.class);</code>
 */
@Deprecated
public class ClientApplicationBuilder {

    private static final String DOUBLE_SLASH = "//";
    private static final String ENCODING = "UTF-8";

    private String applicationHref;
    private final ClientBuilder clientBuilder; //internal delegate object

    public ClientApplicationBuilder() {
        this.clientBuilder = new ClientBuilder();
    }

    protected ClientApplicationBuilder(ClientBuilder builder) {
        this.clientBuilder = builder;
    }

    /**
     * Allows usage of a Properties instance instead of loading a {@code .properties} file via
     * {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeyProperties(Properties properties) {
        this.clientBuilder.setApiKeyProperties(properties);
        return this;
    }

    /**
     * Creates an API Key Properties instance based on the specified Reader instead of loading a {@code .properties}
     * file via  {@link #setApiKeyFileLocation(String) apiKeyFileLocation} configuration.
     * <p/>
     * The constructed {@code Properties} contents and property name overrides function the same as described in the
     * {@link #setApiKeyFileLocation(String) setApiKeyFileLocation} JavaDoc.
     * @param reader the reader to use to construct a Properties instance.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeyReader(Reader reader) {
        this.clientBuilder.setApiKeyReader(reader);
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
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeyInputStream(InputStream is) {
        this.clientBuilder.setApiKeyInputStream(is);
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
     * Assuming you were using these default property names, your {@code ClientApplicationBuilder} usage might look
     * like the following:
     * <pre>
     * String location = "/home/jsmith/.stormpath/apiKey.properties";
     *
     * Application app = new ClientApplicationBuilder().setApiKeyFileLocation(location).build().getApplication();
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
     * Your {@code ClientApplicationBuilder} usage would look like the following:
     * <pre>
     * String location = "/home/jsmith/.stormpath/apiKey.properties";
     *
     * Application app = new ClientApplicationBuilder()
     *     .setApiKeyFileLocation(location)
     *     .setApiKeyIdPropertyName("myStormpathApiKeyId")
     *     .setApiKeySecretPropertyName("myStormpathApiKeySecret")
     *     .build().getApplication();
     * </pre>
     *
     * @param location the file, classpath or url location of the API Key {@code .properties} file to load when
     *                 constructing the API Key to use for communicating with the Stormpath REST API.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeyFileLocation(String location) {
        this.clientBuilder.setApiKeyFileLocation(location);
        return this;
    }

    /**
     * Sets the name used to query for the API Key ID from a Properties instance.  That is:
     * <pre>
     * String apiKeyId = properties.getProperty(<b>apiKeyIdPropertyName</b>);
     * </pre>
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.clientBuilder.setApiKeyIdPropertyName(apiKeyIdPropertyName);
        return this;
    }

    /**
     * Sets the name used to query for the API Key Secret from a Properties instance.  That is:
     * <pre>
     * String apiKeySecret = properties.getProperty(<b>apiKeySecretPropertyName</b>);
     * </pre>
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.clientBuilder.setApiKeySecretPropertyName(apiKeySecretPropertyName);
        return this;
    }

    //For internal Stormpath testing needs only:
    ClientApplicationBuilder setBaseUrl(String baseUrl) {
        this.clientBuilder.setBaseUrl(baseUrl);
        return this;
    }


    /**
     * Sets the fully qualified Stormpath Application HREF (a URL) to use to acquire the Application instance when
     * {@link #build()} is called.  See the Class-level JavaDoc for usage scenarios.
     *
     * @param applicationHref the fully qualified Stormpath Application HREF (a URL) to use to acquire the
     *                        Application instance when {@link #build()} is called.
     * @return this ClientApplicationBuilder instance for method chaining.
     */
    public ClientApplicationBuilder setApplicationHref(String applicationHref) {
        this.applicationHref = applicationHref;
        return this;
    }

    /**
     * Builds a Client and Application wrapper instance based on the configured
     * {@link #setApplicationHref(String) applicationHref}. See the Class-level JavaDoc for usage scenarios.
     *
     * @return a Client and Application wrapper instance based on the configured {@link #setApplicationHref(String) applicationHref}.
     */
    public ClientApplication build() {

        String href = this.applicationHref != null ? this.applicationHref.trim() : null;
        if (href == null || href.equals("")) {
            String msg = "'applicationHref' property must be specified when using this builder implementation.";
            throw new IllegalArgumentException(msg);
        }

        String cleanedHref = href;

        int atSignIndex = href.indexOf('@');

        if (atSignIndex > 0) {

            String[] parts = getHrefWithUserInfo(href, atSignIndex);
            //parts[0] = up to and including the double slash
            //parts[1] = the raw user info
            //parts[2] = after the at sign

            cleanedHref = parts[0] + parts[2];

            parts = parts[1].split(":", 2);
            Properties apiKeyProperties = createApiKeyProperties(parts);
            setApiKeyProperties(apiKeyProperties);

        } //otherwise an apiKey File/Reader/InputStream/etc for the API Key is required

        Client client = buildClient();
        Application application = client.getDataStore().getResource(cleanedHref, Application.class);

        return new ClientApplication(client, application);
    }

    protected Client buildClient() {
        return this.clientBuilder.build();
    }

    protected String[] getHrefWithUserInfo(String href, int atSignIndex) {
        int doubleSlashIndex = href.indexOf(DOUBLE_SLASH);
        if (doubleSlashIndex <= 0) {
            throw new IllegalArgumentException("Invalid application href URL");
        }

        String[] parts = new String[3];

        parts[0] = href.substring(0, doubleSlashIndex + DOUBLE_SLASH.length()); //up to and including the double slash
        parts[1] = href.substring(doubleSlashIndex + DOUBLE_SLASH.length(), atSignIndex); //raw user info
        parts[2] = href.substring(atSignIndex + 1); //after the @ character

        return parts;
    }

    protected Properties createApiKeyProperties(String[] pair) {
        if (pair == null || pair.length != 2) {
            String msg = "applicationHref userInfo segment must consist of the following format: " +
                    "apiKeyId:apiKeySecret";
            throw new IllegalArgumentException(msg);
        }

        Properties props = new Properties();
        props.put("apiKey.id", urlDecode(pair[0]));
        props.put("apiKey.secret", urlDecode(pair[1]));

        return props;
    }


    protected String urlDecode(String s) {
        try {
            return urlDecode(s, ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to URL decode userInfo fragment: " + s, e);
        }
    }

    protected String urlDecode(String s, String encoding) throws UnsupportedEncodingException {
        return URLDecoder.decode(s, encoding);
    }
}
