package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.oauth.http.OAuthHttpServletRequest;
import com.stormpath.sdk.oauth.authc.BearerLocation;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC
 */
public class DefaultBearerOauthAuthenticationRequest implements AuthenticationRequest {

    private final HttpServletRequest httpServletRequest;

    private final BearerLocation[] bearerLocations;

    public DefaultBearerOauthAuthenticationRequest(HttpServletRequest httpServletRequest, BearerLocation[] bearerLocations) {
        this.httpServletRequest = httpServletRequest;
        this.bearerLocations = bearerLocations;
    }

    public DefaultBearerOauthAuthenticationRequest(HttpRequest httpRequest, BearerLocation[] bearerLocations) {
        this.httpServletRequest = new OAuthHttpServletRequest(httpRequest);
        this.bearerLocations = bearerLocations;
    }

    @Override
    public Object getPrincipals() {
        throw new UnsupportedOperationException("getPrincipals() this operation is not supported ApiAuthenticationRequest.");
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("getCredentials()this operation is not supported ApiAuthenticationRequest.");
    }

    @Override
    public String getHost() {
        throw new UnsupportedOperationException("getHost() this operation is not supported ApiAuthenticationRequest.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() this operation is not supported ApiAuthenticationRequest.");
    }

    @Override
    public AccountStore getAccountStore() {
        throw new UnsupportedOperationException("getAccountStore()this operation is not supported ApiAuthenticationRequest.");
    }

}
