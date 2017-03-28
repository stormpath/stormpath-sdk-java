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
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.provider.AccountCustomDataPermissionResolver;
import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.DefaultGroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.EmptyAccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupCustomDataPermissionResolver;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import com.stormpath.spring.security.provider.OktaAuthenticationProvider;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import com.stormpath.spring.security.provider.UsernamePasswordAuthenticationTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import java.util.Arrays;

/**
 * @since 1.0.RC5
 */
public abstract class AbstractStormpathSpringSecurityConfiguration {

    @Value("#{ @environment['okta.enabled'] ?: true }")
    protected boolean oktaEnabled;

    @Autowired
    private Application application;

    @Autowired
    private Client client;

    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return new DefaultGroupGrantedAuthorityResolver();
    }

    public GroupPermissionResolver stormpathGroupPermissionResolver() {
        return new GroupCustomDataPermissionResolver();
    }

    public AccountGrantedAuthorityResolver stormpathAccountGrantedAuthorityResolver() {
        return new EmptyAccountGrantedAuthorityResolver();
    }

    public AccountPermissionResolver stormpathAccountPermissionResolver() {
        return new AccountCustomDataPermissionResolver();
    }

    public AuthenticationTokenFactory stormpathAuthenticationTokenFactory() {
        return new UsernamePasswordAuthenticationTokenFactory();
    }

    public AuthenticationProvider stormpathAuthenticationProvider() {

        StormpathAuthenticationProvider provider;

        if (oktaEnabled) {
            provider = new OktaAuthenticationProvider(application, client);
        }
        else {
            provider = new StormpathAuthenticationProvider(application);
        }

        provider.setGroupGrantedAuthorityResolver(stormpathGroupGrantedAuthorityResolver());
        provider.setGroupPermissionResolver(stormpathGroupPermissionResolver());
        provider.setAccountGrantedAuthorityResolver(stormpathAccountGrantedAuthorityResolver());
        provider.setAccountPermissionResolver(stormpathAccountPermissionResolver());
        provider.setAuthenticationTokenFactory(stormpathAuthenticationTokenFactory());

        return provider;
    }

    public AuthenticationManager stormpathAuthenticationManager() {
        return new ProviderManager(Arrays.asList(stormpathAuthenticationProvider()));
    }
}
