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
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtToAccountConverter implements Function<String,Account> {

    private final Client client;

    public JwtToAccountConverter(Client client) {
        Assert.notNull(client, "client cannot be null");
        this.client = client;
    }

    @Override
    public Account apply(String s) {

        String secret = ClientApiKeyAccessor.INSTANCE.getApiKey(client).getSecret();

        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(s).getBody();

        String accountHref = claims.getSubject();

        //will hit the cache:
        return this.client.getResource(accountHref, Account.class);
    }
}
