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
package com.stormpath.sdk.impl.idsite;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.jwt.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.jwt.signer.JwtSigner;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.stormpath.sdk.impl.jwt.JwtConstants.*;

/**
 * @since 1.0.RC
 */
public class DefaultIdSiteUrlBuilder implements IdSiteUrlBuilder {

    public static final String SSO_ENDPOINT = "http://api.stormpath.com/sso";
    public static final String SSO_LOGOUT_ENDPOINT = SSO_ENDPOINT + "/logout";

    private final InternalDataStore internalDataStore;

    private final String applicationHref;

    private String callbackUri;

    private String state;

    private String path;

    private boolean logout = false;

    public DefaultIdSiteUrlBuilder(InternalDataStore internalDataStore, String applicationHref) {
        Assert.notNull(internalDataStore, "internalDataStore cannot be null.");
        Assert.hasText(applicationHref, "applicationHref cannot be null or empty");

        this.internalDataStore = internalDataStore;
        this.applicationHref = applicationHref;
    }

    @Override
    public IdSiteUrlBuilder setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
        return this;
    }

    @Override
    public IdSiteUrlBuilder setState(String state) {
        this.state = state;
        return this;
    }

    @Override
    public IdSiteUrlBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public IdSiteUrlBuilder setLogout(boolean logout) {
        this.logout = logout;
        return this;
    }

    @Override
    public String build() {
        Assert.state(Strings.hasText(this.callbackUri), "callbackUri cannot be null or empty.");

        String nonce = UUID.randomUUID().toString();

        long now = System.currentTimeMillis() / 1000; //Seconds

        final ApiKey apiKey = this.internalDataStore.getApiKey();

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put(ISSUED_AT_PARAM_NAME, now);
        body.put(NONCE_PARAM_NAME, nonce);
        body.put(ISSUER_PARAM_NAME, apiKey.getId());
        body.put(SUBJECT_PARAM_NAME, applicationHref);
        if (Strings.hasText(this.path)) {
            body.put(PATH_PARAM_NAME, this.path);
        }
        body.put(REDIRECT_URI_PARAM_NAME, this.callbackUri);
        if (Strings.hasText(this.state)) {
            body.put(STATE_PARAM_NAME, this.state);
        }

        ObjectMapper mapper = new ObjectMapper();

        try {

            String message = mapper.writeValueAsString(body);
            JwtSigner jwtSigner = new DefaultJwtSigner(apiKey.getSecret());
            String jwt = jwtSigner.sign(message);

            QueryString queryString = new QueryString();
            queryString.put(JWR_REQUEST_PARAM_NAME, jwt);

            String endpoint;

            if (logout) {
                endpoint = SSO_LOGOUT_ENDPOINT;
            } else {
                endpoint = SSO_ENDPOINT;
            }

            StringBuilder urlBuilder = new StringBuilder(endpoint).append('?').append(queryString.toString());

            return urlBuilder.toString();

        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong when constructing the SsoRedirectUri: " + e);
        }

    }
}
