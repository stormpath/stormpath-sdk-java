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

import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.api.ClientApiKeyBuilder;
import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.DefaultResourceFactory;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * The default {@link ClientBuilder} implementation.
 *
 * @since 1.0.alpha
 */
public class DefaultClientBuilder implements ClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(DefaultClientBuilder.class);

    private com.stormpath.sdk.api.ApiKey apiKey;
    private Proxy proxy;
    private CacheManager cacheManager;

    private static final String USER_HOME = System.getProperty("user.home") + File.separatorChar;
    private static final String STORMPATH_PROPERTIES = "stormpath.properties";
    private static final String[] DEFAULT_STORMPATH_PROPERTIES_FILE_LOCATIONS = {
            ClasspathResource.SCHEME_PREFIX + "com/stormpath/sdk/config/" + STORMPATH_PROPERTIES,
            ClasspathResource.SCHEME_PREFIX + STORMPATH_PROPERTIES,
            USER_HOME + ".stormpath" + File.separatorChar + STORMPATH_PROPERTIES,
            USER_HOME + STORMPATH_PROPERTIES
    };

    private String apiKeyFile;
    private String apiKeyId;
    private String apiKeySecret;
    private long cacheManagerTtl;
    private long cacheManagerTti;
    private String cacheManagerCaches;
    private String baseUrl;
    private int connectionTimeout;
    private AuthenticationScheme authenticationScheme;
    private int proxyPort;
    private String proxyHost;
    private String proxyUsername;
    private String proxyPassword;
    private String applicationName;
    private String applicationHref;

    public DefaultClientBuilder() {
        for (String location : DEFAULT_STORMPATH_PROPERTIES_FILE_LOCATIONS) {
            Properties props = getPropertiesFromFile(location);

            // check to see if property value is null before setting value
            // if != null, allow it to override previously set values
            if (getPropertyValue(props, DEFAULT_CLIENT_API_KEY_FILE_PROPERTY_NAME) != null) {
                apiKeyFile = getPropertyValue(props, DEFAULT_CLIENT_API_KEY_FILE_PROPERTY_NAME);
            } else {
                // todo: support json and yaml overrides
                apiKeyFile = ClientApiKeyBuilder.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION;
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_API_KEY_ID_PROPERTY_NAME) != null) {
                apiKeyId = getPropertyValue(props, DEFAULT_CLIENT_API_KEY_ID_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_API_KEY_SECRET_PROPERTY_NAME) != null) {
                apiKeySecret = getPropertyValue(props, DEFAULT_CLIENT_API_KEY_SECRET_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_TTL_PROPERTY_NAME) != null) {
                cacheManagerTtl = Long.valueOf(getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_TTL_PROPERTY_NAME));
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_TTI_PROPERTY_NAME) != null) {
                cacheManagerTti = Long.valueOf(getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_TTI_PROPERTY_NAME));
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME) != null) {
                cacheManagerCaches = getPropertyValue(props, DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_BASE_URL_PROPERTY_NAME) != null) {
                baseUrl = getPropertyValue(props, DEFAULT_CLIENT_BASE_URL_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME) != null) {
                connectionTimeout = Integer.valueOf(getPropertyValue(props, DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME)) * 1000;
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME) != null) {
                authenticationScheme = Enum.valueOf(AuthenticationScheme.class, getPropertyValue(props, DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME));
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME) != null) {
                proxyPort = Integer.valueOf(getPropertyValue(props, DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME));
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME) != null) {
                proxyHost = getPropertyValue(props, DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME) != null) {
                proxyUsername = getPropertyValue(props, DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME) != null) {
                proxyPassword = getPropertyValue(props, DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_APPLICATION_NAME_PROPERTY_NAME) != null) {
                applicationName = getPropertyValue(props, DEFAULT_APPLICATION_NAME_PROPERTY_NAME);
            }

            if (getPropertyValue(props, DEFAULT_APPLICATION_HREF_PROPERTY_NAME) != null) {
                applicationHref = getPropertyValue(props, DEFAULT_APPLICATION_HREF_PROPERTY_NAME);
            }
        }
    }

    @Override
    public ClientBuilder setApiKey(ApiKey apiKey) {
        return setApiKey((com.stormpath.sdk.api.ApiKey) apiKey);
    }

    @Override
    public ClientBuilder setApiKey(com.stormpath.sdk.api.ApiKey apiKey) {
        Assert.notNull(apiKey, "apiKey cannot be null.");
        this.apiKey = apiKey;
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

    /* @since 1.0.RC3 */
    @Override
    public ClientBuilder setConnectionTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout cannot be a negative number.");
        this.connectionTimeout = timeout;
        return this;
    }

    @Override
    public Client build() {
        if (this.apiKey == null) {
            log.debug("No API Key configured. Attempting to acquire an API Key found from well-known locations ($HOME/.stormpath/apiKey.properties < environment variables < system properties)...");
            this.apiKey = ApiKeys.builder().build();
        }

        // todo: if apiKeyFile, apiKeyId or apiKeySecret is set, use them

        Assert.state(this.apiKey != null,
                "No ApiKey has been set. It is required to properly build the Client. See 'setApiKey(ApiKey)'.");

        if (this.cacheManager == null) {
            log.debug("No CacheManager configured.  Defaulting to in-memory CacheManager with default TTL and TTI of " +
                    "one hour.");
            this.cacheManager = Caches.newCacheManager()
                    .withDefaultTimeToIdle(cacheManagerTti, TimeUnit.SECONDS)
                    .withDefaultTimeToLive(cacheManagerTtl, TimeUnit.SECONDS)
                    .build();
        }

        if (this.proxyPort > 0 || this.proxyHost != null && (this.proxyUsername == null || this.proxyPassword == null)) {
            this.proxy = new Proxy(this.proxyHost, this.proxyPort);
        } else if (this.proxyUsername != null && this.proxyPassword != null) {
            this.proxy = new Proxy(this.proxyHost, this.proxyPort, this.proxyUsername, this.proxyPassword);
        }

        // todo: allow applicationName and applicationHref to override the default application

        return new DefaultClient(this.apiKey, this.baseUrl, this.proxy, this.cacheManager, this.authenticationScheme, this.connectionTimeout);
    }

    @Override
    public ClientBuilder setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl argument cannot be null.");
        }
        this.baseUrl = baseUrl;
        return this;
    }

    private ResourceFactory resourceFactory = new DefaultResourceFactory();

    private Properties getPropertiesFromFile(String fileLocation) {
        Properties props = new Properties();

        try {
            log.debug("fileLocation: {}", fileLocation);
            Reader reader = createFileReader(fileLocation);
            props = toProperties(reader);
        } catch (IOException ignored) {
            log.debug(
                    "Unable to find or load default configuration file [{}]. " +
                            "This can be safely ignored as this is a fallback location - " +
                            "other more specific locations will be checked.",
                    fileLocation, ignored
            );
        }
        return props;
    }

    private static Properties toProperties(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return properties;
    }

    private Reader createFileReader(String fileLocation) throws IOException {
        InputStream is = this.resourceFactory.createResource(fileLocation).getInputStream();
        return toReader(is);
    }

    private static Reader toReader(InputStream is) throws IOException {
        if (is == null) {
            throw new FileNotFoundException();
        }
        return new InputStreamReader(is, "ISO-8859-1");
    }

    private static String getPropertyValue(Properties properties, String propName) {
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
