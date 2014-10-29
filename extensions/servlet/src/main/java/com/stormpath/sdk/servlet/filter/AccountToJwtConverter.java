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
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.UUID;

public class AccountToJwtConverter implements Function<Account,String> {

    private final String secret;
    private final int ttlSeconds;

    public AccountToJwtConverter(String base64EncodedSecret, int jwtTtlSeconds) {
        Assert.hasText(base64EncodedSecret, "secret cannot be null or empty.");
        this.secret = base64EncodedSecret;
        this.ttlSeconds = jwtTtlSeconds;
    }

    @Override
    public String apply(Account account) {

        long nowMillis = System.currentTimeMillis();

        Date now = new Date(nowMillis);
        String id = UUID.randomUUID().toString();
        String href = account.getHref();
        Assert.hasText(href, "Account href cannot be null or empty.");

        JwtBuilder builder = Jwts.builder()
                   .setId(id)
                   .setIssuedAt(now)
                   .setSubject(href)
                   .signWith(SignatureAlgorithm.HS256, secret);

        if (this.ttlSeconds >= 0) {
            long expMillis = nowMillis + ttlSeconds;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }
}
