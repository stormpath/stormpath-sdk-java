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
package com.stormpath.sdk.client;

import com.stormpath.sdk.cache.CacheManager;

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to
 * construct {@link com.stormpath.sdk.client.Client} instances.
 * <p/>
 * The {@code ClientBuilder} is especially useful for constructing Client instances with Stormpath API Key,
 * Proxy and Cache information.
 * <p/>
 * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
 * client as follows:
 * <pre>
 * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
 *
 * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setFileLocation(location).build();
 * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
 * </pre>
 * <p/>
 * @see ApiKeyBuilder
 * @since 1.0.beta
 */
public interface ClientBuilder {

    /**
     * Allows specifying an {@code ApiKey} instance directly instead of reading the key from a stream-based resource
     * (e.g. File, Reader, Properties or InputStream).
     * <p/>
     * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
     * client as follows:
     * <pre>
     * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
     *
     * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setFileLocation(location).build();
     * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
     * </pre>
     *
     * @param apiKey the ApiKey to use to authenticate requests to the Stormpath API server.
     * @return the ClientBuilder instance for method chaining.
     * @see ApiKeyBuilder#setId(String)
     * @deprecated in 1.0.RC and will be removed before 1.0 final. Use {@link #setApiKey(com.stormpath.sdk.api.ApiKey)} instead.
     */
    @Deprecated
    ClientBuilder setApiKey(ApiKey apiKey);

    /**
     * Allows specifying an {@code ApiKey} instance directly instead of reading the key from a stream-based resource
     * (e.g. File, Reader, Properties or InputStream).
     * <p/>
     * Assuming you stored your API Key in your home directory per Stormpath's instructions, you would create your
     * client as follows:
     * <pre>
     * String location = System.getProperty("user.home") + "/.stormpath/apiKey.properties";
     *
     * ApiKey apiKey = {@link ApiKeys ApiKeys}.builder().setFileLocation(location).build();
     * Client client = {@link Clients Clients}.builder().setApiKey(apiKey).build();
     * </pre>
     *
     * @param apiKey the ApiKey to use to authenticate requests to the Stormpath API server.
     * @return the ClientBuilder instance for method chaining.
     * @see ApiKeyBuilder#setId(String)
     */
    ClientBuilder setApiKey(com.stormpath.sdk.api.ApiKey apiKey);

    /**
     * Sets the HTTP proxy to be used when communicating with the Stormpath API server.
     *
     * @param proxy the {@code Proxy} you need to use.
     * @return the ClientBuilder instance for method chaining.
     */
    ClientBuilder setProxy(Proxy proxy);

    /**
     * Sets the {@link CacheManager} that should be used to cache Stormpath REST resources, reducing round-trips to the
     * Stormpath API server and enhancing application performance.
     * <p/>
     * <h3>Single JVM Applications</h3>
     * If your application runs on a single JVM-based applications, the
     * {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder} should be sufficient for your needs. You
     * create a {@code CacheManagerBuilder} by using the {@link com.stormpath.sdk.cache.Caches Caches} utility class,
     * for example:
     * <pre>
     * import static com.stormpath.sdk.cache.Caches.*;
     *
     * ...
     *
     * Client client = Clients.builder()...
     *     .setCacheManager(
     *         {@link com.stormpath.sdk.cache.Caches#newCacheManager() newCacheManager()}
     *         .withDefaultTimeToLive(1, TimeUnit.DAYS) //general default
     *         .withDefaultTimeToIdle(2, TimeUnit.HOURS) //general default
     *         .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Account.class) //Account-specific cache settings
     *             .withTimeToLive(1, TimeUnit.HOURS)
     *             .withTimeToIdle(30, TimeUnit.MINUTES))
     *         .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Group.class) //Group-specific cache settings
     *             .withTimeToLive(2, TimeUnit.HOURS))
     *         .build() //build the CacheManager
     *     )
     *     .build(); //build the Client
     * </pre>
     * <em>The above TTL and TTI times are just examples showing API usage - the times themselves are not
     * recommendations.  Choose TTL and TTI times based on your application requirements.</em>
     * <h3>Multi-JVM / Clustered Applications</h3>
     * The default {@code CacheManager} instances returned by the
     * {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder} might not be sufficient for a
     * multi-instance application that runs on multiple JVMs and/or hosts/servers, as there could be cache-coherency
     * problems across the JVMs.  See the {@link com.stormpath.sdk.cache.CacheManagerBuilder CacheManagerBuilder}
     * JavaDoc for additional information.
     * <p/>
     * In these multi-JVM environments, you will likely want to create a simple CacheManager implementation that wraps
     * your distributed Caching API/product of choice and then plug that implementation in to the Stormpath SDK via
     * this method.
     *
     * @param cacheManager the {@link CacheManager} that should be used to cache Stormpath REST resources, reducing
     *                     round-trips to the Stormpath API server and enhancing application performance.
     */
    ClientBuilder setCacheManager(CacheManager cacheManager);

    /**
     * Overrides the default (very secure)
     * <a href="http://docs.stormpath.com/rest/product-guide/#authentication-digest">Stormpath SAuthc1 Digest Authentication Scheme</a>
     * used to authenticate every request sent to the Stormpath API server.
     * <p/>
     * It is not recommended that you override this setting <em>unless</em> your application is deployed in an
     * environment that - outside of your application's control - manipulates request headers on outgoing HTTP requests.
     * Google App Engine is one such environment, for example.
     * <p/>
     * As such, in these environments only, an alternative authentication mechanism is necessary, such as
     * <a href="http://docs.stormpath.com/rest/product-guide/#authentication-basic">HTTP
     * Basic Authentication</a>.  You can enable Basic Authentication as follows (again, only do this if your
     * application runtime environment forces you to use it, like Google App Engine):
     * </pre>
     * Client client = Clients.builder()...
     *    // setApiKey, etc...
     *    .setAuthenticationScheme(AuthenticationScheme.BASIC) //set the basic authentication scheme
     *    .build(); //build the Client
     * </pre>
     *
     * @param authenticationScheme the type of authentication to be used for communication with the Stormpath API server.
     * @return the ClientBuilder instance for method chaining
     */
    ClientBuilder setAuthenticationScheme(AuthenticationScheme authenticationScheme);

    /**
     * Constructs a new {@link Client} instance based on the ClientBuilder's current configuration state.
     *
     * @return a new {@link Client} instance based on the ClientBuilder's current configuration state.
     */
    Client build();

}
