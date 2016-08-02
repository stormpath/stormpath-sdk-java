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

import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.error.authc.OAuthAuthenticationException;
import com.stormpath.sdk.impl.authc.BasicApiAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.jwt.signer.JwtSigner;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.impl.oauth.issuer.JwtOAuthIssuer;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
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
 * Authenticates http requests that represents an attempt to obtain a new Access Token from the application's token
 * endpoint, for example, {@code /oauth/token}.
 *
 * <p>This implementation currently only supports OAuth 2 Client Credentials Grant Type requests.</p>
 *
 * @since 1.0.RC
 */
public class AccessTokenRequestAuthenticator {

    public final static char SPACE_SEPARATOR = ' ';

    private static final EnumSet<GrantType> SUPPORTED_GRANT_TYPES = EnumSet.of(GrantType.CLIENT_CREDENTIALS);

    // This implementation generates Access Tokens that are JWTs.  The JWT specification lists the following
    // default field names that should be used for JWTs making identity assertions:

    // http://tools.ietf.org/html/draft-ietf-oauth-json-web-token-21#section-4.1.1
    public static final String ACCESS_TOKEN_ISSUER_FIELD_NAME = "iss";

    // http://tools.ietf.org/html/draft-ietf-oauth-json-web-token-21#section-4.1.2
    public static final String ACCESS_TOKEN_SUBJECT_FIELD_NAME = "sub";

    // http://tools.ietf.org/html/draft-ietf-oauth-json-web-token-21#section-4.1.6
    public static final String ACCESS_TOKEN_CREATION_TIMESTAMP_FIELD_NAME = "iat";

    // http://tools.ietf.org/html/draft-ietf-oauth-json-web-token-21#section-4.1.4
    public static final String ACCESS_TOKEN_EXPIRATION_TIMESTAMP_FIELD_NAME = "exp";

    private final InternalDataStore dataStore;

    private final JwtSigner jwtSigner;

    public AccessTokenRequestAuthenticator(InternalDataStore internalDataStore) {
        dataStore = internalDataStore;

        jwtSigner = new DefaultJwtSigner(dataStore.getClientCredentials().getId(), dataStore.getClientCredentials().getSecret());
    }

    public AccessTokenResult authenticate(Application application, AccessTokenAuthenticationRequest request) {
        Assert.notNull(request, "request cannot be null.");

        validateSupportedGrantType(request.getGrantType());

        ApiAuthenticationResult authResult;

        try {
            authResult = new BasicApiAuthenticator(dataStore)
                .authenticate(application, request.getClientId(), request.getClientSecret());
        } catch (ResourceException e) {
            throw ApiAuthenticationExceptionFactory
                .newOAuthException(OAuthAuthenticationException.class, OAuthAuthenticationException.INVALID_CLIENT);
        }

        Set<String> grantedScopes;

        long ttl = request.getTtl();

        DefaultTokenResponse.Builder responseBuilder =
            DefaultTokenResponse.tokenType(TokenType.BEARER).expiresIn(String.valueOf(ttl));

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
        throw ApiAuthenticationExceptionFactory
            .newOAuthException(OAuthAuthenticationException.class, OAuthAuthenticationException.UNSUPPORTED_GRANT_TYPE);
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

        jsonMap.put(ACCESS_TOKEN_ISSUER_FIELD_NAME, application.getHref());
        jsonMap.put(ACCESS_TOKEN_SUBJECT_FIELD_NAME, result.getApiKey().getId());
        jsonMap.put(ACCESS_TOKEN_CREATION_TIMESTAMP_FIELD_NAME, createdAt);

        //ttl is in seconds.  The expiration timestamp needs to be equal to: createdAt (in seconds) + ttl :
        long expirationTimeAsSecondsSinceEpoch = createdAt + ttl;

        jsonMap.put(ACCESS_TOKEN_EXPIRATION_TIMESTAMP_FIELD_NAME, expirationTimeAsSecondsSinceEpoch);

        if (Strings.hasText(scope)) {
            jsonMap.put(OAUTH_SCOPE, scope);
        }

        OAuthIssuer jwtIssuer = new JwtOAuthIssuer(jwtSigner, jsonMap);

        try {
            return jwtIssuer.accessToken();
        } catch (OAuthSystemException e) {
            throw new IllegalStateException("Unexpected exception occurred while creating access token.");
        }

    }
}
