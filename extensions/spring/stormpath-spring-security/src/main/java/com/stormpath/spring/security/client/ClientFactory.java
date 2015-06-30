/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.spring.security.client;

import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.spring.security.cache.SpringCacheManager;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * A simple factory that allows a Stormpath SDK Client to be created via Spring's
 * {@link org.springframework.beans.factory.config.AbstractFactoryBean} concept.
 * <p/>
 * As this class is a simple bridge between APIs, it does not do much - all configuration properties are immediately
 * passed through to an internal {@link com.stormpath.sdk.client.ClientBuilder ClientBuilder} instance, and the
 * {@link #createInstance()} implementation merely calls {@link com.stormpath.sdk.client.ClientBuilder#build()}.
 * <h2>Usage</h2>
 * Example {@code spring.xml} configuration:
 * <p/>
 * <pre>
 * <bean id="stormpathClient" class="com.stormpath.spring.security.client.ClientFactory" >
 * <property name="apiKeyFileLocation" value="#{systemProperties['user.home']}/.stormpath/apiKey.properties" />
 * </bean>
 *
 * ...
 * </pre>
 *
 * @see ClientBuilder
 * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
 * @see com.stormpath.sdk.api.ApiKeyBuilder#setFileLocation(String)
 */
public class ClientFactory extends AbstractFactoryBean<Client> {

    private ClientBuilder clientBuilder;
    private ApiKey apiKey;

    public ClientFactory() {
        super();
        this.clientBuilder = Clients.builder();
    }

    @Override
    protected Client createInstance() throws Exception {
        return clientBuilder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    public ClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public void setClientBuilder(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setFileLocation(String) setFileLocation(apiKeyFileLocation)}{@code .build())}.
     * See that JavaDoc for expected syntax/format.
     *
     * @param apiKeyFileLocation the file, classpath or url location of the API Key {@code .properties} file to load when
     *                           constructing the API Key to use for communicating with the Stormpath REST API.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setFileLocation(String)
     * @since 0.2
     */
    public void setApiKeyFileLocation(String apiKeyFileLocation) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setFileLocation(apiKeyFileLocation).build());
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setInputStream(java.io.InputStream) setInputStream(apiKeyInputStream)}{@code .build())}.
     *
     * @param apiKeyInputStream the InputStream to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setInputStream(java.io.InputStream)
     * @since 0.2
     */
    public void setApiKeyInputStream(InputStream apiKeyInputStream) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setInputStream(apiKeyInputStream).build());
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setReader(java.io.Reader) setReader(apiKeyReader)}{@code .build())}.
     *
     * @param apiKeyReader the reader to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setReader(java.io.Reader)
     * @since 0.2
     */
    public void setApiKeyReader(Reader apiKeyReader) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setReader(apiKeyReader).build());
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setProperties(java.util.Properties) setProperties(properties)}{@code .build())}.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setProperties(java.util.Properties)
     * @since 0.2
     */
    public void setApiKeyProperties(Properties properties) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setProperties(properties).build());
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setIdPropertyName(String) setIdPropertyName(apiKeyIdPropertyName)}{@code .build())}.
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setIdPropertyName(String)
     * @since 0.2
     */
    public void setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setIdPropertyName(apiKeyIdPropertyName).build());
    }

    /**
     * Calls {@code clientBuilder.setApiKey(ApiKeys.builder().}{@link com.stormpath.sdk.api.ApiKeyBuilder#setSecretPropertyName(String) setSecretPropertyName(apiKeySecretPropertyName)}{@code .build())}.
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @see ClientBuilder#setApiKey(com.stormpath.sdk.api.ApiKey)
     * @see com.stormpath.sdk.api.ApiKeyBuilder#setSecretPropertyName(String)
     * @since 0.2
     */
    public void setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.clientBuilder.setApiKey(ApiKeys.builder().setSecretPropertyName(apiKeySecretPropertyName).build());
    }

    /**
    * Uses the specified Spring {@link CacheManager} instance as the Stormpath SDK Client's CacheManager, allowing both
    * Spring and Stormpath SDK to share the same cache mechanism.
    * <p/>
    * If for some reason you don't want to share the same cache mechanism, you can explicitly set a Stormpath SDK-only
    * {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance via the
    * {@link #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager) setStormpathCacheManager} method.
    *
    * @param cacheManager the Spring CacheManager to use for the Stormpath SDK Client's caching needs.
    * @since 0.2.0
    * @see #setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager)
    */
    public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
        CacheManager stormpathCacheManager = new SpringCacheManager(cacheManager);
        this.clientBuilder.setCacheManager(stormpathCacheManager);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setCacheManager(com.stormpath.sdk.cache.CacheManager) setCacheManager}
     * using the specified Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance, but <b>note:</b>
     * This method should only be used if the Stormpath SDK should use a <em>different</em> CacheManager than what
     * Spring uses.
     * <p/>
     * If you prefer that Spring and the Stormpath SDK use the same cache mechanism to reduce complexity/configuration,
     * configure your preferred Spring {@code cacheManager} first and then use that cacheManager by calling the
     * {@link #setCacheManager(org.springframework.cache.CacheManager) setCacheManager(springCacheManager)} method
     * instead of this one.
     *
     * @param cacheManager the Storpmath SDK-specific CacheManager to use for the Stormpath SDK Client's caching needs.
     * @since 0.2.0
     * @see #setCacheManager(org.springframework.cache.CacheManager)
     */
    public void setStormpathCacheManager(com.stormpath.sdk.cache.CacheManager cacheManager) {
        this.clientBuilder.setCacheManager(cacheManager);
    }
}