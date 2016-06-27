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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.spring.config.AbstractStormpathWebSecurityConfiguration;
import com.stormpath.spring.config.StormpathWebSecurityConfigurer;
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter;
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * @since 1.0.RC5
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled", "stormpath.spring.security.enabled"}, matchIfMissing = true)
@ConditionalOnClass({Servlet.class, Filter.class, DispatcherServlet.class})
@ConditionalOnWebApplication
@AutoConfigureBefore(StormpathWebMvcAutoConfiguration.class)
@AutoConfigureAfter(StormpathSpringSecurityAutoConfiguration.class)
public class StormpathWebSecurityAutoConfiguration extends AbstractStormpathWebSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationSuccessHandler")
    @Override
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return super.stormpathAuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationFailureHandler")
    @Override
    public AuthenticationFailureHandler stormpathAuthenticationFailureHandler() {
        return super.stormpathAuthenticationFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathWebSecurityConfigurer")
    public StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer() {
        return super.stormpathWebSecurityConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLogoutHandler")
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenRepository stormpathCsrfTokenRepository() {
        return super.stormpathCsrfTokenRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public ErrorModelFactory stormpathLoginErrorModelFactory() {
        return super.stormpathLoginErrorModelFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name="springSecurityIdSiteResultListener")
    @ConditionalOnProperty(name="stormpath.web.idSite.enabled")
    public IdSiteResultListener springSecurityIdSiteResultListener() {
        return super.springSecurityIdSiteResultListener();
    }

    @Bean
    @ConditionalOnMissingBean(name="springSecuritySamlResultListener")
    @ConditionalOnProperty(name="stormpath.web.callback.enabled")
    public SamlResultListener springSecuritySamlResultListener() {
        return super.springSecuritySamlResultListener();
    }

    @Bean
    @ConditionalOnMissingBean(name="oAuthAuthenticationProcessingFilter")
    public OAuthAuthenticationSpringSecurityProcessingFilter oAuthAuthenticationProcessingFilter() {
        return super.oAuthAuthenticationProcessingFilter();
    }

    @Bean
    @ConditionalOnMissingBean(name="springSecurityResolvedAccountFilter")
    public SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter() {
        return super.springSecurityResolvedAccountFilter();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationEntryPoint")
    public AuthenticationEntryPoint stormpathAuthenticationEntryPoint() {
        return super.stormpathAuthenticationEntryPoint();
    }

}
