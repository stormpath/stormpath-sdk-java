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

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials;
import com.stormpath.sdk.impl.authc.credentials.ClientCredentialsProvider;
import com.stormpath.sdk.cache.CacheConfigurationBuilder;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.cache.CacheManagerBuilder;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.stormpath.sdk.impl.authc.RequestAuthenticatorFactory;
import com.stormpath.sdk.impl.authc.credentials.DefaultClientCredentialsProviderChain;
import com.stormpath.sdk.impl.config.*;
import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.DefaultResourceFactory;
import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.lang.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>The default {@link ClientBuilder} implementation. This looks for configuration files
 * in the following locations and order of precedence (last one wins).</p>
 * <ul>
 * <li>classpath:com/stormpath/sdk/config/stormpath.properties</li>
 * <li>classpath:stormpath.properties</li>
 * <li>classpath:stormpath.json</li>
 * <li>classpath:stormpath.yaml</li>
 * <li>~/.stormpath/stormpath.properties</li>
 * <li>~/.stormpath/stormpath.json</li>
 * <li>~/.stormpath/stormpath.yaml</li>
 * <li>~/stormpath.properties</li>
 * <li>~/stormpath.json</li>
 * <li>~/stormpath.yaml</li>
 * </ul>
 *
 * @since 1.0.alpha
 */
public class DefaultClientBuilder implements ClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(DefaultClientBuilder.class);

    private ApiKey apiKey;
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

    private ClientConfiguration clientConfig = new ClientConfiguration();

    public DefaultClientBuilder() {
        Collection<PropertiesSource> sources = new ArrayList<PropertiesSource>();
        ResourceFactory resourceFactory = new DefaultResourceFactory();

        for (String location : DEFAULT_STORMPATH_PROPERTIES_FILE_LOCATIONS) {
            Resource resource = resourceFactory.createResource(location);
            PropertiesSource propertiesSource = new OptionalPropertiesSource(new ResourcePropertiesSource(resource));
            sources.add(propertiesSource);
            // if location is a .properties file and it's not the first one, look for JSON and YAML equivalents
            if (!location.equals(DEFAULT_STORMPATH_PROPERTIES_FILE_LOCATIONS[0]) && location.endsWith(".properties")) {
                String jsonFile = location.replace(".properties", ".json");
                resource = resourceFactory.createResource(jsonFile);
                PropertiesSource jsonSource = new OptionalPropertiesSource(new JSONPropertiesSource(resource));
                sources.add(jsonSource);

                String yamlFile = location.replace(".properties", ".yaml");
                resource = resourceFactory.createResource(yamlFile);
                PropertiesSource yamlSource = new OptionalPropertiesSource(new YAMLPropertiesSource(resource));
                sources.add(yamlSource);
            }
        }

        Map<String, String> props = new LinkedHashMap<>();

        for (PropertiesSource source : sources) {
            Map<String, String> srcProps = source.getProperties();
            props.putAll(srcProps);
        }

        // check to see if property value is null before setting value
        // if != null, allow it to override previously set values
        if (props.get(DEFAULT_CLIENT_API_KEY_FILE_PROPERTY_NAME) != null) {
            String apiKeyFile = props.get(DEFAULT_CLIENT_API_KEY_FILE_PROPERTY_NAME);
            // remove backslashes that can end up in file when it's written programmatically, e.g. in a test
            apiKeyFile = apiKeyFile.replace("\\:", ":");
            clientConfig.setApiKeyFile(apiKeyFile);
        }

        if (props.get(DEFAULT_CLIENT_API_KEY_ID_PROPERTY_NAME) != null) {
            clientConfig.setApiKeyId(props.get(DEFAULT_CLIENT_API_KEY_ID_PROPERTY_NAME));
        }

        if (props.get(DEFAULT_CLIENT_API_KEY_SECRET_PROPERTY_NAME) != null) {
            clientConfig.setApiKeySecret(props.get(DEFAULT_CLIENT_API_KEY_SECRET_PROPERTY_NAME));
        }

        if (props.get(DEFAULT_CLIENT_CACHE_MANAGER_ENABLED_PROPERTY_NAME) != null) {
            clientConfig.setCacheManagerEnabled(Boolean.valueOf(props.get(DEFAULT_CLIENT_CACHE_MANAGER_ENABLED_PROPERTY_NAME)));
        }

        if (props.get(DEFAULT_CLIENT_CACHE_MANAGER_TTL_PROPERTY_NAME) != null) {
            clientConfig.setCacheManagerTtl(Long.valueOf(props.get(DEFAULT_CLIENT_CACHE_MANAGER_TTL_PROPERTY_NAME)));
        }

        if (props.get(DEFAULT_CLIENT_CACHE_MANAGER_TTI_PROPERTY_NAME) != null) {
            clientConfig.setCacheManagerTti(Long.valueOf(props.get(DEFAULT_CLIENT_CACHE_MANAGER_TTI_PROPERTY_NAME)));
        }

        for (String prop : props.keySet()) {
            boolean isPrefix = prop.length() == DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME.length();
            if (!isPrefix && prop.startsWith(DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME)) {
                // get class from prop name
                String cacheClass = prop.substring(DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME.length() + 1, prop.length() - 4);
                String cacheTti = props.get(DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME + "." + cacheClass + ".tti");
                String cacheTtl = props.get(DEFAULT_CLIENT_CACHE_MANAGER_CACHES_PROPERTY_NAME + "." + cacheClass + ".ttl");
                CacheConfigurationBuilder cacheBuilder = Caches.forResource(Classes.forName(cacheClass));
                if (cacheTti != null) {
                    cacheBuilder.withTimeToIdle(Long.valueOf(cacheTti), TimeUnit.SECONDS);
                }
                if (cacheTtl != null) {
                    cacheBuilder.withTimeToLive(Long.valueOf(cacheTtl), TimeUnit.SECONDS);
                }
                if (!clientConfig.getCacheManagerCaches().containsKey(cacheClass)) {
                    clientConfig.getCacheManagerCaches().put(cacheClass, cacheBuilder);
                }
            }
        }

        if (props.get(DEFAULT_CLIENT_BASE_URL_PROPERTY_NAME) != null) {
            String baseUrl = props.get(DEFAULT_CLIENT_BASE_URL_PROPERTY_NAME);
            // remove backslashes that can end up in file when it's written programmatically, e.g. in a test
            baseUrl = baseUrl.replace("\\:", ":");
            clientConfig.setBaseUrl(baseUrl);
        }

        if (props.get(DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME) != null) {
            clientConfig.setConnectionTimeout(Integer.valueOf(props.get(DEFAULT_CLIENT_CONNECTION_TIMEOUT_PROPERTY_NAME)));
        }

        if (props.get(DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME) != null) {
            clientConfig.setAuthenticationScheme(Enum.valueOf(AuthenticationScheme.class, props.get(DEFAULT_CLIENT_AUTHENTICATION_SCHEME_PROPERTY_NAME)));
        }

        if (props.get(DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME) != null) {
            clientConfig.setProxyPort(Integer.valueOf(props.get(DEFAULT_CLIENT_PROXY_PORT_PROPERTY_NAME)));
        }

        if (props.get(DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME) != null) {
            clientConfig.setProxyHost(props.get(DEFAULT_CLIENT_PROXY_HOST_PROPERTY_NAME));
        }

        if (props.get(DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME) != null) {
            clientConfig.setProxyUsername(props.get(DEFAULT_CLIENT_PROXY_USERNAME_PROPERTY_NAME));
        }

        if (props.get(DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME) != null) {
            clientConfig.setProxyPassword(props.get(DEFAULT_CLIENT_PROXY_PASSWORD_PROPERTY_NAME));
        }
    }

    @Override
    public ClientBuilder setApiKey(ApiKey apiKey) {
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
        this.clientConfig.setAuthenticationScheme(authenticationScheme);
        return this;
    }

    /* @since 1.0.RC3 */
    @Override
    public ClientBuilder setConnectionTimeout(int timeout) {
        Assert.isTrue(timeout >= 0, "Timeout cannot be a negative number.");
        this.clientConfig.setConnectionTimeout(timeout);
        return this;
    }

    @Override
    public ClientBuilder setRequestAuthenticatorFactory(Object factory) {
        Assert.isInstanceOf(RequestAuthenticatorFactory.class, factory);
        this.clientConfig.setRequestAuthenticatorFactory((RequestAuthenticatorFactory) factory);
        return this;
    }

    @Override
    public Client build() {
        if (!this.clientConfig.isCacheManagerEnabled()) {
            log.debug("CacheManager disabled. Defaulting to DisabledCacheManager");
            this.cacheManager = Caches.newDisabledCacheManager();
        } else if (this.cacheManager == null) {
            log.debug("No CacheManager configured. Defaulting to in-memory CacheManager with default TTL and TTI of five minutes.");

            CacheManagerBuilder cacheManagerBuilder = Caches.newCacheManager()
                    .withDefaultTimeToIdle(this.clientConfig.getCacheManagerTti(), TimeUnit.SECONDS)
                    .withDefaultTimeToLive(this.clientConfig.getCacheManagerTtl(), TimeUnit.SECONDS);
            if (this.clientConfig.getCacheManagerCaches().size() > 0) {
                for (CacheConfigurationBuilder builder : this.clientConfig.getCacheManagerCaches().values()) {
                    cacheManagerBuilder.withCache(builder);
                }
            }

            this.cacheManager = cacheManagerBuilder.build();
        }

        // use proxy overrides if they're set
        if (this.clientConfig.getProxyPort() > 0 || this.clientConfig.getProxyHost() != null &&
                (this.clientConfig.getProxyUsername() == null || this.clientConfig.getProxyPassword() == null)) {
            this.proxy = new Proxy(this.clientConfig.getProxyHost(), this.clientConfig.getProxyPort());
        } else if (this.clientConfig.getProxyUsername() != null && this.clientConfig.getProxyPassword() != null) {
            this.proxy = new Proxy(this.clientConfig.getProxyHost(), this.clientConfig.getProxyPort(),
                    this.clientConfig.getProxyUsername(), this.clientConfig.getProxyPassword());
        }

        ClientCredentials clientCredentials;

        if (this.apiKey != null) {
            clientCredentials = new ApiKeyCredentials(this.apiKey);
        } else {
            ClientCredentialsProvider clientCredentialsProvider = new DefaultClientCredentialsProviderChain(clientConfig);
            clientCredentials = clientCredentialsProvider.getClientCredentials();
        }

        return new DefaultClient(clientCredentials, this.clientConfig.getBaseUrl(), this.proxy, this.cacheManager,
                this.clientConfig.getAuthenticationScheme(), this.clientConfig.getRequestAuthenticatorFactory(), this.clientConfig.getConnectionTimeout());
    }

    @Override
    public ClientBuilder setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl argument cannot be null.");
        }
        this.clientConfig.setBaseUrl(baseUrl);
        return this;
    }

    // Used for testing, package private
    ClientConfiguration getClientConfiguration() {
        return clientConfig;
    }
}
