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
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;


/**
 * @since 1.0.RC4.6
 */
public abstract class AbstractStormpathSpringSecurityWebConfiguration extends AbstractStormpathConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AbstractStormpathSpringSecurityWebConfiguration.class);

    @Autowired
    private Client client;

    @Autowired
    private Application application;

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

        StormpathAuthenticationProvider provider = new StormpathAuthenticationProvider();

        provider.setClient(client);
        provider.setApplicationRestUrl(application.getHref());

        provider.setGroupGrantedAuthorityResolver(stormpathGroupGrantedAuthorityResolver());
        provider.setGroupPermissionResolver(stormpathGroupPermissionResolver());
        provider.setAccountGrantedAuthorityResolver(stormpathAccountGrantedAuthorityResolver());
        provider.setAccountPermissionResolver(stormpathAccountPermissionResolver());
        provider.setAuthenticationTokenFactory(stormpathAuthenticationTokenFactory());

        return provider;
    }

}
