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
package com.stormpath.spring.security.configuration;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.lang.UnknownClassException;
import com.stormpath.spring.config.AbstractStormpathConfiguration;
import com.stormpath.spring.security.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


/**
 * @since 1.0.RC4.4
 */
public abstract class AbstractStormpathSpringSecurityConfiguration extends AbstractStormpathConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AbstractStormpathSpringSecurityConfiguration.class);

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

    @Autowired(required = false)
    protected GroupGrantedAuthorityResolver groupGrantedAuthorityResolver;

    @Autowired(required = false)
    protected GroupPermissionResolver groupPermissionResolver;

    @Autowired(required = false)
    protected AccountGrantedAuthorityResolver accountGrantedAuthorityResolver;

    @Autowired(required = false)
    protected AccountPermissionResolver accountPermissionResolver;

    @Autowired(required = false)
    protected AuthenticationTokenFactory authenticationTokenFactory;


    public StormpathAuthenticationProvider stormpathAuthenticationProvider() {
        StormpathAuthenticationProvider stormpathAuthenticationProvider = new StormpathAuthenticationProvider();
        stormpathAuthenticationProvider.setClient(stormpathClient());
        stormpathAuthenticationProvider.setApplicationRestUrl(stormpathApplication().getHref());

        //The resolver bean takes precedence over the one configured in the properties file
        GroupGrantedAuthorityResolver groupGrantedAuthorityResolver = this.groupGrantedAuthorityResolver != null ? this.groupGrantedAuthorityResolver : groupGrantedAuthorityResolver();
        if (groupGrantedAuthorityResolver != null) {
            stormpathAuthenticationProvider.setGroupGrantedAuthorityResolver(groupGrantedAuthorityResolver);
        }
        //The resolver bean takes precedence over the one configured in the properties file
        GroupPermissionResolver groupPermissionResolver = this.groupPermissionResolver != null ? this.groupPermissionResolver : groupPermissionResolver();
        if (groupPermissionResolver != null) {
            stormpathAuthenticationProvider.setGroupPermissionResolver(groupPermissionResolver);
        }
        //The resolver bean takes precedence over the one configured in the properties file
        AccountGrantedAuthorityResolver accountGrantedAuthorityResolver = this.accountGrantedAuthorityResolver != null ? this.accountGrantedAuthorityResolver : accountGrantedAuthorityResolver();
        if (accountGrantedAuthorityResolver != null) {
            stormpathAuthenticationProvider.setAccountGrantedAuthorityResolver(accountGrantedAuthorityResolver);
        }
        //The resolver bean takes precedence over the one configured in the properties file
        AccountPermissionResolver accountPermissionResolver = this.accountPermissionResolver != null ? this.accountPermissionResolver : accountPermissionResolver();
        if (accountPermissionResolver != null) {
            stormpathAuthenticationProvider.setAccountPermissionResolver(accountPermissionResolver);
        }
        //The factory bean takes precedence over the one configured in the properties file
        AuthenticationTokenFactory authenticationTokenFactory = this.authenticationTokenFactory != null ? this.authenticationTokenFactory : authenticationTokenFactory();
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
