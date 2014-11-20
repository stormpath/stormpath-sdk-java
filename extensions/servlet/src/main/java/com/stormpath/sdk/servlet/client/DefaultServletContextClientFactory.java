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
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.cache.CacheManagerFactory;
import com.stormpath.sdk.servlet.cache.DefaultCacheManagerFactory;
import com.stormpath.sdk.servlet.cache.ServletContextAttributeCacheManager;
import com.stormpath.sdk.servlet.config.Config;

import javax.servlet.ServletContext;
import java.util.Map;

/** Default {@link ServletContextClientFactory} implementation. */
public class DefaultServletContextClientFactory implements ServletContextClientFactory {

    public static final String STORMPATH_API_KEY_FILE          = "stormpath.apiKey.file";
    public static final String STORMPATH_AUTHENTICATION_SCHEME = "stormpath.authentication.scheme";

    public static final String STORMPATH_PROXY_HOST     = "stormpath.proxy.host";
    public static final String STORMPATH_PROXY_PORT     = "stormpath.proxy.port";
    public static final String STORMPATH_PROXY_USERNAME = "stormpath.proxy.username";
    public static final String STORMAPTH_PROXY_PASSWORD = "stormpath.proxy.password";

    public static final String STORMPATH_APPLICATION_HREF = "stormpath.application.href";

    private final CacheManagerFactory cacheManagerFactory = new DefaultCacheManagerFactory();

    @Override
    public Client createClient(ServletContext servletContext) {

        Config config = (Config)servletContext.getAttribute(Config.class.getName());
        Assert.notNull(config, "Stormpath Config instance is not available in the ServletContext.  Ensure the " +
                               "ConfigLoader ServletContext Listener is defined before the ClientLoader Listener.");

        ClientBuilder builder = Clients.builder();

        applyApiKey(builder, config, servletContext);

        applyProxy(builder, config, servletContext);

        applyAuthenticationScheme(builder, config, servletContext);

        applyCacheManager(builder, config, servletContext);

        return builder.build();
    }

    protected void applyCacheManager(ClientBuilder builder, Config config, ServletContext servletContext) {

        CacheManager cacheManager = cacheManagerFactory.createCacheManager(config);

        if (cacheManager == null) {
            // no cache manager config was specified, assume a default, but allow the app developer to specify one
            // at runtime via a ServletContext attribute:
            cacheManager =
                new ServletContextAttributeCacheManager(servletContext, Caches.newCacheManager().build(), true);
        } else {
            //cache manager config was specified - a runtime version should not also be specified
            cacheManager = new ServletContextAttributeCacheManager(servletContext, cacheManager, false);
        }

        builder.setCacheManager(cacheManager);
    }

    protected void applyApiKey(ClientBuilder clientBuilder, Config config, ServletContext servletContext) {
        ApiKey apiKey = createApiKey(config, servletContext);
        clientBuilder.setApiKey(apiKey);
    }

    protected ApiKey createApiKey(final Config config, @SuppressWarnings("UnusedParameters") ServletContext servletContext) {

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

    protected void applyProxy(ClientBuilder builder, final Config config,
                              @SuppressWarnings("UnusedParameters") ServletContext servletContext) {

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

    protected void applyAuthenticationScheme(ClientBuilder builder, Map<String,String> props,
                                             @SuppressWarnings("UnusedParameters") ServletContext servletContext) {
        String schemeName = props.get(STORMPATH_AUTHENTICATION_SCHEME);
        if (Strings.hasText(schemeName)) {
            AuthenticationScheme scheme = AuthenticationScheme.valueOf(schemeName.toUpperCase());
            builder.setAuthenticationScheme(scheme);
        }
    }

}
