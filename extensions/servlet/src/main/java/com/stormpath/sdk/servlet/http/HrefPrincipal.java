package com.stormpath.sdk.servlet.http;

public class HrefPrincipal extends StringPrincipal {

    public HrefPrincipal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HrefPrincipal && super.equals(obj);
    }
}
