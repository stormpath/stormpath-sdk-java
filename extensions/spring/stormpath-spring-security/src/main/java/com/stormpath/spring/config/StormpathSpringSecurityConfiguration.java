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
package com.stormpath.spring.config;

import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;

/**
 * @since 1.0.RC5
 */
@Configuration
@Conditional({ StormpathEnabled.class, StormpathSpringSecurityEnabled.class, SecurityBasicEnabled.class})
public class StormpathSpringSecurityConfiguration extends AbstractStormpathSpringSecurityConfiguration {

    @Bean
    @Override
    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return super.stormpathGroupGrantedAuthorityResolver();
    }

    @Bean
    @Override
    public GroupPermissionResolver stormpathGroupPermissionResolver() {
        return super.stormpathGroupPermissionResolver();
    }

    @Bean
    @Override
    public AccountGrantedAuthorityResolver stormpathAccountGrantedAuthorityResolver() {
        return super.stormpathAccountGrantedAuthorityResolver();
    }

    @Bean
    @Override
    public AccountPermissionResolver stormpathAccountPermissionResolver() {
        return super.stormpathAccountPermissionResolver();
    }

    @Bean
    @Override
    public AuthenticationTokenFactory stormpathAuthenticationTokenFactory() {
        return super.stormpathAuthenticationTokenFactory();
    }

    @Bean
    @Override
    public AuthenticationProvider stormpathAuthenticationProvider() {
        return super.stormpathAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager stormpathAuthenticationManager() {
        return super.stormpathAuthenticationManager();
    }

}
