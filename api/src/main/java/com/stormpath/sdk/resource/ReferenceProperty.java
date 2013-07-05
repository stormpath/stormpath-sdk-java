package com.stormpath.sdk.resource;

/**
 * @since 0.9
 */
public class ReferenceProperty<T extends Resource> extends Property<T> {

    private final boolean collection;

    public ReferenceProperty(String name, Class<T> type, boolean required, boolean collection) {
        super(name, type, required);
        this.collection = collection;
    }

    public boolean isCollection() {
        return collection;
    }
}
