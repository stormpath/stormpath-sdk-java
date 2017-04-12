/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.mvc.Controller;
import com.stormpath.sdk.servlet.mvc.ProviderAccountRequestFactory;
import com.stormpath.sdk.servlet.mvc.provider.FacebookCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.GithubCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.GoogleCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.LinkedinCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.OktaOIDCCallbackController;
import com.stormpath.sdk.servlet.util.ServletUtils;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A pre-authentication filter for Social accounts. Extracts an access token/code from the incoming callback request and
 * uses it to populate the Spring Security context with an {@link ProviderAuthenticationToken ProviderAuthenticationToken}.
 * <p>
 *
 * @since 1.3.0
 */
public class SocialCallbackSpringSecurityProcessingFilter extends HttpFilter implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(SocialCallbackSpringSecurityProcessingFilter.class);

    @Autowired
    protected ProviderAccountRequestFactory providerAccountRequestFactory;

    @Autowired
    protected Application application;

    @Autowired
    @Qualifier("stormpathAuthenticationManager")
    AuthenticationManager stormpathAuthenticationManager; // provided by stormpath-spring-security

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Autowired
    protected Controller stormpathGoogleCallbackController;

    @Autowired
    protected Controller stormpathFacebookCallbackController;

    @Autowired
    protected Controller stormpathGithubCallbackController;

    @Autowired
    protected Controller stormpathLinkedinCallbackController;

    @Autowired
    protected Controller oktaCallbackController;

    @Autowired
    @Qualifier("stormpathAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Autowired
    @Qualifier("stormpathAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Value("#{ @environment['stormpath.web.social.google.uri'] ?: '/callbacks/google' }")
    protected String googleCallbackUri;

    @Value("#{ @environment['stormpath.web.social.facebook.uri'] ?: '/callbacks/facebook' }")
    protected String facebookCallbackUri;

    @Value("#{ @environment['stormpath.web.social.linkedin.uri'] ?: '/callbacks/linkedin' }")
    protected String linkedinCallbackUri;

    @Value("#{ @environment['stormpath.web.social.github.uri'] ?: '/callbacks/github' }")
    protected String githubCallbackUri;

    @Value("#{ @environment['stormpath.web.social.okta.uri'] ?: '/callbacks/okta' }")
    protected String oktaCallbackUri;


    public void afterPropertiesSet() {
    }

    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        String requestUri = ServletUtils.getRequestUri(request);
        ProviderAccountRequest providerAccountRequest = null;
        try {
            if (requestUri.equals(googleCallbackUri)) {
                providerAccountRequest = ((GoogleCallbackController)stormpathGoogleCallbackController).getAccountProviderRequest(request);
            } else if (requestUri.equals(facebookCallbackUri)) {
                providerAccountRequest = ((FacebookCallbackController)stormpathFacebookCallbackController).getAccountProviderRequest(request);
            } else if (requestUri.equals(githubCallbackUri)) {
                providerAccountRequest = ((GithubCallbackController)stormpathGithubCallbackController).getAccountProviderRequest(request);
            } else if (requestUri.equals(linkedinCallbackUri)) {
                providerAccountRequest = ((LinkedinCallbackController)stormpathLinkedinCallbackController).getAccountProviderRequest(request);
            } else if (requestUri.equals(oktaCallbackUri)) {
                providerAccountRequest = ((OktaOIDCCallbackController)oktaCallbackController).getAccountProviderRequest(request);
            }
        } catch (Exception e) {
            logger.error("Exception handling social callback request", e);
            failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException(e.getMessage()));
        }

        if (providerAccountRequest == null) { //if exception was thrown then the providerAccountRequest will also be null and the chain will continue
            chain.doFilter(request, response);
            return;
        }
        ProviderAccountResult result = application.getAccount(providerAccountRequest);
        Account account = result.getAccount();
        if (account != null) {

            // 751: Check if account is unverified and let the failureHandler do the rest
            if (account.getStatus().equals(AccountStatus.UNVERIFIED)) {
                failureHandler.onAuthenticationFailure(request, response, new LockedException("Unverified account."));
            }

            Authentication authentication = stormpathAuthenticationManager.authenticate(new ProviderAuthenticationToken(result));
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            successHandler.onAuthenticationSuccess(request, response, authentication);
        }

        chain.doFilter(request, response);
    }

}