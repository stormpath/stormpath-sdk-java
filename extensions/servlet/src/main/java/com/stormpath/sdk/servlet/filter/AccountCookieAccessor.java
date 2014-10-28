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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.CookieConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

public abstract class AccountCookieAccessor {

    protected Client getClient(HttpServletRequest request) {
        return ClientResolver.INSTANCE.getClient(request.getServletContext());
    }

    protected final String getSigningKey(HttpServletRequest request) {
        Client client = getClient(request);
        DataStore ds = client.getDataStore();
        Assert.isInstanceOf(InternalDataStore.class, ds);
        InternalDataStore ids = (InternalDataStore) ds;
        return ids.getApiKey().getSecret();
    }

    protected String createJwt(HttpServletRequest request, Account account) {
        String signingKey = getSigningKey(request);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        int sessionTimeoutInSeconds = request.getServletContext().getSessionCookieConfig().getMaxAge();

        if (sessionTimeoutInSeconds < 1) {
            //just assume 30 min
            //TODO fall back to a sensible default if container session timeout is < 1
            sessionTimeoutInSeconds = 30 * 60; //30 minutes
        }

        long expMillis = nowMillis + (sessionTimeoutInSeconds * 1000);
        Date exp = new Date(expMillis);

        return Jwts.builder()
                   .setId(UUID.randomUUID().toString())
                   .setIssuedAt(now)
                   .setExpiration(exp)
                   .setSubject(account.getHref())
                   .signWith(SignatureAlgorithm.HS256, signingKey)
                   .compact();
    }


    CookieConfig getAccountCookieConfig(HttpServletRequest request) {
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext()).getAccountCookieConfig();
    }

}
