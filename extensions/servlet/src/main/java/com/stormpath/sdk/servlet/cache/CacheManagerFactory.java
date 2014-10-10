package com.stormpath.sdk.servlet.cache;

import com.stormpath.sdk.cache.CacheManager;

import java.util.Map;

public interface CacheManagerFactory {

    CacheManager createCacheManager(Map<String,String> config);

}
