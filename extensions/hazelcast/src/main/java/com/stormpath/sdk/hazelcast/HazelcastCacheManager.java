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
package com.stormpath.sdk.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.lang.Assert;

import java.util.concurrent.ConcurrentMap;

/**
 * A {@code CacheManager} implementation that manages and returns {@link Cache} instances backed by
 * <a href="http://hazelcast.org/docs/3.2/manual/html-single/hazelcast-documentation.html#map">Hazelcast Maps</a>. Each
 * Hazelcast map instance is treated as a 'cache region', and, by default, each region is named after the fully
 * qualified interface name of the resource cached, for example,
 * {@code com.stormpath.sdk.account.Account}, {@code com.stormpath.sdk.group.Group}.  This allows you to define and
 * configure Hazelcast maps based on the name of the type of data being stored, which is often the most common way of
 * configuring data caching policies.
 *
 * <h3>Usage</h3>
 *
 * <p>Setting up and configuring a Hazelcast cluster is outside the scope of this SDK and its documentation, but the
 * Hazelcast team provides great
 * <a href="http://hazelcast.org/docs/latest/manual/html-single/hazelcast-documentation.html">Hazelcast Documentation
 * </a>.  It is especially important to consult the Hazelcast documentation to understand how to
 * specify Time-To-Live and Time-To-Idle configuration for your Stormpath SDK cache regions, as well as perhaps to
 * enable advanced features like eviction policies and maybe even persistent backup.</p>
 *
 * <p>Once your Hazelcast environment is accessible, you should obtain a {@link HazelcastInstance} per the Hazelcast
 * documentation.  Then you would specify that instance when constructing your Stormpath SDK Client.  For example:</p>
 *
 * <pre>
 * HazelcastInstance hazelcast = getMyHazelcastInstance(); //get or create your Hazelcast instance from somewhere
 *
 * <b>CacheManager cacheManager = new HazelcastCacheManager(hazelcast);</b>
 *
 * Client client = {@link com.stormpath.sdk.client.Clients Clients}.builder()<b>.setCacheManager(cacheManager)</b>.build();
 * </pre>
 *
 * <p>When the client is constructed, it will then use Hazelcast for all its caching needs.</p>
 *
 * <h3>HazelcastClient or HazelcastInstance?</h3>
 *
 * <p>It should be noted that if your application acts as a pure client to a Hazelcast cluster - that is, it is not a
 * peer member of the Hazelcast cluster itself, but only a client of the cluster - that configuring a
 * {@code HazelcastClient} instance is likely the preferred type to use in your application code.</p>
 *
 * <p>However, note that {@code HazelcastClient} implements the {@code HazelcastInstance} interface.  This means that
 * you can use <em>either</em> a {@code HazelcastClient} instance or a server/peer {@code HazelcastInstance} runtime
 * instance when creating a {@code HazelcastCacheManager}.  Either will work fine depending on your application needs.
 * For example, either of these two code samples will work depending on how you choose to access Hazelcast:</p>
 *
 * <pre>
 * HazelcastInstance hazelcast = getMyHazelcastInstance(); //get or create your Hazelcast instance from somewhere
 * <b>CacheManager cacheManager = new HazelcastCacheManager(hazelcast);</b>
 * Client stormpathClient = {@link com.stormpath.sdk.client.Clients Clients}.builder()<b>.setCacheManager(cacheManager)</b>.build();
 * </pre>
 *
 * <p>or:</p>
 *
 * <pre>
 * HazelcastClient hazelcastClient = getMyHazelcastClientInstance(); //get or create your HazelcastClient instance from
 * somewhere
 * <b>CacheManager cacheManager = new HazelcastCacheManager(hazelcastClient);</b>
 * Client stormpathClient = {@link com.stormpath.sdk.client.Clients Clients}.builder()<b>.setCacheManager(cacheManager)</b>.build();
 * </pre>
 *
 * @since 1.0.0
 */
public class HazelcastCacheManager implements CacheManager {

    private HazelcastInstance hazelcastInstance;

    /**
     * Creates a new {@code HazelcastCacheManager} that, after creation, must be configured with a
     * {@link HazelcastInstance} via the
     * {@link #setHazelcastInstance(com.hazelcast.core.HazelcastInstance) setHazelcastInstance} method.
     */
    public HazelcastCacheManager() {
    }

    /**
     * Creates a new {@code HazelcastCacheManager} that uses the specified {@code hazelcastInstance} to acquire
     * maps for all caching operations.  Because {@code HazelcastClient} implements the {@code HazelcastInstance}
     * interface, the argument may be a {@code HazelcastClient} instance if desired.
     *
     * @param hazelcastInstance the {@code HazelcastInstance} (or {@code HazeclastClient}) used to acquire maps for all
     *                 caching operations.
     */
    public HazelcastCacheManager(HazelcastInstance hazelcastInstance) {
        Assert.notNull(hazelcastInstance, "HazelcastInstance argument cannot be null.");
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * Uses the specified {@code hazelcastInstance} to acquire maps for all caching operations.  Because
     * {@code HazelcastClient} implements the {@code HazelcastInstance} interface, the argument may be a
     * {@code HazelcastClient} instance if desired.
     *
     * @param hazelcastInstance the {@code HazelcastInstance} (or {@code HazeclastClient}) used to acquire maps for all
     *                          caching operations.
     */
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        Assert.notNull(hazelcastInstance, "HazelcastInstance argument cannot be null.");
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        Assert.hasText(name, "name argument cannot be null or empty.");
        ConcurrentMap<K, V> hazelcastMap = hazelcastInstance.getMap(name);
        return new HazelcastCache<K, V>(hazelcastMap);
    }
}
