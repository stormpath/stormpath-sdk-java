package com.stormpath.sdk.okta;


import java.net.URL;

/**
 * HAL based link representation.
 */
public class Link {

    private URL href;
    private String method;

    public URL getHref() {
        return href;
    }

    public Link setHref(URL href) {
        this.href = href;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Link setMethod(String method) {
        this.method = method;
        return this;
    }
}
