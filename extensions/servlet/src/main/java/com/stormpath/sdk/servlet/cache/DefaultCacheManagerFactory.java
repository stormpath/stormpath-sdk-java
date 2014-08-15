package com.stormpath.sdk.servlet.cache;

import com.stormpath.sdk.cache.CacheConfigurationBuilder;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.cache.CacheManagerBuilder;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.lang.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DefaultCacheManagerFactory implements CacheManagerFactory {

    public static final String STORMPATH_CACHE_CONFIG_PREFIX = "stormpath.cache.";
    public static final String STORMPATH_CACHE_ENABLED       = STORMPATH_CACHE_CONFIG_PREFIX + "enabled";
    public static final String STORMPATH_CACHE_TTI_SUFFIX    = ".tti";
    public static final String STORMPATH_CACHE_TTL_SUFFIX    = ".ttl";
    public static final String STORMPATH_CACHE_TTI           = STORMPATH_CACHE_CONFIG_PREFIX + "tti";
    public static final String STORMPATH_CACHE_TTL           = STORMPATH_CACHE_CONFIG_PREFIX + "ttl";

    private final boolean createDefault;

    public DefaultCacheManagerFactory() {
        this(false);
    }

    public DefaultCacheManagerFactory(boolean createDefault) {
        this.createDefault = createDefault;
    }

    @Override
    public CacheManager createCacheManager(Properties props) {

        props = (props != null ? props : new Properties());

        Set keys = props.keySet();

        CacheManagerBuilder builder = Caches.newCacheManager();
        boolean configured = false;

        Map<String, CacheConfigurationBuilder> regionConfigs = new HashMap<String, CacheConfigurationBuilder>();

        for (final Object key : keys) {
            final String sKey = (String) key;

            if (STORMPATH_CACHE_ENABLED.equals(sKey)) {
                String value = props.getProperty(sKey);
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

                configured = true;

                if (!enabled) {
                    //short circuit:
                    return Caches.newDisabledCacheManager();
                }

            } else if (STORMPATH_CACHE_TTI.equals(sKey)) {

                String value = props.getProperty(sKey);
                long tti = parseLong(sKey, value);
                builder.withDefaultTimeToIdle(tti, TimeUnit.MILLISECONDS);
                configured = true;

            } else if (STORMPATH_CACHE_TTL.equals(sKey)) {

                String value = props.getProperty(sKey);
                long ttl = parseLong(sKey, value);
                builder.withDefaultTimeToLive(ttl, TimeUnit.MILLISECONDS);
                configured = true;

            } else if (sKey.startsWith(STORMPATH_CACHE_CONFIG_PREFIX)) {

                String value = props.getProperty(sKey);
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

                configured = true;

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
            //else not a stormpath.cache property - ignore it for CacheManager building purposes
        }

        if (!configured && !createDefault) {
            return null;
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
