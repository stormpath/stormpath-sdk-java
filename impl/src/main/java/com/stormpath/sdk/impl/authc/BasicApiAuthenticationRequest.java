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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.error.authc.InvalidApiKeyException;
import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.error.authc.UnsupportedAuthenticationSchemeException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Assert;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

/**
 * @since 1.0.RC
 */
public class BasicApiAuthenticationRequest implements AuthenticationRequest<String, String> {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final HttpRequest httpRequest;

    private final String id;

    private final String secret;

    public BasicApiAuthenticationRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;

        Assert.isTrue(hasHttpRequest());

        String authzHeaderValue;

        authzHeaderValue = httpRequest.getHeader(ApiAuthenticationRequestFactory.AUTHORIZATION_HEADER);

        String[] authTokens = getAuthenticationTokens(authzHeaderValue);

        id = authTokens[0];
        secret = authTokens[1];
    }

    private String[] getAuthenticationTokens(String authzHeaderValue) {

        String encodedAuthenticationTokens = getEncodedAuthenticationToken(authzHeaderValue);

        byte[] encodedBytes = encodedAuthenticationTokens.getBytes(UTF_8);

        if (!Base64.isBase64(encodedBytes)) {
            throw new InvalidApiKeyException(null);
        }

        String decoded = new String(Base64.decodeBase64(encodedBytes), UTF_8);

        String[] tokens = decoded.split(":", 2);

        if (tokens.length != 2) { //no password specified
            throw new InvalidApiKeyException(null);
        }

        return tokens;

    }

    private String getEncodedAuthenticationToken(String authzHeaderValue) {

        if (authzHeaderValue == null) {
            throw new MissingApiKeyException(null);
        }

        String[] schemeAndValue = authzHeaderValue.split(" ", 2);

        if (schemeAndValue.length != 2) {
            throw new MissingApiKeyException(null);
        }

        if (!ApiAuthenticationRequestFactory.BASIC_AUTHENTICATION_SCHEME.equalsIgnoreCase(schemeAndValue[0])) {
            throw new UnsupportedAuthenticationSchemeException(null);
        }

        return schemeAndValue[1];
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public boolean hasHttpRequest() {
        return httpRequest != null;
    }

    @Override
    public String getPrincipals() {
        return id;
    }

    @Override
    public String getCredentials() {
        return secret;
    }

    @Override
    public String getHost() {
        throw new UnsupportedOperationException("getHost() this operation is not supported ApiAuthenticationRequest..");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear() this operation is not supported ApiAuthenticationRequest..");
    }

    @Override
    public AccountStore getAccountStore() {
        throw new UnsupportedOperationException("getAccountStore() this operation is not supported ApiAuthenticationRequest.");
    }
}

