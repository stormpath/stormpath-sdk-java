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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.spring.csrf.SpringSecurityCsrfTokenManager;
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter;
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter;
import com.stormpath.spring.security.provider.SpringSecurityIdSiteResultListener;
import com.stormpath.spring.security.provider.SpringSecuritySamlResultListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;


/**
 * @since 1.0.RC5
 */
@Order(99)
public abstract class AbstractStormpathWebSecurityConfiguration {

    @Autowired
    protected Client client;

    @Autowired
    protected Application application;

    @Autowired
    @Qualifier("stormpathAuthenticationProvider")
    protected AuthenticationProvider stormpathAuthenticationProvider; //provided by stormpath-spring-security

    @Autowired
    @Qualifier("stormpathAuthenticationResultSaver")
    protected Saver<AuthenticationResult> authenticationResultSaver; //provided by stormpath-spring-webmvc

    @Autowired
    @Qualifier("stormpathRequestEventPublisher")
    private Publisher<RequestEvent> stormpathRequestEventPublisher; //provided by stormpath-spring-webmvc

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.me.uri'] ?: '/me' }")
    protected String meUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.csrf.token.name'] ?: '_csrf'}")
    protected String csrfTokenName;

    @Value("#{ @environment['stormpath.web.csrf.token.enabled'] ?: true }")
    protected boolean csrfTokenEnabled;

    @Value("#{ @environment['stormpath.spring.security.csrf.token.repository'] ?: 'session' }")
    protected String csrfTokenRepository;

    @Value("#{ @environment['stormpath.web.oauth2.enabled'] ?: true }")
    protected boolean accessTokenEnabled;

    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String produces;

    public StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer() {
        return new StormpathWebSecurityConfigurer();
    }

    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        StormpathLoginSuccessHandler loginSuccessHandler =
            new StormpathLoginSuccessHandler(client, authenticationResultSaver, produces);
        loginSuccessHandler.setDefaultTargetUrl(loginNextUri);
        loginSuccessHandler.setTargetUrlParameter("next");
        loginSuccessHandler.setRequestCache(new NullRequestCache());
        return loginSuccessHandler;
    }

    public AuthenticationFailureHandler stormpathAuthenticationFailureHandler() {
        String loginFailureUri = loginUri + "?error";
        return new StormpathAuthenticationFailureHandler(
            loginFailureUri, stormpathRequestEventPublisher,
            stormpathLoginErrorModelFactory(), produces
        );
    }

    public ErrorModelFactory stormpathLoginErrorModelFactory() {
        return new SpringSecurityLoginErrorModelFactory();
    }

    public LogoutHandler stormpathLogoutHandler() {
        return new StormpathLogoutHandler(authenticationResultSaver);
    }

    public IdSiteResultListener springSecurityIdSiteResultListener() {
        return new SpringSecurityIdSiteResultListener(stormpathAuthenticationProvider);
    }

    public SamlResultListener springSecuritySamlResultListener() {
        return new SpringSecuritySamlResultListener(stormpathAuthenticationProvider);
    }

    public CsrfTokenRepository stormpathCsrfTokenRepository() {
        // Fix for https://github.com/stormpath/stormpath-sdk-java/issues/918: Use session-based CSRF token repository
        // by default, since that's what Spring Security uses by default
        if ("session".equals(csrfTokenRepository)) {
            HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
            csrfTokenRepository.setParameterName(csrfTokenName);
            return csrfTokenRepository;
        } else if ("cookie".equals(csrfTokenRepository)) {
            // Fix for https://github.com/stormpath/stormpath-sdk-java/issues/918: Allow cookie-based repository instead of
            // HttpSessionCsrfTokenRepository so Spring Security can be configured stateless with
            // sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // This is used when `stormpath.web.csrf.token.repository` is set to `cookie`.
            CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
            csrfTokenRepository.setParameterName(csrfTokenName);
            return csrfTokenRepository;
        } else {
            throw new IllegalArgumentException("CSRF Token Repository '" + csrfTokenRepository +
                    "' is not supported. Please set stormpath.spring.security.csrf.token.repository to 'session' or 'cookie'.");
        }
    }

    public CsrfTokenManager stormpathCsrfTokenManager() {
        //Spring Security supports CSRF protection only in Thymeleaf or JSP's with Sec taglib., therefore we
        //cannot just delegate the CSRF strategy to Spring Security, we need to handle it ourselves in Spring.
        if (csrfTokenEnabled) {
            return new SpringSecurityCsrfTokenManager(stormpathCsrfTokenRepository(), csrfTokenName);
        }
        return new DisabledCsrfTokenManager(csrfTokenName);

    }

    public OAuthAuthenticationSpringSecurityProcessingFilter oAuthAuthenticationProcessingFilter() {
        OAuthAuthenticationSpringSecurityProcessingFilter filter = new OAuthAuthenticationSpringSecurityProcessingFilter();
        filter.setEnabled(accessTokenEnabled);
        return filter;
    }

    public SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter() {
        return new SpringSecurityResolvedAccountFilter();
    }

    public AuthenticationEntryPoint stormpathAuthenticationEntryPoint() {
        return new StormpathAuthenticationEntryPoint(loginUri, produces, meUri, application.getName());
    }
}
