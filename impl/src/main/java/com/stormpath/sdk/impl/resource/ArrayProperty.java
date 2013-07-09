package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.8
 */
public class ArrayProperty<T extends Resource> extends Property<T> {

    public ArrayProperty(String name, Class<T> type, boolean required) {
        super(name, type, required);
    }
}
