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
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.spring.cache.SpringCacheManager;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * A Spring {@link org.springframework.beans.factory.FactoryBean FactoryBean} that produces a singleton {@link Client}
 * instance that will be used to communicate with the Stormpath REST API.
 *
 * <p>Each client must be configured with an {@link com.stormpath.sdk.api.ApiKey ApiKey} to authenticate with the REST
 * API.  Consider using the {@link com.stormpath.spring.config.ClientApiKeyFactoryBean ClientApiKeyFactoryBean} or
 * {@link com.stormpath.sdk.api.ApiKeys#builder() ApiKeys.builder()} to acquire the ApiKey that will be used with the
 * client.</p>
 *
 * @see com.stormpath.spring.config.ClientApiKeyFactoryBean ClientApiKeyFactoryBean
 * @see com.stormpath.sdk.api.ApiKeys#builder() ApiKeys.builder()
 * @since 1.0.RC4
 */
public class ClientFactoryBean extends AbstractFactoryBean<Client> {

    private ClientBuilder builder = Clients.builder();

    /**
     * Sets the base URL of the Stormpath REST API to use.  If unspecified, this value defaults to {@code
     * https://api.stormpath.com/v1} - the most common use case for Stormpath's public SaaS cloud.
     *
     * <p>Customers using Stormpath's Enterprise HA cloud might need to configure this to be {@code
     * https://enterprise.stormpath.io/v1} for example.</p>
     *
     * @param baseUrl the base URL of the Stormpath REST API to use.
     */
    public void setBaseUrl(String baseUrl) {
        builder.setBaseUrl(baseUrl);
    }

    /**
     * Overrides the default (very secure) <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest">Stormpath
     * SAuthc1 Digest Authentication Scheme</a> used to authenticate every request sent to the Stormpath API server.
     *
     * <p>It is not recommended that you override this setting <em>unless</em> your application is deployed in an
     * environment that - outside of your application's control - manipulates request headers on outgoing HTTP requests.
     * Google App Engine is one such environment, for example.</p>
     *
     * <p>As such, in these environments only, an alternative authentication mechanism is necessary, such as <a
     * href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP Basic Authentication</a>.  You can
     * enable Basic Authentication as follows (again, only do this if your application runtime environment forces you to
     * use it, like Google App Engine):</p>
     *
     * <pre>
     * Client client = Clients.builder()...
     *    // setApiKey, etc...
     *    .setAuthenticationScheme(AuthenticationScheme.BASIC) //set the basic authentication scheme
     *    .build(); //build the Client
     * </pre>
     *
     * @param authenticationScheme the type of authentication to be used for communication with the Stormpath API
     *                             server.
     */
    public void setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        builder.setAuthenticationScheme(authenticationScheme);
    }

    /**
     * Sets both the timeout until a connection is established and the socket timeout (i.e. a maximum period of
     * inactivity between two consecutive data packets). A timeout value of zero is interpreted as an infinite timeout.
     *
     * @param timeout connection and socket timeout in milliseconds
     */
    public void setConnectionTimeout(int timeout) {
        builder.setConnectionTimeout(timeout);
    }

    /**
     * Sets the ApiKey to use to authenticate communication with the Stormpath REST API.  This is another bean often
     * constructed with the {@link com.stormpath.spring.config.ClientApiKeyFactoryBean} or via an {@link
     * com.stormpath.sdk.api.ApiKeys#builder() ApiKeys.builder()}.
     *
     * @param apiKey the ApiKey to use to authenticate communication with the Stormpath REST API.
     */
    public void setApiKey(ApiKey apiKey) {
        builder.setApiKey(apiKey);
    }

    /**
     * Uses the specified Spring {@link org.springframework.cache.CacheManager CacheManager} instance as the Stormpath
     * SDK Client's CacheManager, allowing both Spring and Stormpath SDK to share the same cache mechanism.
     *
     * <p> If for some reason you don't want to share the same cache mechanism, you can explicitly set a Stormpath
     * SDK-only {@link com.stormpath.sdk.cache.CacheManager} instance via the {@link
     * #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager) setStormpathCacheManager} method.</p>
     *
     * @param cacheManager the Spring CacheManager to use for the Stormpath SDK Client's caching needs.
     * @see #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager)
     */
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        CacheManager stormpathCacheManager = new SpringCacheManager(cacheManager);
        builder.setCacheManager(stormpathCacheManager);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setCacheManager(com.stormpath.sdk.cache.CacheManager)
     * setCacheManager} using the specified Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager}
     * instance, but <b>note:</b> This method should only be used if the Stormpath SDK should use a <em>different</em>
     * CacheManager than what Spring uses.
     *
     * <p>If you prefer that Spring and the Stormpath SDK use the same cache mechanism to reduce
     * complexity/configuration, configure your preferred Spring {@code cacheManager} first and then use that
     * cacheManager by calling the {@link #setCacheManager(org.springframework.cache.CacheManager)
     * setCacheManager(springCacheManager)} method instead of this one.</p>
     *
     * @param cacheManager the Storpmath SDK-specific CacheManager to use for the Stormpath SDK Client's caching needs.
     * @see #setCacheManager(org.springframework.cache.CacheManager)
     */
    public void setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager cacheManager) {
        builder.setCacheManager(cacheManager);
    }

    /**
     * Sets the proxy settings the client should use if it is necessary to communicate through an HTTP Proxy to the
     * Stormpath REST API.
     *
     * @param proxy the proxy settings the client should use if it is necessary to communicate through an HTTP Proxy to
     *              the Stormpath REST API.
     */
    public void setProxy(Proxy proxy) {
        builder.setProxy(proxy);
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    @Override
    protected Client createInstance() throws Exception {
        return builder.build();
    }
}
