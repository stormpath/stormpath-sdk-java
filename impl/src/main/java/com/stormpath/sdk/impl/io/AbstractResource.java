package com.stormpath.sdk.impl.io;

import com.stormpath.sdk.lang.Assert;

public abstract class AbstractResource implements Resource {

    private final String location;

    public AbstractResource(String location) {
        Assert.hasText(location, "Location argument cannot be null or empty.");
        this.location = canonicalize(location);
    }

    protected String canonicalize(String input) {
        if (hasResourcePrefix(input)) {
            input = stripPrefix(input);
        }
        return input;
    }

    /**
     * Returns {@code true} if the resource path is not null and starts with one of the recognized
     * resource prefixes ({@code classpath:}, {@code url:}, or {@code file:), {@code false} otherwise.
     *
     * @param resourcePath the resource path to check
     * @return {@code true} if the resource path is not null and starts with one of the recognized
     *         resource prefixes, {@code false} otherwise.
     */
    protected boolean hasResourcePrefix(String resourcePath) {
        return resourcePath != null && resourcePath.startsWith(getScheme() + ":");
    }

    private static String stripPrefix(String resourcePath) {
        return resourcePath.substring(resourcePath.indexOf(":") + 1);
    }

    public String getLocation() {
        return location;
    }

    protected abstract String getScheme();

    @Override
    public String toString() {
        return getScheme() + location;
    }
}
