package com.stormpath.sdk.impl.resource;

/**
 * @since 0.8
 */
public class IntegerProperty extends NonStringProperty<Integer> {

    public IntegerProperty(String name, boolean required) {
        super(name, Integer.class, required);
    }
}
