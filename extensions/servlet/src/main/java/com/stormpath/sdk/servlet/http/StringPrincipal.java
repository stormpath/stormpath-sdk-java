package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.lang.Assert;

import java.io.Serializable;
import java.security.Principal;

public class StringPrincipal implements Principal, Serializable {

    private final String value;

    public StringPrincipal(String value) {
        Assert.hasText(value, "String argument cannot be null or empty.");
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof StringPrincipal && value.equals(((StringPrincipal) obj).value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getName() {
        return value;
    }
}
