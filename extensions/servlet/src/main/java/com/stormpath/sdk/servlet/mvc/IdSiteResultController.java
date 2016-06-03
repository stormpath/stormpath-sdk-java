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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.idsite.RegistrationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.IdSiteAuthenticationRequest;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.impl.DefaultRegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.http.Saver;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdSiteResultController extends AbstractController {

    private String registerNextUri;
    private String loginNextUri;

    private Controller logoutController;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private Publisher<RequestEvent> eventPublisher;
    private AccessTokenResultFactory resultFactory; //since 1.0.0

    private List<IdSiteResultListener> idSiteResultListeners = new ArrayList<IdSiteResultListener>();

    public void setRegisterNextUri(String registerNextUri) {
        this.registerNextUri = registerNextUri;
    }

    public void setLoginNextUri(String loginNextUri) {
        this.loginNextUri = loginNextUri;
    }

    public void setLogoutController(Controller logoutController) {
        this.logoutController = logoutController;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return authenticationResultSaver;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    public void addIdSiteResultListener(IdSiteResultListener resultListener) {
        Assert.notNull(resultListener, "resultListener cannot be null");
        idSiteResultListeners.add(resultListener);
    }

    public void init() {
        Assert.hasText(registerNextUri, "registerNextUri must be configured.");
        Assert.hasText(loginNextUri, "loginNextUri must be configured.");
        Assert.notNull(logoutController, "logoutController must be configured.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver must be configured.");
        Assert.notNull(eventPublisher, "request event publisher must be configured.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    @Override
    protected ViewModel doGet(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final Application app = getApplication(request);

        final ViewModel[] viewModel = new ViewModel[1];

        IdSiteCallbackHandler idSiteCallbackHandler = app.newIdSiteCallbackHandler(request).setResultListener(new IdSiteResultListener() {
            @Override
            public void onRegistered(RegistrationResult result) {
                viewModel[0] = IdSiteResultController.this.onRegistration(request, response, app, result);
            }

            @Override
            public void onAuthenticated(com.stormpath.sdk.idsite.AuthenticationResult result) {
                viewModel[0] = IdSiteResultController.this.onAuthentication(request, response, app, result);
            }

            @Override
            public void onLogout(LogoutResult result) {
                viewModel[0] = IdSiteResultController.this.onLogout(request, response, app, result);

            }
        });

        for (IdSiteResultListener resultListener : idSiteResultListeners) {
            idSiteCallbackHandler.addResultListener(resultListener);
        }

        idSiteCallbackHandler.getAccountResult();

        return viewModel[0];
    }

    protected ViewModel onRegistration(final HttpServletRequest request, final HttpServletResponse response,
                                       Application application, RegistrationResult result) {

        final Account account = result.getAccount();

        AccountStatus status = account.getStatus();

        RequestEvent e = createRegisteredEvent(request, response, account);
        publish(e);

        if (status == AccountStatus.ENABLED) {
            //the user does not need to verify their email address, so just assume they are authenticated
            //(since they specified their password during registration):
            AuthenticationResult authcResult = new TransientAuthenticationResult(account);
            saveResult(request, response, authcResult);
        }
        // else - do we need to do anything else?

        //just redirect to post-register view:
        return new DefaultViewModel(registerNextUri).setRedirect(true);
    }

    protected ViewModel onAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         Application application,
                                         com.stormpath.sdk.idsite.AuthenticationResult result) {

        AccessTokenResult accessTokenResult = getAccessToken(result.getAccount(), request, response);
        saveResult(request, response, accessTokenResult);

        return new DefaultViewModel(loginNextUri).setRedirect(true);
    }

    protected ViewModel onLogout(HttpServletRequest request, HttpServletResponse response, Application application,
                                 LogoutResult result) {

        //let the IdSiteLogoutController know this is a reply from ID site and to not redirect to ID site again:
        request.setAttribute(LogoutResult.class.getName(), result);

        try {
            return logoutController.handleRequest(request, response);
        } catch (Exception e) {
            String msg = "Unable to successfully handle logout: " + e.getMessage();
            throw new RuntimeException(msg, e);
        }
    }

    protected RegisteredAccountRequestEvent createRegisteredEvent(HttpServletRequest request,
                                                                  HttpServletResponse response, Account account) {
        return new DefaultRegisteredAccountRequestEvent(request, response, account);
    }

    protected void publish(RequestEvent e) {
        try {
            getEventPublisher().publish(e);
        } catch (Exception ex) {
            String msg = "Unable to publish registered account request event: " + ex.getMessage();
            throw new RuntimeException(msg, ex);
        }
    }

    protected void saveResult(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        getAuthenticationResultSaver().set(request, response, result);
    }

    /**
     * @since 1.0.0
     */
    private AccessTokenResult getAccessToken(Account account, HttpServletRequest request, HttpServletResponse response)  {

        //code copied from AccessTokenController#clientCredentialsAuthenticationRequest

        Client client = ClientResolver.INSTANCE.getClient(request);
        Application application = ApplicationResolver.INSTANCE.getApplication(request);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(account.getHref())
                .setIssuer(application.getHref())
                .setAudience(client.getApiKey().getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60)))
                .claim("status", "AUTHENTICATED");
        String secret = client.getApiKey().getSecret();
        String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, secret.getBytes(Charset.forName("UTF-8"))).compact();

        IdSiteAuthenticationRequest idSiteRequest = OAuthRequests.IDSITE_AUTHENTICATION_REQUEST.builder().setToken(token).build();
        final OAuthGrantRequestAuthenticationResult origResult = Authenticators.ID_SITE_AUTHENTICATOR.forApplication(application).authenticate(idSiteRequest);

        OAuthGrantRequestAuthenticationResult result = new OAuthGrantRequestAuthenticationResult() {
            @Override
            public String getAccessTokenString() {
                return origResult.getAccessTokenString();
            }
            @Override
            public AccessToken getAccessToken() {
                return origResult.getAccessToken();
            }
            @Override
            public String getRefreshTokenString() {
                return origResult.getRefreshTokenString();
            }
            @Override
            public RefreshToken getRefreshToken() {
                return origResult.getRefreshToken();
            }
            @Override
            public String getAccessTokenHref() {
                return origResult.getAccessTokenHref();
            }
            @Override
            public String getTokenType() {
                return origResult.getTokenType();
            }
            @Override
            public long getExpiresIn() {
                return origResult.getExpiresIn();
            }
        };

        return createAccessTokenResult(request, response, result);
    }

    /**
     * @since 1.0.0
     */
    public void setAccessTokenResultFactory(AccessTokenResultFactory resultFactory) {
        this.resultFactory = resultFactory;
    }

    /**
     * @since 1.0.0
     */
    public AccessTokenResultFactory getAccessTokenResultFactory() {
        return resultFactory;
    }

    /**
     * @since 1.0.0
     */
    protected AccessTokenResult createAccessTokenResult(final HttpServletRequest request,
                                                        final HttpServletResponse response,
                                                        final OAuthGrantRequestAuthenticationResult result) {
        return getAccessTokenResultFactory().createAccessTokenResult(request, response, result);
    }
}
