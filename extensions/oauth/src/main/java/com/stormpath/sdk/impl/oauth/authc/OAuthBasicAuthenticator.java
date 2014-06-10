/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.oauth.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.ApiAuthenticationResult;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.impl.authc.BasicApiAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.jwt.signer.JwtSigner;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.impl.oauth.issuer.JwtOauthIssuer;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.authc.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.apache.oltu.oauth2.common.OAuth.*;

/**
 * OAuthBasicAuthenticator is the default implementation of the resource authorization authenticator.
 *
 * @since 1.0.RC
 */
public class OAuthBasicAuthenticator {

    public final static char SPACE_SEPARATOR = ' ';

    private static final EnumSet<GrantType> SUPPORTED_GRANT_TYPES;

    public final static String TIMESTAMP_PARAM_NAME = "timestamp";

    public final static String APPLICATION_HREF_PARAM_NAME = "application_href";

    static {
        SUPPORTED_GRANT_TYPES = EnumSet.of(GrantType.CLIENT_CREDENTIALS);
    }

    private final InternalDataStore dataStore;

    private final JwtSigner jwtSigner;

    public OAuthBasicAuthenticator(InternalDataStore internalDataStore) {
        dataStore = internalDataStore;

        jwtSigner = new DefaultJwtSigner(dataStore.getApiKey().getSecret());
    }

    public AccessTokenResult authenticate(Application application, DefaultBasicOauthAuthenticationRequest request) {
        Assert.notNull(request, "request cannot be null.");

        validateSupportedGrantType(request.getGrantType());

        ApiAuthenticationResult authResult;

        try {
            authResult = new BasicApiAuthenticator(dataStore).authenticate(application, request.getClientId(), request.getClientSecret());
        } catch (ResourceException e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_CLIENT);
        }

        Set<String> grantedScopes;

        long ttl = request.getTtl();

        DefaultTokenResponse.Builder responseBuilder = DefaultTokenResponse.tokenType(TokenType.BEARER).expiresIn(String.valueOf(ttl));

        String scope;

        if (request.hasScopeFactory()) {
            StringBuilder scopeBuilder = new StringBuilder();
            grantedScopes = request.getScopeFactory().createScope(authResult, request.getScopes());
            for (Iterator<String> scopeIterator = grantedScopes.iterator(); scopeIterator.hasNext(); ) {
                scopeBuilder.append(scopeIterator.next());
                if (scopeIterator.hasNext()) {
                    scopeBuilder.append(SPACE_SEPARATOR);
                }
            }
            scope = scopeBuilder.toString();
            responseBuilder.scope(scope);

        } else {
            grantedScopes = Collections.emptySet();
            scope = null;
        }

        String accessToken = createAccessToken(application, authResult, ttl, scope);

        responseBuilder.accessToken(accessToken).applicationHref(application.getHref());

        return new DefaultAccessTokenResult(dataStore, authResult.getApiKey(), grantedScopes, responseBuilder.build());

    }

    private void validateSupportedGrantType(String requestedGrantType) {
        for (GrantType grantType : SUPPORTED_GRANT_TYPES) {
            if (grantType.toString().equalsIgnoreCase(requestedGrantType)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.UNSUPPORTED_GRANT_TYPE);
    }

    /**
     * The result of this method is a that contains the following information.
     * <p/>
     * base64Header.base64Payload.base64Signature
     */
    private String createAccessToken(Application application, ApiAuthenticationResult result, long ttl, String scope) {

        //created is the UTC current time in seconds.
        long createdAt = System.currentTimeMillis() / 1000;

        Map<String, Object> jsonMap = new HashMap<String, Object>();

        jsonMap.put(APPLICATION_HREF_PARAM_NAME, application.getHref());
        jsonMap.put(OAUTH_CLIENT_ID, result.getApiKey().getId());
        jsonMap.put(TIMESTAMP_PARAM_NAME, createdAt);
        jsonMap.put(OAUTH_EXPIRES_IN, ttl);

        if (Strings.hasText(scope)) {
            jsonMap.put(OAUTH_SCOPE, scope);
        }

        OAuthIssuer jwtIssuer = new JwtOauthIssuer(jwtSigner, jsonMap);

        try {
            return jwtIssuer.accessToken();
        } catch (OAuthSystemException e) {
            throw new IllegalStateException("Unexpected exception occurred while creating access token.");
        }

    }
}
