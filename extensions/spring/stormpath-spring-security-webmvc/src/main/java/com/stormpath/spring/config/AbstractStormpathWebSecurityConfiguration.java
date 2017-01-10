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
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.ProviderAccountRequestFactory;
import com.stormpath.spring.csrf.SpringSecurityCsrfTokenManager;
import com.stormpath.spring.filter.ContentNegotiationSpringSecurityAuthenticationFilter;
import com.stormpath.spring.filter.StormpathSecurityContextPersistenceFilter;
import com.stormpath.spring.filter.StormpathWrapperFilter;
import com.stormpath.spring.security.provider.SocialCallbackSpringSecurityProcessingFilter;
import com.stormpath.spring.security.provider.SpringSecurityIdSiteResultListener;
import com.stormpath.spring.security.provider.SpringSecuritySamlResultListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

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

    /**
     * @since 1.3.0
     */
    @Autowired
    ProviderAccountRequestFactory stormpathProviderAccountRequestFactory; //provided by stormpath-spring-webmvc

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

    @Autowired
    @Qualifier("stormpathAuthenticationManager")
    AuthenticationManager stormpathAuthenticationManager; // provided by stormpath-spring-security

    @Value("#{ @environment['stormpath.web.cors.enabled'] ?: true }")
    protected boolean corsEnabled;

    @Value("#{ @environment['stormpath.web.cors.allowed.originUris'] }")
    protected String corsAllowedOrigins;

    @Value("#{ @environment['stormpath.web.cors.allowed.headers'] ?: 'Content-Type,Accept,X-Requested-With,remember-me' }")
    protected String corsAllowedHeaders;

    @Value("#{ @environment['stormpath.web.cors.allowed.methods'] ?: 'POST,GET,OPTIONS,DELETE,PUT' }")
    protected String corsAllowedMethods;

    @Value("#{ @environment['stormpath.web.stormpathFilter.enabled'] ?: true }")
    protected boolean stormpathFilterEnabled;

    @Autowired
    List<Resolver<Account>> stormpathAccountResolvers;

    @Value("#{ @environment['stormpath.web.oauth2.uri'] ?: '/oauth/token' }")
    protected String accessTokenUri;

    @Value("#{ @environment['stormpath.web.request.client.attributeNames'] ?: 'client' }")
    protected String clientRequestAttributeNames;

    @Value("#{ @environment['stormpath.web.request.application.attributeNames'] ?: 'application' }")
    protected String applicationRequestAttributeNames;

    @Autowired
    @Qualifier("stormpathWrappedServletRequestFactory")
    private WrappedServletRequestFactory wrappedServletRequestFactory;

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

    public StormpathSecurityContextPersistenceFilter stormpathSecurityContextPersistenceFilter() {
        StormpathSecurityContextPersistenceFilter filter = new StormpathSecurityContextPersistenceFilter();
        return filter;
    }

    public SocialCallbackSpringSecurityProcessingFilter socialCallbackSpringSecurityProcessingFilter() {
        SocialCallbackSpringSecurityProcessingFilter filter = new SocialCallbackSpringSecurityProcessingFilter();
        filter.setEnabled(true);
        return filter;
    }

    // This sets up the Content Negotiation aware filter and replaces the calls to http.formLogin()
    // refer to: https://github.com/stormpath/stormpath-sdk-java/issues/682
    public ContentNegotiationSpringSecurityAuthenticationFilter contentNegotiationSpringSecurityAuthenticationFilter() {
        ContentNegotiationSpringSecurityAuthenticationFilter filter = new ContentNegotiationSpringSecurityAuthenticationFilter();

        filter.setSupportedMediaTypes(MediaType.parseMediaTypes(produces));
        filter.setAuthenticationManager(stormpathAuthenticationManager);
        filter.setUsernameParameter("login");
        filter.setPasswordParameter("password");
        filter.setAuthenticationSuccessHandler(stormpathAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(stormpathAuthenticationFailureHandler());

        /**
         * @since 1.3.0
         */
        filter.setProviderAccountRequestFactory(stormpathProviderAccountRequestFactory);

        return filter;
    }

    /**
     * @since 1.3.0
     */
    public AccountResolverFilter springSecurityResolvedAccountFilter() {
        AccountResolverFilter accountResolverFilter = new AccountResolverFilter();
        accountResolverFilter.setEnabled(stormpathFilterEnabled);
        accountResolverFilter.setResolvers(stormpathAccountResolvers);
        accountResolverFilter.setOauthEndpointUri(accessTokenUri);
        return accountResolverFilter;
    }


    public AuthenticationEntryPoint stormpathAuthenticationEntryPoint() {
        return new StormpathAuthenticationEntryPoint(loginUri, produces, meUri, application.getName());
    }

    /**
     * To be used by Spring Security's built-in CORS support.
     *
     * @since 1.3.0
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Strings.split(corsAllowedOrigins) != null ? Arrays.asList(Strings.split(corsAllowedOrigins)) : Collections.<String>emptyList());
        configuration.setAllowedHeaders(Strings.split(corsAllowedHeaders) != null ? Arrays.asList(Strings.split(corsAllowedHeaders)) : Collections.<String>emptyList());
        configuration.setAllowedMethods(Strings.split(corsAllowedMethods) != null ? Arrays.asList(Strings.split(corsAllowedMethods)) : Collections.<String>emptyList());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * This filter adds Client and Application as attributes to every request in order for subsequent Filters to have access to them.
     * For example, a filter trying to validate an access token will need to have access to the Application (see AuthorizationHeaderAccountResolver)
     *
     * @since 1.3.0
     */
    public StormpathWrapperFilter stormpathWrapperFilter() {
        Assert.notNull(clientRequestAttributeNames, "clientRequestAttributeNames cannot be null.");
        Assert.notNull(applicationRequestAttributeNames, "applicationRequestAttributeNames cannot be null.");
        StormpathWrapperFilter filter = new StormpathWrapperFilter();
        filter.setClientRequestAttributeNames(Strings.split(clientRequestAttributeNames) != null ? new LinkedHashSet<>(Arrays.asList(Strings.split(clientRequestAttributeNames))) : Collections.<String>emptySet());
        filter.setApplicationRequestAttributeNames(Strings.split(applicationRequestAttributeNames) != null ? new LinkedHashSet<>(Arrays.asList(Strings.split(applicationRequestAttributeNames))) : Collections.<String>emptySet());
        filter.setClient(client);
        filter.setApplication(application);
        filter.setWrappedServletRequestFactory(wrappedServletRequestFactory);
        return filter;
    }
}
