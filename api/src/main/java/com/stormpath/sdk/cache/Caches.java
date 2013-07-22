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
package com.stormpath.sdk.cache;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.Resource;

/**
 * Static utility/helper <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s for
 * building {@link CacheManager}s and their associated cache regions, suitable for <b>SINGLE-JVM APPLICATIONS</b>.
 * <p/>
 * If your application is deployed on multiple JVMs (e.g. a distributed/clustered web app), you might not want to
 * use the builders here and instead implement the {@link CacheManager} API directly to use your distributed/clustered
 * cache technology of choice.
 * <p/>
 * See the {@link CacheManagerBuilder} JavaDoc for more information the effects of caching in
 * single-jvm vs distributed-jvm applications.
 * <h3>Usage Example</h3>
 * <pre>
 * import static com.stormpath.sdk.cache.Caches.*;
 *
 * ...
 *
 * Caches.{@link #newCacheManager() newCacheManager()}
 *     .withDefaultTimeToLive(1, TimeUnit.DAYS) //general default
 *     .withDefaultTimeToIdle(2, TimeUnit.HOURS) //general default
 *     .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Account.class) //Account-specific cache settings
 *         .withTimeToLive(1, TimeUnit.HOURS)
 *         .withTimeToIdle(30, TimeUnit.MINUTES))
 *     .withCache({@link com.stormpath.sdk.cache.Caches#forResource(Class) forResource}(Group.class) //Group-specific cache settings
 *         .withTimeToLive(2, TimeUnit.HOURS))
 *     .build() //build the CacheManager
 * </pre>
 * <em>The above TTL and TTI times are just examples showing API usage - the times themselves are not
 * recommendations.  Choose TTL and TTI times based on your application requirements.</em>
 *
 * @since 0.8
 */
public class Caches {

    /**
     * Instantiates a new {@code CacheManagerBuilder} suitable for <b>SINGLE-JVM APPLICATIONS</b>.  If your application
     * is deployed on multiple JVMs (e.g. for a distributed/clustered web app), you might not want to use this method
     * and instead implement the {@link CacheManager} API directly to use your distributed/clustered cache technology
     * of choice.
     * <p/>
     * See the {@link CacheManagerBuilder} JavaDoc for more information the effects of caching in
     * single-jvm vs distributed-jvm applications.
     *
     * @return a new {@code CacheManagerBuilder} suitable for <b>SINGLE-JVM APPLICATIONS</b>.
     */
    public static CacheManagerBuilder newCacheManager() {
        return (CacheManagerBuilder) Classes.newInstance("com.stormpath.sdk.impl.cache.DefaultCacheManagerBuilder");
    }

    /**
     * Returns a new {@link CacheConfigurationBuilder} to configure a cache region that will store data for instances
     * of type {@code clazz}.
     * <p/>
     * This is a convenience method equivalent to {@link #named(String) named(clazz.getName())}, but it could help
     * with readability, for example:
     * <pre>
     * import static com.stormpath.sdk.cache.Caches.*
     * ...
     * newCacheManager()
     *     .withCache(forResource(Account.class).withTimeToIdle(10, TimeUnit.MINUTES))
     *     .build();
     * </pre>
     *
     * @param clazz the resource class that will have a backing cache region for storing data of that type
     * @param <T>   Resource sub-interface
     * @return a new {@link CacheConfigurationBuilder} to configure a cache region that will store data for instances
     *         of type {@code clazz}.
     */
    public static <T extends Resource> CacheConfigurationBuilder forResource(Class<T> clazz) {
        return named(clazz.getName());
    }

    /**
     * Returns a new {@link CacheConfigurationBuilder} used to configure a cache region with the specified name.  For
     * example:
     * <pre>
     * import static com.stormpath.sdk.cache.Caches.*
     * ...
     * newCacheManager()
     *     .withCache(named("myCacheRegion").withTimeToIdle(10, TimeUnit.MINUTES))
     *     .build();
     * </pre>
     *
     * @param name the name of the cache region for which the configuration will apply
     * @return a new {@link CacheConfigurationBuilder} used to configure a cache region with the specified name.
     */
    public static CacheConfigurationBuilder named(String name) {
        return (CacheConfigurationBuilder) Classes.newInstance("com.stormpath.sdk.impl.cache.DefaultCacheConfigurationBuilder", name);
    }

}
