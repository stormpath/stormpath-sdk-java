package com.stormpath.sdk.servlet.http;

public class GivenNamePrincipal extends StringPrincipal {

    public GivenNamePrincipal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GivenNamePrincipal && super.equals(obj);
    }
}
