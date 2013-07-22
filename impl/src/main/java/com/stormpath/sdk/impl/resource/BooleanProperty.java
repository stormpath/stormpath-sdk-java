package com.stormpath.sdk.impl.resource;

/**
 * @since 0.9
 */
public class BooleanProperty extends Property<Boolean> {

    public BooleanProperty(String name, boolean required) {
        super(name, Boolean.class, required);
    }
}
