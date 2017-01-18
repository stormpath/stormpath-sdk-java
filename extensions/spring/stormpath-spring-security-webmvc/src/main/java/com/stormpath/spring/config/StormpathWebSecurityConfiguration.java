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

import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.spring.filter.ContentNegotiationSpringSecurityAuthenticationFilter;
import com.stormpath.spring.filter.StormpathSecurityContextPersistenceFilter;
import com.stormpath.spring.filter.StormpathWrapperFilter;
import com.stormpath.spring.security.provider.SocialCallbackSpringSecurityProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * @since 1.0.RC5
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@EnableStormpathWebMvc
@EnableStormpathSecurity
@Conditional(StormpathSpringSecurityEnabled.class)
public class StormpathWebSecurityConfiguration extends AbstractStormpathWebSecurityConfiguration {

    @Bean
    @Override
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return super.stormpathAuthenticationSuccessHandler();
    }

    @Bean
    @Override
    public AuthenticationFailureHandler stormpathAuthenticationFailureHandler() {
        return super.stormpathAuthenticationFailureHandler();
    }

    @Bean
    @Override
    public SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter() {
        return super.stormpathSecurityConfigurerAdapter();
    }

    @Bean
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

    @Bean
    public CsrfTokenRepository stormpathCsrfTokenRepository() {
        return super.stormpathCsrfTokenRepository();
    }

    @Bean
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Bean
    @Override
    public ErrorModelFactory stormpathLoginErrorModelFactory() {
        return super.stormpathLoginErrorModelFactory();
    }

    @Bean
    public IdSiteResultListener springSecurityIdSiteResultListener() {
        return super.springSecurityIdSiteResultListener();
    }

    @Bean
    @Override
    public StormpathSecurityContextPersistenceFilter stormpathSecurityContextPersistenceFilter() {
        return super.stormpathSecurityContextPersistenceFilter();
    }

    @Bean
    @Override
    public SocialCallbackSpringSecurityProcessingFilter socialCallbackSpringSecurityProcessingFilter() {
        return super.socialCallbackSpringSecurityProcessingFilter();
    }

    /**
     * @since 1.3.0
     */
    @Bean
    @Override
    public ContentNegotiationSpringSecurityAuthenticationFilter contentNegotiationSpringSecurityAuthenticationFilter() {
        return super.contentNegotiationSpringSecurityAuthenticationFilter();
    }

    @Bean
    @Override
    public AuthenticationEntryPoint stormpathAuthenticationEntryPoint() {
        return super.stormpathAuthenticationEntryPoint();
    }

    /**
     * @since 1.3.0
     */
    @Bean
    @Override
    public CorsConfigurationSource corsConfigurationSource() {
        return super.corsConfigurationSource();
    }

    /**
     * @since 1.3.0
     */
    @Bean
    public AccountResolverFilter springSecurityResolvedAccountFilter() {
        return super.springSecurityResolvedAccountFilter();
    }

    /**
     * @since 1.3.0
     */
    @Bean
    public StormpathWrapperFilter stormpathWrapperFilter() {
        return super.stormpathWrapperFilter();
    }

}
