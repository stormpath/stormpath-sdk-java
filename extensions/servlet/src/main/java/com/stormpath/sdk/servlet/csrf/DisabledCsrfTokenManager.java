package com.stormpath.sdk.servlet.csrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class DisabledCsrfTokenManager implements CsrfTokenManager {

    private static final String DUMMY_TOKEN = UUID.randomUUID().toString();

    public static final String DEFAULT_CSRF_TOKEN_NAME = "_csrf";

    private String csrfTokenName;

    public DisabledCsrfTokenManager() {
        this(DEFAULT_CSRF_TOKEN_NAME);
    }

    public DisabledCsrfTokenManager(String csrfTokenName) {
        this.csrfTokenName = csrfTokenName;
    }

    @Override
    public String getTokenName() {
        return this.csrfTokenName;
    }

    @Override
    public String createCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        return DUMMY_TOKEN;
    }

    @Override
    public boolean isValidCsrfToken(HttpServletRequest request, HttpServletResponse response, String csrfToken) {
        return DUMMY_TOKEN.equals(csrfToken);
    }
}
