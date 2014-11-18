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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

public class DefaultAccountJwtFactory implements AccountJwtFactory {

    @Override
    public String createAccountJwt(HttpServletRequest request, HttpServletResponse response, Account account) {

        String id = UUID.randomUUID().toString();

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        String sub = getJwtSubject(request, account);
        Assert.hasText(sub, "JWT subject value cannot be null or empty.");

        String signingKey = getJwtSigningKey(request, account);

        JwtBuilder builder =
            Jwts.builder().setId(id).setIssuedAt(now).setSubject(sub).signWith(SignatureAlgorithm.HS256, signingKey);

        int ttl = getJwtTtlSeconds(request, account);
        if (ttl >= 0) {
            long ttlMillis = ttl * 1000; //JWT requires times to be in seconds (not millis) since epoch
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    protected String getJwtSubject(HttpServletRequest request, Account account) {

        // If the account was authenticated by HTTP Basic authentication using an API Key, the
        // com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme implementation will indicate this by exposing
        // the API Key used as a request attribute.  So we check for the presence of this attribute to know if the JWT
        // should retain knowledge of how the account was authenticated.

        ApiKey apiKey = (ApiKey)request.getAttribute(ApiKey.class.getName());
        if (apiKey != null) {
            //request was authenticated with an API Key (and not username/password authentication):
            return apiKey.getHref();
        }

        // otherwise the request was authenticated with username/password authentication, so just represent the account
        // directly:
        return account.getHref();
    }

    protected String getJwtSigningKey(HttpServletRequest request, Account account) {
        Client client = ClientResolver.INSTANCE.getClient(request.getServletContext());
        ApiKey apiKey = client.getApiKey();
        return apiKey.getSecret();
    }

    protected int getJwtTtlSeconds(HttpServletRequest request, Account account) {
        Config config = ConfigResolver.INSTANCE.getConfig(request.getServletContext());
        return config.getAccountJwtTtl();
    }
}
