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
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.Servlets;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

public class DefaultJwtAccountResolver implements JwtAccountResolver, ServletContextInitializable {

    private Client client;
    private JwtSigningKeyResolver jwtSigningKeyResolver;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        this.client = Servlets.getClient(servletContext);
        Config config = (Config) servletContext.getAttribute(Config.class.getName());
        this.jwtSigningKeyResolver = config.getInstance("stormpath.web.account.jwt.signingKey.resolver");
    }

    protected Client getClient() {
        return this.client;
    }

    protected JwtSigningKeyResolver getJwtSigningKeyResolver() {
        return this.jwtSigningKeyResolver;
    }

    @Override
    public Account getAccountByJwt(final HttpServletRequest request, final HttpServletResponse response, String jwt) {

        final JwtSigningKeyResolver resolver = getJwtSigningKeyResolver();

        SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                String base64Encoded = resolver.getSigningKey(request, response, header, claims);
                return DatatypeConverter.parseBase64Binary(base64Encoded);
            }
        };

        Claims claims = Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(jwt).getBody();

        String accountHref = claims.getSubject();

        //will hit the cache:
        Client client = getClient();
        return client.getResource(accountHref, Account.class);
    }
}
