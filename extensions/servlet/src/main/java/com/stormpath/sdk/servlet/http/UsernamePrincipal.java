package com.stormpath.sdk.servlet.http;

public class UsernamePrincipal extends StringPrincipal {

    public UsernamePrincipal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UsernamePrincipal && super.equals(obj);
    }
}
