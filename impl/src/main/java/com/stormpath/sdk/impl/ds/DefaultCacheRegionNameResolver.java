package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.8
 */
public class DefaultCacheRegionNameResolver implements CacheRegionNameResolver {

    @Override
    public <T extends Resource> String getCacheRegionName(Class<T> clazz) {
        Assert.notNull(clazz, "Class argument cannot be null.");
        Class iface = DefaultResourceFactory.getInterfaceClass(clazz);
        return iface.getName();
    }
}
