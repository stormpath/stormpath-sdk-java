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

import com.stormpath.spring.config.AbstractStormpathSpringSecurityConfiguration;
import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.Authentication;

/**
 * @since 1.0.RC5
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = { "stormpath.enabled", "stormpath.spring.security.enabled" }, matchIfMissing = true)
@AutoConfigureAfter({ StormpathAutoConfiguration.class, SecurityAutoConfiguration.class })
public class StormpathSpringSecurityAutoConfiguration extends AbstractStormpathSpringSecurityConfiguration {

    @Bean
    @Override
    @ConditionalOnMissingBean
    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return super.stormpathGroupGrantedAuthorityResolver();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean
    public GroupPermissionResolver stormpathGroupPermissionResolver() {
        return super.stormpathGroupPermissionResolver();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean
    public AccountGrantedAuthorityResolver stormpathAccountGrantedAuthorityResolver() {
        return super.stormpathAccountGrantedAuthorityResolver();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean
    public AccountPermissionResolver stormpathAccountPermissionResolver() {
        return super.stormpathAccountPermissionResolver();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean
    public AuthenticationTokenFactory stormpathAuthenticationTokenFactory() {
        return super.stormpathAuthenticationTokenFactory();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean
    public AuthenticationProvider stormpathAuthenticationProvider() {
        return super.stormpathAuthenticationProvider();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(name = "stormpathAuthenticationManager")
    public AuthenticationManager stormpathAuthenticationManager() {
        return super.stormpathAuthenticationManager();
    }

    // This is only working as an inner static class
    // Need to find out if this is best practice
    @Order(99)
    @Configuration
    protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter {
        @Autowired
        StormpathSpringSecurityAutoConfiguration stormpathSpringSecurityAutoConfiguration;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(stormpathSpringSecurityAutoConfiguration.stormpathAuthenticationProvider());
        }
    }
}