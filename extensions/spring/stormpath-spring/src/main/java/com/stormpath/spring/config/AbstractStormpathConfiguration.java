/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.spring.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.spring.cache.SpringCacheManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractStormpathConfiguration {

    private static final String APP_HREF_ERROR =
        "A 'stormpath.application.href' property value must be configured if you have more than one application " +
        "registered in Stormpath.";

    @Autowired(required = false)
    protected CacheManager cacheManager;

    @Value("#{ @environment['stormpath.baseUrl'] }")
    protected String baseUrl;

    @Value("#{ @environment['stormpath.apiKey.id'] }")
    protected String apiKeyId;

    @Value("#{ @environment['stormpath.apiKey.secret'] }")
    protected String apiKeySecret;

    @Value("#{ @environment['stormpath.apiKey.file'] }")
    protected Resource apiKeyFile;

    @Value("#{ @environment['stormpath.apiKey.fileIdPropertyName'] }")
    protected String apiKeyFileIdPropertyName;

    @Value("#{ @environment['stormpath.apiKey.fileSecretPropertyName'] }")
    protected String apiKeyFileSecretPropertyName;

    @Value("#{ @environment['stormpath.application.href'] }")
    protected String applicationHref;

    @Value("#{ @environment['stormpath.cache.enabled'] ?: true }")
    protected boolean cachingEnabled;

    @Value("#{ @environment['stormpath.proxy.host'] }")
    protected String proxyHost;

    @Value("#{ @environment['stormpath.proxy.port'] ?: 80 }")
    protected int proxyPort;

    @Value("#{ @environment['stormpath.proxy.username'] }")
    protected String proxyUsername;

    @Value("#{ @environment['stormpath.proxy.password'] }")
    protected String proxyPassword;

    @Value("#{ @environment['stormpath.connectionTimeout'] ?: 0 }")
    protected int connectionTimeout;

    @Value("#{ @environment['stormpath.authentication.scheme'] }")
    protected AuthenticationScheme authenticationScheme;

    public ApiKey stormpathClientApiKey() {

        ApiKeyBuilder builder = ApiKeys.builder();

        if (Strings.hasText(apiKeyFileIdPropertyName)) {
            builder.setIdPropertyName(apiKeyFileIdPropertyName);
        }
        if (Strings.hasText(apiKeyFileSecretPropertyName)) {
            builder.setSecretPropertyName(apiKeyFileSecretPropertyName);
        }
        if (apiKeyFile != null) {
            try {
                builder.setInputStream(apiKeyFile.getInputStream());
            } catch (IOException e) {
                String msg = "Unable to acquire specified resource [" + apiKeyFile + "]: " + e.getMessage();
                throw new BeanCreationException(msg, e);
            }
        }

        if (Strings.hasText(apiKeyId)) {
            builder.setId(apiKeyId);
        }
        if (Strings.hasText(apiKeySecret)) {
            builder.setSecret(apiKeySecret);
        }

        return builder.build();
    }

    public Application stormpathApplication() {

        Client client = stormpathClient();

        if (Strings.hasText(applicationHref)) {
            return client.getResource(applicationHref, Application.class);
        }

        //otherwise no href configured - try to find an application:

        Application single = null;

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //ignore the admin app
                continue;
            }
            if (single != null) {
                //there is more than one application in the tenant, and we can't infer which one should be used
                //for this particular application.  Let them know:
                throw new IllegalStateException(APP_HREF_ERROR);
            }
            single = app;
        }

        return single;
    }

    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {

        if (!cachingEnabled) {
            return Caches.newDisabledCacheManager();
        }

        if (cacheManager != null) {
            return new SpringCacheManager(cacheManager);
        }

        //otherwise no Spring CacheManager - create a default:
        return Caches.newCacheManager().withDefaultTimeToLive(1, TimeUnit.HOURS)
                     .withDefaultTimeToIdle(1, TimeUnit.HOURS).build();
    }

    protected Proxy resolveProxy() {

        if (!Strings.hasText(proxyHost)) {
            return null;
        }

        Proxy proxy;

        if (Strings.hasText(proxyUsername) || Strings.hasText(proxyPassword)) {
            proxy = new Proxy(proxyHost, proxyPort, proxyUsername, proxyPassword);
        } else {
            proxy = new Proxy(proxyHost, proxyPort);
        }

        return proxy;
    }

    public Client stormpathClient() {

        ClientBuilder builder = Clients.builder()
                                       .setApiKey(stormpathClientApiKey())
                                       .setCacheManager(stormpathCacheManager());

        if (authenticationScheme != null) {
            builder.setAuthenticationScheme(authenticationScheme);
        }

        if (Strings.hasText(baseUrl)) {
            builder.setBaseUrl(baseUrl);
        }

        Proxy proxy = resolveProxy();
        if (proxy != null) {
            builder.setProxy(proxy);
        }

        if (connectionTimeout > 0) {
            builder.setConnectionTimeout(connectionTimeout);
        }

        return builder.build();
    }

}
