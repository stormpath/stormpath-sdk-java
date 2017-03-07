package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.support.RequestAuthenticationException;
import com.stormpath.sdk.lang.Assert;

public class SswsAuthenticator implements RequestAuthenticator {

    public static final String AUTHENTICATION_SCHEME = "SSWS";

    private final ApiKeyCredentials apiKeyCredentials;

    public SswsAuthenticator(ApiKeyCredentials apiKeyCredentials) {
        Assert.notNull(apiKeyCredentials, "apiKeyCredentials must be not be null.");
        this.apiKeyCredentials = apiKeyCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + apiKeyCredentials.getSecret());
    }
}
