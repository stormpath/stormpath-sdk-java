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

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.lang.UnknownClassException;
import com.stormpath.spring.security.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.0.RC4.3
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class StormpathSpringSecurityConfiguration extends AbstractStormpathConfiguration {

    private static final Logger log = LoggerFactory.getLogger(StormpathSpringSecurityConfiguration.class);

    @Value("#{ @environment['stormpath.spring.security.provider.groupGrantedAuthorityResolver'] }")
    protected String groupGrantedAuthorityResolverFQN;

    @Value("#{ @environment['stormpath.spring.security.provider.groupPermissionResolver'] }")
    protected String groupPermissionResolverFQN;

    @Value("#{ @environment['stormpath.spring.security.provider.accountGrantedAuthorityResolver'] }")
    protected String accountGrantedAuthorityResolverFQN;

    @Value("#{ @environment['stormpath.spring.security.provider.accountPermissionResolver'] }")
    protected String accountPermissionResolverFQN;

    @Value("#{ @environment['stormpath.spring.security.provider.authenticationTokenFactory'] }")
    protected String authenticationTokenFactoryFQN;

    protected GroupGrantedAuthorityResolver groupGrantedAuthorityResolver() {
        Object object = instantiate(groupGrantedAuthorityResolverFQN);
        if (object != null) {
            if (object instanceof GroupGrantedAuthorityResolver) {
                return (GroupGrantedAuthorityResolver) object;
            } else {
                String msg = object + " is not an instance of [" + GroupGrantedAuthorityResolver.class + "]";
                throw new IllegalStateException(msg);
            }
        }
        return null;
    }

    protected GroupPermissionResolver groupPermissionResolver() {
        Object object = instantiate(groupPermissionResolverFQN);
        if (object != null) {
            if (object instanceof GroupPermissionResolver) {
                return (GroupPermissionResolver) object;
            } else {
                String msg = object + " is not an instance of [" + GroupPermissionResolver.class + "]";
                throw new IllegalStateException(msg);
            }
        }
        return null;
    }

    protected AccountGrantedAuthorityResolver accountGrantedAuthorityResolver() {
        Object object = instantiate(accountGrantedAuthorityResolverFQN);
        if (object != null) {
            if (object instanceof AccountGrantedAuthorityResolver) {
                return (AccountGrantedAuthorityResolver) object;
            } else {
                String msg = object + " is not an instance of [" + AccountGrantedAuthorityResolver.class + "]";
                throw new IllegalStateException(msg);
            }
        }
        return null;
    }

    protected AccountPermissionResolver accountPermissionResolver() {
        Object object = instantiate(accountPermissionResolverFQN);
        if (object != null) {
            if (object instanceof AccountPermissionResolver) {
                return (AccountPermissionResolver) object;
            } else {
                String msg = object + " is not an instance of [" + AccountPermissionResolver.class + "]";
                throw new IllegalStateException(msg);
            }
        }
        return null;
    }

    protected AuthenticationTokenFactory authenticationTokenFactory() {
        Object object = instantiate(authenticationTokenFactoryFQN);
        if (object != null) {
            if (object instanceof AuthenticationTokenFactory) {
                return (AuthenticationTokenFactory) object;
            } else {
                String msg = object + " is not an instance of [" + AuthenticationTokenFactory.class + "]";
                throw new IllegalStateException(msg);
            }
        }
        return null;
    }

    @Bean
    public StormpathAuthenticationProvider stormpathAuthenticationProvider() {
        StormpathAuthenticationProvider stormpathAuthenticationProvider = new StormpathAuthenticationProvider();
        stormpathAuthenticationProvider.setClient(stormpathClient());
        stormpathAuthenticationProvider.setApplicationRestUrl(stormpathApplication().getHref());

        GroupGrantedAuthorityResolver groupGrantedAuthorityResolver = groupGrantedAuthorityResolver();
        GroupPermissionResolver groupPermissionResolver = groupPermissionResolver();
        AccountGrantedAuthorityResolver accountGrantedAuthorityResolver = accountGrantedAuthorityResolver();
        AccountPermissionResolver accountPermissionResolver = accountPermissionResolver();
        AuthenticationTokenFactory authenticationTokenFactory = authenticationTokenFactory();

        if (groupGrantedAuthorityResolver != null) {
            stormpathAuthenticationProvider.setGroupGrantedAuthorityResolver(groupGrantedAuthorityResolver);
        }
        if (groupPermissionResolver != null) {
            stormpathAuthenticationProvider.setGroupPermissionResolver(groupPermissionResolver);
        }
        if (accountGrantedAuthorityResolver != null) {
            stormpathAuthenticationProvider.setAccountGrantedAuthorityResolver(accountGrantedAuthorityResolver);
        }
        if (accountPermissionResolver != null) {
            stormpathAuthenticationProvider.setAccountPermissionResolver(accountPermissionResolver);
        }
        if (authenticationTokenFactory != null) {
            stormpathAuthenticationProvider.setAuthenticationTokenFactory(authenticationTokenFactory);
        }

        return  stormpathAuthenticationProvider;
    }

    private Object instantiate(String fullyQualifiedClassName) {
        String className = Strings.trimWhitespace(fullyQualifiedClassName);
        if (className != null) {
            try {
                return Classes.newInstance(className);
            } catch (UnknownClassException ex) {
                String msg = "Failed to instantiate class [" + className + "]";
                throw new IllegalStateException(msg, ex);
            }
        } else {
            return null;
        }
    }


}
