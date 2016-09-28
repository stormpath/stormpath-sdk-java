package com.stormpath.sdk.servlet.filter.account;

/**
 * @since 1.1.0
 */
public class InsecureCookieException extends RuntimeException {
    private static final String I8N_MESSAGE_KEY = "stormpath.cookie.insecure.error";

    public InsecureCookieException(String s) {
        super(s);
    }

    @Override
    public String getLocalizedMessage() {
        return I8N_MESSAGE_KEY;
    }
}
