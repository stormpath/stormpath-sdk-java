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
package com.stormpath.sdk.servlet.cache;

import com.stormpath.sdk.cache.CacheConfigurationBuilder;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.cache.CacheManagerBuilder;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.lang.Strings;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @since 1.0.RC3
 */
public class PropertiesCacheManagerFactory implements CacheManagerFactory {

    public static final String STORMPATH_CACHE_CONFIG_PREFIX = "stormpath.client.cacheManager.caches.";
    public static final String STORMPATH_CACHE_MANAGER       = "stormpath.client.cacheManager";
    public static final String STORMPATH_CACHE_ENABLED       = STORMPATH_CACHE_MANAGER + ".enabled";
    public static final String STORMPATH_CACHE_TTI_SUFFIX    = ".tti";
    public static final String STORMPATH_CACHE_TTL_SUFFIX    = ".ttl";
    public static final String STORMPATH_CACHE_TTI           = STORMPATH_CACHE_MANAGER + ".defaultTti";
    public static final String STORMPATH_CACHE_TTL           = STORMPATH_CACHE_MANAGER + ".defaultTtl";

    @Override
    public CacheManager createCacheManager(Map<String,String> config) {

        config = (config != null ? config : new LinkedHashMap<String, String>());

        Set keys = config.keySet();

        CacheManagerBuilder builder = Caches.newCacheManager();

        Map<String, CacheConfigurationBuilder> regionConfigs = new HashMap<String, CacheConfigurationBuilder>();

        for (final Object key : keys) {

            final String sKey = (String) key;

            if (STORMPATH_CACHE_MANAGER.equals(sKey)) {
                //this defines the cache manager bean itself (this object), so just skip
                //(the Config mechanism already instantiated this instance)

                //noinspection UnnecessaryContinue
                continue;

            } else if (STORMPATH_CACHE_ENABLED.equals(sKey)) {
                String value = config.get(sKey);
                boolean enabled = true;

                if (Strings.hasText(value)) {
                    if ("true".equalsIgnoreCase(value)) {
                        enabled = true;
                    } else if ("false".equalsIgnoreCase(value)) {
                        enabled = false;
                    } else {
                        String msg = STORMPATH_CACHE_ENABLED + " value must equal true or false";
                        throw new IllegalArgumentException(msg);
                    }
                }

                if (!enabled) {
                    //short circuit:
                    return Caches.newDisabledCacheManager();
                }

            } else if (STORMPATH_CACHE_TTI.equals(sKey)) {

                String value = config.get(sKey);
                long tti = parseLong(sKey, value);
                builder.withDefaultTimeToIdle(tti, TimeUnit.MILLISECONDS);

            } else if (STORMPATH_CACHE_TTL.equals(sKey)) {

                String value = config.get(sKey);
                long ttl = parseLong(sKey, value);
                builder.withDefaultTimeToLive(ttl, TimeUnit.MILLISECONDS);

            } else if (sKey.startsWith(STORMPATH_CACHE_CONFIG_PREFIX)) {

                String value = config.get(sKey);
                String suffix = sKey.substring(STORMPATH_CACHE_CONFIG_PREFIX.length());
                String regionName;
                long ttl = -1;
                long tti = -1;

                if (suffix.endsWith(STORMPATH_CACHE_TTI_SUFFIX)) {
                    regionName = suffix.substring(0, suffix.length() - STORMPATH_CACHE_TTI_SUFFIX.length());
                    tti = parseLong(sKey, value);
                } else if (suffix.endsWith(STORMPATH_CACHE_TTL_SUFFIX)) {
                    regionName = suffix.substring(0, suffix.length() - STORMPATH_CACHE_TTL_SUFFIX.length());
                    ttl = parseLong(sKey, value);
                } else {
                    throw new IllegalArgumentException(
                        "Unrecognized configuration property [" + sKey + "]. Ensure any " +
                        "configured region specifies either a TTI or TTL or both via " +
                        "the appropriate suffix (.tti or .ttl respectively).");
                }

                CacheConfigurationBuilder ccb = regionConfigs.get(regionName);
                if (ccb == null) {
                    ccb = Caches.named(regionName);
                    regionConfigs.put(regionName, ccb);
                }

                if (ttl >= 0) {
                    ccb.withTimeToLive(ttl, TimeUnit.MILLISECONDS);
                }
                if (tti >= 0) {
                    ccb.withTimeToIdle(tti, TimeUnit.MILLISECONDS);
                }
            }
            //else not a stormpath.client.cache property - ignore it for CacheManager building purposes
        }

        for (CacheConfigurationBuilder ccb : regionConfigs.values()) {
            builder.withCache(ccb);
        }

        return builder.build();
    }

    protected long parseLong(String key, String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            String msg = "Unable to parse " + key + " value to a long (milliseconds).";
            throw new IllegalArgumentException(msg, e);
        }
    }
}
