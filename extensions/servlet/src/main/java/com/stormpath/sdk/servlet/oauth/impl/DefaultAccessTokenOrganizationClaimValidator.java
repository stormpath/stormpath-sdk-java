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
package com.stormpath.sdk.servlet.oauth.impl;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.oauth.AccessTokenOrganizationClaimValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.2.0
 */
public class DefaultAccessTokenOrganizationClaimValidator implements AccessTokenOrganizationClaimValidator {

    private static final String ORG_CLAIM = "org";

    private Resolver<String> organizationNameKeyResolver;

    public DefaultAccessTokenOrganizationClaimValidator(Resolver<String> organizationNameKeyResolver) {
        Assert.notNull(organizationNameKeyResolver, "organizationNameKeyResolver cannot be null.");
        this.organizationNameKeyResolver = organizationNameKeyResolver;
    }

    @Override
    public boolean isValid(HttpServletRequest request, HttpServletResponse response, String token) {
        //https://github.com/stormpath/stormpath-sdk-java/issues/742
        //Check if the resolved organization matches the org claim inside the token, the claim is not there do nothing,
        //if the organization resolver returns nothing and the claim is there throw an exception, if the the claim is not there
        //and the organization resolver is not there do nothing, finally if the claim and the resolved org don't match throw an exception,
        //otherwise do nothing.
        Client client = ClientResolver.INSTANCE.getClient(request);
        byte[] clientSecret = client.getApiKey().getSecret().getBytes();
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(clientSecret).parseClaimsJws(token);
        String orgHref = claimsJws.getBody().get(ORG_CLAIM, String.class);
        if (Strings.hasText(orgHref)) {
            Organization organization = client.getResource(orgHref, Organization.class);

            String resolvedOrgNameKey = organizationNameKeyResolver.get(request, response);

            //TODO review this, not so sure about this case but it makes sense if no organization is resolve and token has an org claim sounds like an invalid authentication.
            if (!Strings.hasText(resolvedOrgNameKey)) {
                return false;
            }

            if (!organization.getNameKey().equals(resolvedOrgNameKey)) {
                return false;
            }
        }
        return true;
    }
}
