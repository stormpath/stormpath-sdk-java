package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.resource.Resource;

public class MapProperty<T extends Resource> extends Property<T> {

    public MapProperty(String name, Class<T> type) {
        super(name, type);
    }

}
