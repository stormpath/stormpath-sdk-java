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
package com.stormpath.sdk.servlet.http.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.oauth.OauthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OauthException;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class BearerAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Logger log = LoggerFactory.getLogger(BearerAuthenticationScheme.class);

    private static final String NAME = "Bearer";

    private JwtSigningKeyResolver jwtSigningKeyResolver;

    public BearerAuthenticationScheme(JwtSigningKeyResolver jwtSigningKeyResolver) {
        Assert.notNull(jwtSigningKeyResolver, "JwtSigningKeyResolver cannot be null.");
        this.jwtSigningKeyResolver = jwtSigningKeyResolver;
    }

    @Override
    public String getName() {
        return NAME;
    }

    protected JwtSigningKeyResolver getJwtSigningKeyResolver() {
        return this.jwtSigningKeyResolver;
    }

    @Override
    public HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt) throws HttpAuthenticationException {

        Assert.notNull(attempt, "attempt cannot be null.");

        HttpServletRequest request = attempt.getRequest();
        Assert.notNull(request, "attempt request property cannot be null.");

        HttpServletResponse response = attempt.getResponse();
        Assert.notNull(response, "attempt response property cannot be null.");

        HttpCredentials credentials = attempt.getCredentials();
        Assert.notNull(credentials, "credentials cannot be null.");
        Assert.isTrue(NAME.equalsIgnoreCase(credentials.getSchemeName()), "Unsupported scheme.");

        final String token = attempt.getCredentials().getSchemeValue();
        Assert.hasText(token, "Cannot authenticate empty Bearer value.");

        try {

            HttpAuthenticationResult result = authenticate(request, response, token);

            request.setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME,
                                 StormpathHttpServletRequest.AUTH_TYPE_BEARER);

            return result;

        } catch (OauthException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");

            try {
                response.getWriter().print(e.toJson());
                response.getWriter().flush();
            } catch (IOException e2) {
                throw new HttpAuthenticationException("Unable to render OAuth error response body: " + e2.getMessage(),
                                                      e2);
            }

            throw new HttpAuthenticationException("OAuth request authentication failed: " + e.getMessage(), e);
        }
    }

    protected HttpAuthenticationResult authenticate(final HttpServletRequest request,
                                                    final HttpServletResponse response, String token) {

        Jws<Claims> jws;

        try {
            final JwtSigningKeyResolver resolver = getJwtSigningKeyResolver();

            SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
                @Override
                public Key resolveSigningKey(JwsHeader header, Claims claims) {
                    return resolver.getSigningKey(request, response, header, claims);
                }
            };

            jws = Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(token);

            return createAuthenticationResult(request, response, jws);

        } catch (ExpiredJwtException e) {
            throw new OauthException(OauthErrorCode.INVALID_CLIENT, "access_token is expired.", null, e);
        } catch (OauthException e) {
            throw e;
        } catch (Exception e) {
            log.debug("JWT verification failed.", e);
            throw new OauthException(OauthErrorCode.INVALID_CLIENT, "access_token is invalid.", null, e);
        }
    }

    protected HttpAuthenticationResult createAuthenticationResult(HttpServletRequest request,
                                                                  HttpServletResponse response, Jws<Claims> jws)
        throws OauthException {

        String href = jws.getBody().getSubject();

        AuthenticationResult authcResult;

        if (href.contains("apiKeys")) {

            int i = href.lastIndexOf('/');
            String id = href.substring(i + 1);
            final ApiKey apiKey = getTokenApiKey(request, id);

            authcResult = new OauthAuthenticationResult() {
                @Override
                public Set<String> getScope() {
                    return Collections.emptySet();
                }

                @Override
                public ApiKey getApiKey() {
                    return apiKey;
                }

                @Override
                public Account getAccount() {
                    return apiKey.getAccount();
                }

                @Override
                public void accept(AuthenticationResultVisitor visitor) {
                    visitor.visit(this);

                }

                @Override
                public String getHref() {
                    //there is no href of this result itself (account href != result href)
                    return null;
                }
            };
        } else {

            Client client = getClient(request);

            final Account account = client.getResource(href, Account.class);

            if (account.getStatus() != AccountStatus.ENABLED) {
                throw new OauthException(OauthErrorCode.INVALID_CLIENT, "account is disabled.", null);
            }

            authcResult = new AuthenticationResult() {
                @Override
                public Account getAccount() {
                    return account;
                }

                @Override
                public void accept(AuthenticationResultVisitor visitor) {
                    visitor.visit(this);

                }

                @Override
                public String getHref() {
                    //there is no href of this result itself (account href != result href)
                    return null;
                }
            };
        }

        return new DefaultHttpAuthenticationResult(request, response, authcResult);
    }

    protected Client getClient(HttpServletRequest request) {
        return (Client)request.getAttribute(Client.class.getName());
    }

    /**
     * Retrieves the {@link ApiKey} instance pointed by this {@code apiKeyId} and accessible from the {@code
     * application} <p/> The ApiKey is retrieved from the {@link Application} passed as argument. <p/> This method
     * asserts that the ApiKey retrieved status is {@link ApiKeyStatus#ENABLED} and also that the status of the account
     * owner is {@link AccountStatus#ENABLED}
     *
     * @param apiKeyId - The id of the {@link ApiKey} embedded in the access token.
     */
    protected ApiKey getTokenApiKey(HttpServletRequest request, String apiKeyId) throws OauthException {
        try {
            return getEnabledApiKey(request, apiKeyId);
        } catch (ResourceException e) {
            OauthErrorCode err = OauthErrorCode.INVALID_CLIENT;
            String msg = e.getStormpathError().getDeveloperMessage();
            throw new OauthException(err, msg, null, e);
        }
    }
}
