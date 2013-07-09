package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.8
 */
public class CollectionReference<C extends CollectionResource<T>, T extends Resource> extends ResourceReference<C> {

    private final Class<T> instanceType;

    public CollectionReference(String name, Class<C> type, boolean required, Class<T> instanceType) {
        super(name, type, required);
        Assert.notNull(instanceType, "instanceType argument cannot be null.");
        this.instanceType = instanceType;
    }

    public Class<T> getInstanceType() {
        return instanceType;
    }
}
