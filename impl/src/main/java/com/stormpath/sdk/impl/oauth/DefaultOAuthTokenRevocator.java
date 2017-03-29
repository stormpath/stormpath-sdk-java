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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OAuthRevocationRequest;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;
import com.stormpath.sdk.oauth.TokenTypeHint;

/**
 * @since 1.2.0
 */
public class DefaultOAuthTokenRevocator implements OAuthTokenRevocator {

    private final static String OAUTH_REVOKE_PATH = "/oauth/revoke";

    private final String oauthRevokePath;

    protected final Application application;

    protected final InternalDataStore dataStore;

    public DefaultOAuthTokenRevocator(Application application, InternalDataStore dataStore) {
        this(application, dataStore, OAUTH_REVOKE_PATH);
    }

    public DefaultOAuthTokenRevocator(Application application, InternalDataStore dataStore, String oauthRevokePath) {
        Assert.notNull(application, "application cannot be null.");
        Assert.notNull(dataStore, "dataStore cannot be null.");
        this.application = application;
        this.dataStore = dataStore;
        this.oauthRevokePath = oauthRevokePath;
    }

    @Override
    public void revoke(OAuthRevocationRequest request) {
        Assert.notNull(request, "oAuthRevocationRequest cannot be null.");

        OAuthTokenRevocationAttempt attempt = new OAuthTokenRevocationAttempt(dataStore, request.getToken());

        TokenTypeHint tokenTypeHint = request.getTokenTypeHint();

        if (tokenTypeHint != null) {
            attempt.setTokenTypeHint(tokenTypeHint.getValue());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        dataStore.create(application.getHref() + oauthRevokePath, attempt, OAuthTokenRevoked.class, httpHeaders);
    }

}
