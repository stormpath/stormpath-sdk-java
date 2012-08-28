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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> implementation similar to
 * the {@link ClientBuilder}, but focused on single-application interaction with Stormpath.
 * <h2>Description</h2>
 * The {@code ClientBuilder} produces a {@link Client} instance useful for interacting with any aspect
 * of an entire Stormpath Tenant's data space.  However, a software application may only be interested in its own
 * functionality and not the entire Stormpath Tenant data space.
 * <p/>
 * The {@code ClientApplicationBuilder} provides a means to more easily acquiring a single
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
 *     .setApiKeyFileLocation(apiKeyFileLocation)
 *     <b>.setApplicationHref(appHref)</b>
 *     .buildApplication().getApplication();
 * </pre>
 * <p/>
 * After acquiring the {@code application} instance, you can interact with it to login accounts, reset passwords,
 * etc.
 * <h2>Service Provider Usage with only an Application URL</h2>
 * Some hosting service providers (e.g. like <a href="http://www.heroku.com">Heroku</a>) do not allow easy access to
 * a a configuration file and therefore it might be difficult to reference an API Key File.  If you cannot reference an
 * API Key File via the {@code file:}, {@code classpath:} or {@code url:}
 * {@link ClientBuilder#setApiKeyFileLocation(String) resource locations}, the Application HREF URL must contain the
 * API Key embedded as the <em><a href="http://en.wikipedia.org/wiki/URI_scheme">user info</a></em> portion of the
 * URL.  For example:
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
 *     .buildApplication().getApplication();
 * </pre>
 * <p/>
 * <b>WARNING: ONLY use the embedded API Key technique if you do not have access to {@code file:}, {@code classpath:}
 * or {@code url:} {@link ClientBuilder#setApiKeyFileLocation(String) resource locations}</b>.  File based API Key
 * storage is a more secure technique than embedding the key in the URL itself.  Also, again, NEVER share your API Key
 * Secret with <em>anyone</em> (not even co-workers).  Stormpath staff will never ask for your API Key Secret.
 *
 * @see #setApiKeyFileLocation(String)
 * @see #setApplicationHref(String)
 * @since 0.5
 */
public class ClientApplicationBuilder extends ClientBuilder {

    private static final String DOUBLE_SLASH = "//";
    private static final String ENCODING = "UTF-8";

    private String applicationHref;

    public ClientApplicationBuilder() {
    }

    /**
     * Sets the fully qualified Stormpath Application HREF (a URL) to use to acquire the Application instance when
     * {@link #buildApplication()} is called.  See the Class-level JavaDoc for usage scenarios.
     *
     * @param applicationHref the fully qualified Stormpath Application HREF (a URL) to use to acquire the
     *                        Application instance when {@link #buildApplication()} is called.
     */
    public void setApplicationHref(String applicationHref) {
        this.applicationHref = applicationHref;
    }

    /**
     * Builds a Client and Application wrapper instance based on the configured
     * {@link #setApplicationHref(String) applicationHref}. See the Class-level JavaDoc for usage scenarios.
     *
     * @return a Client and Application wrapper instance based on the configured {@link #setApplicationHref(String) applicationHref}.
     */
    public ClientApplication buildApplication() {

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

        Client client = build();
        Application application = client.getDataStore().getResource(cleanedHref, Application.class);

        return new ClientApplication(client, application);
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
