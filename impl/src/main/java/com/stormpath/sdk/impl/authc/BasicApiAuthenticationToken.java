package com.stormpath.sdk.impl.authc;

/**
 * @since 1.0.RC
 */
public class BasicApiAuthenticationToken {

    private final String id;

    private final String secret;

    public BasicApiAuthenticationToken(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }
}
