package com.stormpath.sdk.impl.mail;

import com.stormpath.sdk.mail.EmailRequest;

/**
 *
 */
public class DefaultEmailRequest implements EmailRequest {

    private String toAddress;
    private String toDisplayName;
    private String token;

    @Override
    public String getToAddress() {
        return toAddress;
    }

    @Override
    public EmailRequest setToAddress(String email) {
        this.toAddress = email;
        return this;
    }

    @Override
    public String getToDisplayName() {
        return toDisplayName;
    }

    @Override
    public EmailRequest setToDisplayName(String displayName) {
        this.toDisplayName = displayName;
        return this;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public EmailRequest setToken(String token) {
        this.token = token;
        return this;
    }
}
