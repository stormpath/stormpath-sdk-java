package com.stormpath.sdk.servlet.application.okta;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.ds.DataStore;
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

    private DataStore dataStore;

    public OktaJwtSigningKeyResolver() {

    }

    public OktaJwtSigningKeyResolver(DataStore dataStore)
    {
        this.dataStore = dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result, SignatureAlgorithm alg) {
        throw new UnsupportedOperationException("getSigningKey() is not supported");
    }

    @Override
    public Key getSigningKey(HttpServletRequest request, HttpServletResponse response, JwsHeader jwsHeader, Claims claims) {
        return dataStore.instantiate(OktaSigningKeyResolver.class).resolveSigningKey(jwsHeader, claims);
    }
}
