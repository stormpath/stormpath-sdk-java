package com.stormpath.sdk.resource;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public abstract class Property<T> {

    protected final String name;
    protected final Class<T> type;
    protected final boolean required;

    protected Property(String name, Class<T> type, boolean required) {
        Assert.notNull(name, "name is required.");
        Assert.notNull(type, "type is required.");
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isRequired() {
        return this.required;
    }
}
