/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.UUID;

import static com.stormpath.sdk.impl.idsite.IdSiteClaims.ACCESS_TOKEN;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlIdpUrlBuilder implements SamlIdpUrlBuilder {

    public static String SSO_LOGOUT_SUFFIX = "/logout";
    public final String ssoEndpoint;
    private final InternalDataStore internalDataStore;
    private final String applicationHref;
    private final SamlClaims claims;

    private boolean logout = false;

    public DefaultSamlIdpUrlBuilder(InternalDataStore internalDataStore, String applicationHref, String samlProviderEndpoint) {
        Assert.notNull(internalDataStore, "internalDataStore cannot be null.");
        Assert.hasText(samlProviderEndpoint, "samlProviderEndpoint cannot be null or empty");

        this.ssoEndpoint = samlProviderEndpoint;
        this.internalDataStore = internalDataStore;
        this.applicationHref = applicationHref;
        this.claims = new SamlClaims();
    }

    @Override
    public SamlIdpUrlBuilder setCallbackUri(String callbackUri) {
        claims.setCallbackUri(callbackUri);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder setState(String state) {
        claims.setState(state);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder setPath(String path) {
        claims.setPath(path);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder setOrganizationNameKey(String organizationNameKey) {
        claims.setOrganizationNameKey(organizationNameKey);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder setSpToken(String spToken) {
        claims.setSpToken(spToken);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder setAccountStoreHref(String accountStoreHref) {
        claims.setAccountStoreHref(accountStoreHref);
        return this;
    }

    @Override
    public SamlIdpUrlBuilder addProperty(String name, Object value) {
        claims.put(name, value);
        return this;
    }

    @Override
    public String build() {
        Assert.state(Strings.hasText(claims.getCallbackUri()), "callbackUri cannot be null or empty.");

        String jti = UUID.randomUUID().toString();

        Date now = new Date();

        final ApiKey apiKey = this.internalDataStore.getApiKey();

        JwtBuilder jwtBuilder = Jwts.builder().setClaims(claims).setId(jti).setIssuedAt(now).setIssuer(apiKey.getId())
                .setSubject(this.applicationHref);

        byte[] secret = apiKey.getSecret().getBytes(Strings.UTF_8);

        String jwt = jwtBuilder
                .setHeaderParam(JwsHeader.TYPE, JwsHeader.JWT_TYPE)
                .setHeaderParam(JwsHeader.KEY_ID, apiKey.getId())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        QueryString queryString = new QueryString();
        queryString.put(ACCESS_TOKEN, jwt);

        StringBuilder urlBuilder = new StringBuilder(ssoEndpoint);

        if (logout) {
            urlBuilder.append(SSO_LOGOUT_SUFFIX);
        }

        return urlBuilder.append('?').append(queryString.toString()).toString();
    }
}
