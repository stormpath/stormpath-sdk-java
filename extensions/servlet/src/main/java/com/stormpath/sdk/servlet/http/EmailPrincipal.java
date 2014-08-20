package com.stormpath.sdk.servlet.http;

public class EmailPrincipal extends StringPrincipal {

    public EmailPrincipal(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EmailPrincipal && super.equals(obj);
    }
}
