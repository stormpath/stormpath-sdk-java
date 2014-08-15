package com.stormpath.sdk.servlet.cache;

import com.stormpath.sdk.cache.CacheManager;

import java.util.Properties;

public interface CacheManagerFactory {

    CacheManager createCacheManager(Properties properties);

}
