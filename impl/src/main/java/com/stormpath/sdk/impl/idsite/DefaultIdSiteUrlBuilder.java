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
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.UUID;

import static com.stormpath.sdk.impl.jwt.IdSiteClaims.*;

/**
 * @since 1.0.RC
 */
public class DefaultIdSiteUrlBuilder implements IdSiteUrlBuilder {

    public static String SSO_LOGOUT_SUFFIX = "/logout";

    private final InternalDataStore internalDataStore;

    public final String ssoEndpoint;

    private final String applicationHref;

    private String callbackUri;

    private String state;

    private String path;

    private boolean logout = false;

    private String organizationNameKey;

    private boolean useSubdomain = false;

    private boolean showOrganizationField = false;

    public DefaultIdSiteUrlBuilder(InternalDataStore internalDataStore, String applicationHref) {
        Assert.notNull(internalDataStore, "internalDataStore cannot be null.");
        Assert.hasText(applicationHref, "applicationHref cannot be null or empty");

        this.ssoEndpoint = getBaseUrl(applicationHref) + "/sso";
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
    public IdSiteUrlBuilder setOrganizationNameKey(String organizationNameKey) {
        this.organizationNameKey = organizationNameKey;
        return this;
    }

    @Override
    public IdSiteUrlBuilder setUseSubdomain(boolean useSubdomain) {
        this.useSubdomain = useSubdomain;
        return this;
    }

    @Override
    public IdSiteUrlBuilder setShowOrganizationField(boolean showOrganizationField) {
        this.showOrganizationField = showOrganizationField;
        return this;
    }

    @Override
    public IdSiteUrlBuilder forLogout() {
        this.logout = true;
        return this;
    }

    @Override
    public String build() {
        Assert.state(Strings.hasText(this.callbackUri), "callbackUri cannot be null or empty.");

        String jti = UUID.randomUUID().toString();

        Date now = new Date();

        final ApiKey apiKey = this.internalDataStore.getApiKey();

        JwtBuilder jwtBuilder = Jwts.builder().setId(jti).setIssuedAt(now).setIssuer(apiKey.getId())
                .setSubject(this.applicationHref).claim(REDIRECT_URI, this.callbackUri);

        if (Strings.hasText(this.path)) {
            jwtBuilder.claim(PATH, this.path);
        }

        if (Strings.hasText(this.state)) {
            jwtBuilder.claim(STATE, this.state);
        }

        if (Strings.hasText(this.organizationNameKey)) {
            jwtBuilder.claim(ORGANIZATION_NAME_KEY, organizationNameKey);
        }

        jwtBuilder.claim(USE_SUBDOMAIN, useSubdomain).claim(SHOW_ORGANIZATION_FIELD, showOrganizationField);

        byte[] secret = apiKey.getSecret().getBytes(Strings.UTF_8);

        String jwt = jwtBuilder.setHeaderParam(JwsHeader.TYPE, JwsHeader.JWT_TYPE).signWith(SignatureAlgorithm.HS256, secret).compact();

        QueryString queryString = new QueryString();
        queryString.put(JWT_REQUEST, jwt);

        StringBuilder urlBuilder = new StringBuilder(ssoEndpoint);

        if (logout) {
            urlBuilder.append(SSO_LOGOUT_SUFFIX);
        }

        return urlBuilder.append('?').append(queryString.toString()).toString();
    }

    /**
     * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/184.
     * Base URL for IDSite is constructed from the applicationHref received in the constructor.
     *
     * @version 1.0.RC4.2
     */
    protected String getBaseUrl(String href) {

        String baseUrl;
        try {
            final String DOUBLE_SLASH = "//";

            int doubleSlashIndex = href.indexOf(DOUBLE_SLASH);
            int singleSlashIndex = href.indexOf("/", doubleSlashIndex + DOUBLE_SLASH.length());
            singleSlashIndex = singleSlashIndex != -1 ? singleSlashIndex : href.length();

            baseUrl = href.substring(0, singleSlashIndex);

        } catch (Exception e) {
            throw new IllegalStateException("IDSite base URL could not be constructed.");
        }

        if (!Strings.hasText(baseUrl)) {
            throw new IllegalStateException("IDSite base URL could not be constructed.");
        }

        return baseUrl;
    }

}
