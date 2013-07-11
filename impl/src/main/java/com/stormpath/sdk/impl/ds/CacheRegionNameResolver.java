package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.8
 */
public interface CacheRegionNameResolver {

    <T extends Resource> String getCacheRegionName(Class<T> clazz);
}
