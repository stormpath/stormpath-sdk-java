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
import com.stormpath.sdk.error.authc.UnsupportedGrantTypeOauthException;
import com.stormpath.sdk.impl.authc.BasicApiAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.oauth.issuer.HmacValueGenerator;
import com.stormpath.sdk.impl.oauth.token.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.BasicOauthAuthenticationResult;
import org.apache.oltu.oauth2.as.issuer.ValueGenerator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

/**
 * OAuthBasicAuthenticator
 *
 * @since 1.0.RC
 */
public class OAuthBasicAuthenticator {

    public final static char SPACE_SEPARATOR = ' ';

    public final static char COLON_SEPARATOR = ':';

    private static final String UTF_8 = "UTF-8";

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private static final EnumSet<GrantType> SUPPORTED_GRANT_TYPES;

    static {
        SUPPORTED_GRANT_TYPES = EnumSet.of(GrantType.CLIENT_CREDENTIALS);
    }

    private final InternalDataStore dataStore;

    public OAuthBasicAuthenticator(InternalDataStore internalDataStore) {
        this.dataStore = internalDataStore;
    }

    public BasicOauthAuthenticationResult authenticate(Application application, DefaultBasicOauthAuthenticationRequest request) {
        Assert.notNull(request, "request cannot be null.");

        validateSupportedGrantType(request.getGrantType());

        ApiAuthenticationResult authResult = new BasicApiAuthenticator(dataStore).authenticate(application, request.getClientId(), request.getClientSecret());

        Set<String> grantedScopes;

        String ttl = String.valueOf(request.getTtl());

        DefaultTokenResponse.Builder responseBuilder = DefaultTokenResponse.tokenType(TokenType.BEARER).expiresIn(ttl);

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

        String accessToken = buildAccessToken(application, authResult, ttl, scope);

        responseBuilder.accessToken(accessToken);

        return new DefaultBasicOauthAuthenticationResult(dataStore, authResult.getApiKey(), grantedScopes, responseBuilder.build());
    }

    private void validateSupportedGrantType(String requestedGrantType) {
        for (GrantType grantType : SUPPORTED_GRANT_TYPES) {
            if (grantType.toString().equalsIgnoreCase(requestedGrantType)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(UnsupportedGrantTypeOauthException.class);
    }

    /**
     * The result of this method is a plain-text access token that contains the following information:
     *
     * urlEncoded(applicationHref):apiKeyId:createdTimestamp:ttl:scope(if exist).
     *
     */
    private String buildAccessToken(Application application, ApiAuthenticationResult result, String ttl, String scope) {

        long createdAt = Calendar.getInstance(UTC).getTime().getTime();

        StringBuilder builder = new StringBuilder(encodeUrl(application.getHref()))
                .append(COLON_SEPARATOR).append(result.getApiKey().getId())
                .append(COLON_SEPARATOR).append(createdAt)
                .append(COLON_SEPARATOR).append(ttl);

        if (scope != null && !scope.isEmpty()) {
            builder.append(COLON_SEPARATOR).append(scope);
        }

        ValueGenerator generator = new HmacValueGenerator(dataStore.getApiKey().getSecret());

        try {
            return generator.generateValue(builder.toString());
        } catch (OAuthSystemException e) {
            throw new IllegalStateException("Unexpected error while generating access token.", e);
        }
    }

    private String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, UTF_8);
        } catch (UnsupportedEncodingException e) {
            //This should never happen
            return url;
        }
    }
}
