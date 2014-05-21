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
package com.stormpath.sdk.impl.sso;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.ouath.signer.DefaultJwtSigner;
import com.stormpath.sdk.impl.ouath.signer.JwtSigner;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.sso.SsoRedirectUrlBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @since 1.0.RC
 */
public class DefaultSsoRedirectUrlBuilder implements SsoRedirectUrlBuilder {

    public static final String SSO_ENDPOINT = "http://api.stormpath.com/sso";

    private final InternalDataStore internalDataStore;

    private final String applicationHref;

    private String callbackUri;

    private String state;

    private String path;

    public DefaultSsoRedirectUrlBuilder(InternalDataStore internalDataStore, String applicationHref) {
        Assert.notNull(internalDataStore, "internalDataStore cannot be null.");
        Assert.hasText(applicationHref, "applicationHref cannot be null or empty");

        this.internalDataStore = internalDataStore;
        this.applicationHref = applicationHref;
    }

    @Override
    public SsoRedirectUrlBuilder setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
        return this;
    }

    @Override
    public SsoRedirectUrlBuilder setState(String state) {
        this.state = state;
        return this;
    }

    @Override
    public SsoRedirectUrlBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String build() {
        Assert.state(Strings.hasText(this.callbackUri), "callbackUri cannot be null or empty.");

        String nonce = UUID.randomUUID().toString();

        long now = System.currentTimeMillis() / 1000; //Seconds

        final ApiKey apiKey = this.internalDataStore.getApiKey();

        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("iat", now);
        body.put("jti", nonce);
        body.put("iss", apiKey.getId());
        body.put("sub", applicationHref);
        if (Strings.hasText(this.path)) {
            body.put("path", this.path);
        }
        body.put("cb_uri", this.callbackUri);
        if (Strings.hasText(this.state)) {
            body.put("state", this.state);
        }

        ObjectMapper mapper = new ObjectMapper();

        try {

            String message = mapper.writeValueAsString(body);
            JwtSigner jwtSigner = new DefaultJwtSigner(apiKey.getSecret());
            String jwt = jwtSigner.sign(message);
            return SSO_ENDPOINT + "?jwtRequest=" + jwt;

        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong when constructing the SsoRedirectUri: " + e);
        }

    }
}
