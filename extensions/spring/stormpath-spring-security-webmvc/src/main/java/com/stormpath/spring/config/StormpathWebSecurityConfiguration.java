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
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter;
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * @since 1.0.RC5
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@EnableStormpathWebMvc
@EnableStormpathSecurity
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
    public StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer() {
        return super.stormpathWebSecurityConfigurer();
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
    public OAuthAuthenticationSpringSecurityProcessingFilter oAuthAuthenticationProcessingFilter() {
        return super.oAuthAuthenticationProcessingFilter();
    }

    @Bean
    @Override
    public SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter() {
        return super.springSecurityResolvedAccountFilter();
    }

    @Bean
    @Override
    public AuthenticationEntryPoint stormpathAuthenticationEntryPoint() {
        return super.stormpathAuthenticationEntryPoint();
    }

}
