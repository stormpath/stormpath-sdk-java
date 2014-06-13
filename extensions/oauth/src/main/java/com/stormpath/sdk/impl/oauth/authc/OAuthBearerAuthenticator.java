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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.error.authc.AccessTokenOauthException;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.ds.JacksonMapMarshaller;
import com.stormpath.sdk.impl.ds.MapMarshaller;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.jwt.signer.JwtSigner;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static com.stormpath.sdk.error.authc.AccessTokenOauthException.*;
import static org.apache.oltu.oauth2.common.OAuth.*;

/**
 * @since 1.0.RC
 */
public class OAuthBearerAuthenticator {

    private final static Charset UTF_8 = Charset.forName("UTF-8");

    public final static String JWT_BEARER_TOKEN_SEPARATOR = ".";

    public final static String SCOPE_SEPARATOR_CHAR = " ";

    private final InternalDataStore dataStore;

    private final JwtSigner jwtSigner;

    private final MapMarshaller mapMarshaller;

    public OAuthBearerAuthenticator(InternalDataStore dataStore) {
        this.dataStore = dataStore;
        this.jwtSigner = new DefaultJwtSigner(dataStore.getApiKey().getSecret());
        this.mapMarshaller = new JacksonMapMarshaller();
    }

    public OauthAuthenticationResult authenticate(Application application, DefaultBearerOauthAuthenticationRequest request) {

        String bearerValue;

        try {
            bearerValue = request.getAccessToken();
        } catch (OAuthSystemException e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, INVALID_ACCESS_TOKEN);
        }

        StringTokenizer tokenizer = new StringTokenizer(bearerValue, JWT_BEARER_TOKEN_SEPARATOR);

        if (tokenizer.countTokens() != 3) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, INVALID_ACCESS_TOKEN);
        }

        String base64JwtHeader = tokenizer.nextToken();

        String base64JsonPayload = tokenizer.nextToken();

        String jwtSignature = tokenizer.nextToken();

        String calculatedSignature = jwtSigner.calculateSignature(base64JwtHeader, base64JsonPayload);

        if (!jwtSignature.equals(calculatedSignature)) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, INVALID_ACCESS_TOKEN);
        }

        byte[] jsonBytes = Base64.decodeBase64(base64JsonPayload);

        Map jsonMap = mapMarshaller.unmarshal(new String(jsonBytes, UTF_8));

        Number createdTimestamp = getRequiredValue(jsonMap, OAuthBasicAuthenticator.TIMESTAMP_PARAM_NAME);

        Number expiresIn = getRequiredValue(jsonMap, OAUTH_EXPIRES_IN);

        validateTokenNotExpired(createdTimestamp.longValue(), expiresIn.longValue());

        String apiKeyId = getRequiredValue(jsonMap, OAUTH_CLIENT_ID);

        //Retrieve the ApiKey that owns this
        ApiKey apiKey = getTokenApiKey(application, apiKeyId);

        String grantedScopes = getOptionalValue(jsonMap, OAUTH_SCOPE);

        Set<String> scope;
        if (grantedScopes != null && !grantedScopes.isEmpty()) {
            scope = new HashSet<String>();
            for (StringTokenizer scopeTokenizer = new StringTokenizer(grantedScopes, SCOPE_SEPARATOR_CHAR); scopeTokenizer.hasMoreElements(); ) {
                String scopeValue = scopeTokenizer.nextToken();
                scope.add(scopeValue);
            }
        } else {
            scope = Collections.emptySet();
        }

        return new DefaultOauthAuthenticationResult(dataStore, apiKey, scope);
    }

    private void validateTokenNotExpired(long created, long timeToLive) {

        long now = System.currentTimeMillis();

        if (now > ((created + timeToLive) * 1000)) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, EXPIRED_ACCESS_TOKEN);
        }
    }

    /**
     * Retrieves the {@link ApiKey} instance pointed by this {@code apiKeyId} and accessible from the {@code application}
     * <p/>
     * The ApiKey is retrieved from the {@link Application} passed as argument.
     * <p/>
     * This method asserts that the ApiKey retrieved status is {@link ApiKeyStatus#ENABLED} and also that the status of
     * the account owner is {@link AccountStatus#ENABLED}
     *
     * @param application - The application that is making the assertion.
     * @param apiKeyId    - The id of the {@link ApiKey} embedded in the access token.
     */
    private ApiKey getTokenApiKey(Application application, String apiKeyId) {

        ApiKey apiKey = application.getApiKey(apiKeyId, new DefaultApiKeyOptions().withAccount());

        if (apiKey == null || apiKey.getStatus() == ApiKeyStatus.DISABLED) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, INVALID_CLIENT);
        }

        Account account = apiKey.getAccount();

        if (account.getStatus() != AccountStatus.ENABLED) {
            throw ApiAuthenticationExceptionFactory.newOauthException(AccessTokenOauthException.class, INVALID_CLIENT);
        }

        return apiKey;
    }

    private <T> T getRequiredValue(Map jsonMap, String parameterName) {

        Object object = jsonMap.get(parameterName);

        Assert.notNull(object, "required jwt parameter is missing or null.");

        return (T) object;
    }

    private <T> T getOptionalValue(Map jsonMap, String parameterName) {

        Object object = jsonMap.get(parameterName);

        if (object == null) {
            return null;
        }

        return (T) object;
    }
}
