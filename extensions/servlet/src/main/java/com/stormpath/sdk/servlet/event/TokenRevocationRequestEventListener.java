/*
* Copyright 2016 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.servlet.event;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.ds.DefaultDataStore;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.oauth.impl.JwtTokenSigningKeyResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.oltu.oauth2.rs.extractor.BearerHeaderTokenExtractor;
import org.apache.oltu.oauth2.rs.extractor.TokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* @since 1.0.RC8.3
*/
public class TokenRevocationRequestEventListener implements RequestEventListener {

    private static final Logger log = LoggerFactory.getLogger(TokenRevocationRequestEventListener.class);

    private final TokenExtractor tokenExtractor = new BearerHeaderTokenExtractor();

    private final JwtTokenSigningKeyResolver jwtTokenSigningKeyResolver = new JwtTokenSigningKeyResolver();

    private Client client = null;

    @Override
    public void on(SuccessfulAuthenticationRequestEvent e) {
        //No-op
    }

    @Override
    public void on(FailedAuthenticationRequestEvent e) {
        //No-op
    }

    @Override
    public void on(RegisteredAccountRequestEvent e) {
        //No-op
    }

    @Override
    public void on(VerifiedAccountRequestEvent e) {
        //No-op
    }

    @Override
    public void on(LogoutRequestEvent e) {
        String jwt = tokenExtractor.getAccessToken(e.getRequest());
        if (jwt != null) {
            if (this.client == null) {
                this.client = ClientResolver.INSTANCE.getClient(e.getRequest()); //will throw if not found
            }

            Key signingKey = jwtTokenSigningKeyResolver.getSigningKey(e.getRequest(), e.getResponse(), null, SignatureAlgorithm.HS256);
            Claims claims = Jwts.parser().setSigningKey(signingKey.getEncoded()).parseClaimsJws(jwt).getBody();

            //Let's be sure this jwt is actually an access token otherwise we will have an error when trying to retrieve
            //a resource (in order to delete it) that actually is not what we expect
            if (isAccessToken(claims)) {
                gracefullyDeleteRefreshToken((String) claims.get("rti"));
                gracefullyDeleteAccessToken(claims.getId());
            }
            //There should never be a refresh token here. Therefore we will not even try to identify if the received JWT is
            //a refresh token. That would be a bug in the filter chain as a refresh token should never be used to anything other than
            //obtaining a new access token
        }
        if (log.isDebugEnabled()) {
            log.debug("The current access and refresh token for {} has been revoked.", e.getAccount().getEmail());
        }
    }

    private boolean isAccessToken(Claims claims) {
        return claims.containsKey("rti");
    }

    private void gracefullyDeleteAccessToken(String accessTokenId) {
        try {
            String href = "/accessTokens/" + accessTokenId;
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("href", href);
            AccessToken accessToken = ((DefaultDataStore)client.getDataStore()).instantiate(AccessToken.class, map, null, true);
            accessToken.delete();
        } catch (ResourceException e) {
            //Let's prevent an error to avoid the flow to continue (failing to try to delete an access token for example)
            if (log.isDebugEnabled()) {
                log.debug("There was an error trying to delete access token with ID " + accessTokenId + ". Exception was: " + e.getStackTrace());
            }
        }
    }

    private void gracefullyDeleteRefreshToken(String refreshTokenId) {
        try{
            String href =  "/refreshTokens/" + refreshTokenId;
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("href", href);
            RefreshToken refreshToken = ((DefaultDataStore)client.getDataStore()).instantiate(RefreshToken.class, map, null, true);
            refreshToken.delete();
        } catch (ResourceException e) {
            //Let's prevent an error to avoid the flow to continue, this component is basically a listener that tries to delete
            //access tokens on logout, we will only post this error in the log
            if (log.isDebugEnabled()) {
                log.debug("There was an error trying to delete refresh token with ID " + refreshTokenId + ". Exception was: " + e.getStackTrace());
            }
        }
    }
}
