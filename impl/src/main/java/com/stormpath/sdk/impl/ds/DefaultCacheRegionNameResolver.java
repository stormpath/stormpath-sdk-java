package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.8
 */
public class DefaultCacheRegionNameResolver implements CacheRegionNameResolver {

    @Override
    public <T extends Resource> String getCacheRegionName(T resource) {
        Assert.notNull(resource, "Resource argument cannot be null.");
        Class clazz = resource.getClass();
        Class iface = DefaultResourceFactory.getInterfaceClass(clazz);
        return iface.getName();
    }
}
