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
import com.stormpath.sdk.error.authc.DisabledAccountException;
import com.stormpath.sdk.error.authc.InvalidApiKeyException;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.oauth.issuer.HmacValueGenerator;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationResult;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * @since 1.0.RC
 */
public class OAuthBearerAuthenticator {

    private final static Charset UTF_8 = Charset.forName("UTF-8");

    public final static String TOKEN_SEPARATOR_CHAR = ":";

    public final static String SCOPE_SEPARATOR_CHAR = " ";

    private final static byte SIGNED_TOKEN_SEPARATOR = 0x3A;

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final InternalDataStore dataStore;

    public OAuthBearerAuthenticator(InternalDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public OauthAuthenticationResult authenticate(Application application, DefaultBearerOauthAuthenticationRequest request) {

        String accessToken;

        try {
            accessToken = request.getAccessToken();
        } catch (OAuthSystemException e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(InvalidApiKeyException.class, "");
        }

        String payloadAsString = extractPayload(accessToken, dataStore.getApiKey().getSecret());

        // The result of this method is a plain-text access token that contains the following information:
        // urlEncoded(applicationHref):apiKeyId:createdTimestamp:ttl:scope(if exist).
        StringTokenizer tokenizer = new StringTokenizer(payloadAsString, TOKEN_SEPARATOR_CHAR);

        //Figure it out if we want to make an assertion that this tenant owns this application. It's url encoded.
        String applicationHref = tokenizer.nextToken();

        String apiKeyId = tokenizer.nextToken();

        validateTokenNotExpired(tokenizer.nextToken(), tokenizer.nextToken());

        //Retrieve the ApiKey that owns this
        ApiKey apiKey = getTokenApiKey(application, apiKeyId);

        Set<String> scope;
        if (tokenizer.hasMoreElements()) {
            StringTokenizer scopeTokenizer = new StringTokenizer(tokenizer.nextToken(), SCOPE_SEPARATOR_CHAR);
            scope = new HashSet<String>();

            for (String scopeValue = scopeTokenizer.nextToken(); scopeTokenizer.hasMoreElements(); ) {
                scope.add(scopeValue);
            }
        } else {
            scope = Collections.emptySet();
        }

        return new DefaultOauthAuthenticationResult(dataStore, apiKey, scope);
    }

    private void validateTokenNotExpired(String created, String timeToLive) {
        long createdTimestamp = Long.valueOf(created);
        long ttl = Long.valueOf(timeToLive);

        long now = Calendar.getInstance(UTC).getTime().getTime();

        if ((createdTimestamp + ttl) > now) {
            throw ApiAuthenticationExceptionFactory.newOauthException(InvalidApiKeyException.class, "expired");
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

        if (apiKey.getStatus() == ApiKeyStatus.DISABLED) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(DisabledAccountException.class);
        }

        Account account = apiKey.getAccount();

        if (apiKey.getAccount().getStatus() != AccountStatus.ENABLED) {
            throw new DisabledAccountException(null, account.getStatus());
        }

        return apiKey;
    }


    private String extractPayload(String accessToken, String tenantSecret) {
        byte[] decodedToken = Base64.decode(accessToken);

        //split accessToken in two parts.
        //decodedToken = signedPayload[] + ":" + payload[]
        int separatorIndex = -1;

        for (int i = decodedToken.length - 1; i >= 0; i--) {
            if (decodedToken[i] == SIGNED_TOKEN_SEPARATOR) {
                separatorIndex = i;
                break;
            }
        }

        Assert.state(separatorIndex > 0, "This base64 string doesn't follow the accessToken rules 'signedPayload:payload'");

        byte[] payload = new byte[separatorIndex];
        System.arraycopy(decodedToken, 0, payload, 0, payload.length);

        //Create the signedPayload array skipping the separator character.
        byte[] signedPayload = new byte[decodedToken.length - (separatorIndex + 1)];
        System.arraycopy(decodedToken, separatorIndex + 1, signedPayload, 0, signedPayload.length);

        String payloadAsString = new String(payload, UTF_8);

        HmacValueGenerator hmacValueGenerator = new HmacValueGenerator(tenantSecret);

        byte[] signedInput = hmacValueGenerator.computeHmac(payload);

        if (signedInput.length != signedPayload.length) {
            throw ApiAuthenticationExceptionFactory.newOauthException(InvalidApiKeyException.class, "errr");
        }

        for (int i = 0; i < signedInput.length; i++) {
            if (signedInput[i] != signedPayload[i]) {
                throw ApiAuthenticationExceptionFactory.newOauthException(InvalidApiKeyException.class, "errr");
            }
        }
        return payloadAsString;
    }
}
