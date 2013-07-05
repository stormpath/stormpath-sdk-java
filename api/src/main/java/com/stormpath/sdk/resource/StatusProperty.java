package com.stormpath.sdk.resource;

/**
 * @since 0.9
 */
public class StatusProperty extends NonStringProperty<Status> {

    public StatusProperty(String name) {
        this(name, false);
    }

    public StatusProperty(String name, boolean required) {
        super(name, Status.class, required);
    }
}
