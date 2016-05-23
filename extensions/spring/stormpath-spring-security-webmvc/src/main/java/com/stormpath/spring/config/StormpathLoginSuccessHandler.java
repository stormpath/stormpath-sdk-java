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
package com.stormpath.spring.config;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.oauth.PasswordGrantAccessTokenResult;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @since 1.0.RC5
 */
public class StormpathLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private Client stormpathClient;

    private Saver<AuthenticationResult> authenticationResultSaver;

    @Autowired
    private Publisher<RequestEvent> stormpathRequestEventPublisher;

    @Autowired
    private Application application;

    @Autowired
    private Client client;

    public StormpathLoginSuccessHandler(Client client, Saver<AuthenticationResult> saver) {
        this.stormpathClient = client;
        this.authenticationResultSaver = saver;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        saveAccount(request, response, authentication);

        SuccessfulAuthenticationRequestEvent e = createSuccessEvent(request, response, getAccount(authentication));
        stormpathRequestEventPublisher.publish(e);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected void saveAccount(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        Account account = getAccount(authentication);

        String jwt = Jwts.builder()
                             .setHeaderParam(JwsHeader.KEY_ID, client.getApiKey().getId())
                             .setSubject(account.getHref())
                             .setIssuedAt(new Date())
                             .setIssuer(application.getHref())
                             .setAudience(client.getApiKey().getId())
                             .setExpiration(DateTime.now().plusMinutes(1).toDate())
                             .claim("status", "AUTHENTICATED").signWith(SignatureAlgorithm.HS256, client.getApiKey().getSecret().getBytes("UTF-8")).compact();

        OAuthRequestAuthentication authenticationRequest = OAuthRequests.IDSITE_AUTHENTICATION_REQUEST.builder().setToken(jwt).build();
        OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.ID_SITE_AUTHENTICATOR.forApplication(application).authenticate(authenticationRequest);

        authenticationResultSaver.set(request, response, createAccessTokenResult(authenticationResult));
    }

    protected AccessTokenResult createAccessTokenResult(final OAuthGrantRequestAuthenticationResult result) {
        final TokenResponse tokenResponse =
                DefaultTokenResponse.tokenType(TokenType.BEARER)
                        .accessToken(result.getAccessTokenString())
                        .refreshToken(result.getRefreshTokenString())
                        .applicationHref(application.getHref())
                        .expiresIn(String.valueOf(result.getExpiresIn())).build();
        return new PasswordGrantAccessTokenResult(result.getAccessToken().getAccount(), tokenResponse);
    }

    protected Account getAccount(Authentication authentication) {
        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        return stormpathClient.getResource(accountHref, Account.class);
    }

    protected SuccessfulAuthenticationRequestEvent createSuccessEvent(HttpServletRequest request,
                                                                      HttpServletResponse response,
                                                                      Account account) {
        return new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, new TransientAuthenticationResult(account));
    }

}
