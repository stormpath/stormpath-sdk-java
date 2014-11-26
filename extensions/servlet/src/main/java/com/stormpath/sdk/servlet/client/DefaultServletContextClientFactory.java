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
package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.cache.DisabledCacheManager;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/** Default {@link ServletContextClientFactory} implementation. */
public class DefaultServletContextClientFactory implements ServletContextClientFactory {

    public static final String STORMPATH_API_KEY_FILE = "stormpath.apiKey.file";
    public static final String STORMPATH_AUTHENTICATION_SCHEME = "stormpath.authentication.scheme";

    public static final String STORMPATH_CACHE_MANAGER = "stormpath.cache.manager";

    public static final String STORMPATH_PROXY_HOST = "stormpath.proxy.host";
    public static final String STORMPATH_PROXY_PORT = "stormpath.proxy.port";
    public static final String STORMPATH_PROXY_USERNAME = "stormpath.proxy.username";
    public static final String STORMAPTH_PROXY_PASSWORD = "stormpath.proxy.password";

    public static final String STORMPATH_APPLICATION_HREF = "stormpath.application.href";

    private Config config;
    private ServletContext servletContext;
    private ConfigResolver configResolver = ConfigResolver.INSTANCE;

    protected Config getConfig() {
        return config;
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public Client createClient(ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");
        this.servletContext = servletContext;

        this.config = configResolver.getConfig(servletContext);

        ClientBuilder builder = Clients.builder();

        applyApiKey(builder);

        applyProxy(builder);

        applyAuthenticationScheme(builder);

        applyCacheManager(builder);

        return builder.build();
    }

    protected void applyCacheManager(ClientBuilder builder) {

        CacheManager cacheManager;

        try {
            cacheManager = config.getInstance(STORMPATH_CACHE_MANAGER);
        } catch (ServletException e) {
            String msg = "Unable to get CacheManager instance from Config: " + e.getMessage();
            throw new IllegalStateException(msg, e);
        }

        Assert.notNull(cacheManager, "Configured CacheManager cannot be null.  If you want to disable caching " +
                                     "entirely, configure a " + DisabledCacheManager.class.getName() + " instead.");

        builder.setCacheManager(cacheManager);
    }

    protected void applyApiKey(ClientBuilder clientBuilder) {
        ApiKey apiKey = createApiKey();
        clientBuilder.setApiKey(apiKey);
    }

    protected ApiKey createApiKey() {

        ApiKeyBuilder apiKeyBuilder = ApiKeys.builder();

        String value = config.get("stormpath.apiKey.id");
        if (Strings.hasText(value)) {
            apiKeyBuilder.setId(value);
        }

        //check for API Key ID embedded in the properties configuration
        value = config.get("stormpath.apiKey.secret");
        if (Strings.hasText(value)) {
            apiKeyBuilder.setSecret(value);
        }

        value = config.get(STORMPATH_API_KEY_FILE);
        if (Strings.hasText(value)) {
            apiKeyBuilder.setFileLocation(value);
        }

        return apiKeyBuilder.build();
    }

    protected void applyProxy(ClientBuilder builder) {

        String proxyHost = config.get(STORMPATH_PROXY_HOST);
        if (!Strings.hasText(proxyHost)) {
            return;
        }

        //otherwise, proxy config is present:

        Proxy proxy;

        int port = 80; //default
        String portValue = config.get(STORMPATH_PROXY_PORT);
        if (Strings.hasText(portValue)) {
            port = Integer.parseInt(portValue);
        }

        String proxyUsername = config.get(STORMPATH_PROXY_USERNAME);
        String proxyPassword = config.get(STORMAPTH_PROXY_PASSWORD);

        if (Strings.hasText(proxyUsername) || Strings.hasText(proxyPassword)) {
            proxy = new Proxy(proxyHost, port, proxyUsername, proxyPassword);
        } else {
            proxy = new Proxy(proxyHost, port);
        }

        builder.setProxy(proxy);
    }

    protected void applyAuthenticationScheme(ClientBuilder builder) {
        String schemeName = config.get(STORMPATH_AUTHENTICATION_SCHEME);
        if (Strings.hasText(schemeName)) {
            AuthenticationScheme scheme = AuthenticationScheme.valueOf(schemeName.toUpperCase());
            builder.setAuthenticationScheme(scheme);
        }
    }
}
