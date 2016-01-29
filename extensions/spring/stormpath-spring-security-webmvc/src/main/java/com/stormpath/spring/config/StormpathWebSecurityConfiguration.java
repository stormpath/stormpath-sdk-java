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
import com.stormpath.sdk.servlet.filter.DefaultFilterBuilder;
import com.stormpath.sdk.servlet.filter.FilterBuilder;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.spring.oauth.OAuth2AuthenticationProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @since 1.0.RC5
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@EnableStormpathWebMvc
@EnableStormpathSecurity
public class StormpathWebSecurityConfiguration extends AbstractStormpathWebSecurityConfiguration implements ServletContextAware {

    private ServletContext servletContext;

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
    public OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter() throws ServletException {

        FilterBuilder builder = new DefaultFilterBuilder().setFilterClass(
                OAuth2AuthenticationProcessingFilter.class) //suppress config logic since Spring is used for config here
                .setServletContext(servletContext).setName("oauth2AuthenticationProcessingFilter");

        OAuth2AuthenticationProcessingFilter filter = (OAuth2AuthenticationProcessingFilter) builder.build();
        filter.setEnabled(csrfTokenEnabled);

        return filter;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
