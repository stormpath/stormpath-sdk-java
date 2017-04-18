package com.stormpath.sdk.servlet.application.okta;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.impl.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;

/**
 *
 */
public class OktaJwtSigningKeyResolver implements JwtSigningKeyResolver {

    private final OktaSigningKeyResolver oktaSigningKeyResolver;

    public OktaJwtSigningKeyResolver(OktaSigningKeyResolver oktaSigningKeyResolver)
    {
        this.oktaSigningKeyResolver = oktaSigningKeyResolver;
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result, SignatureAlgorithm alg) {
        throw new UnsupportedOperationException("getSigningKey() is not supported");
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, JwsHeader jwsHeader, Claims claims) {
        return oktaSigningKeyResolver.resolveSigningKey(jwsHeader, claims);
    }
}
