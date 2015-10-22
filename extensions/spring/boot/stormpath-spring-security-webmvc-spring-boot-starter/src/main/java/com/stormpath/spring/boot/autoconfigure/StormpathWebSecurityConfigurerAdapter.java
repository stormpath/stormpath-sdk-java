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

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.spring.config.AbstractStormpathWebSecurityConfigurerAdapter;
import com.stormpath.spring.config.StormpathSecurityEnabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

/**
 * @since 1.0.RC5
 */
@Component
public class StormpathWebSecurityConfigurerAdapter extends AbstractStormpathWebSecurityConfigurerAdapter {

    @Autowired
    protected AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler;

    @Autowired
    public LogoutHandler stormpathLogoutHandler;

    @Autowired
    public AuthenticationManager getAuthenticationManager;

    @Autowired
    public CsrfTokenRepository stormpathCsrfTokenRepository;

    @Autowired
    public CsrfTokenManager stormpathCsrfTokenManager;

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     */

    public final void configure(HttpSecurity http) throws Exception {
        configure(http, stormpathAuthenticationSuccessHandler, stormpathLogoutHandler);
    }

    /**
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code> thus removing its required configuration.
     * This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     *             if an error occurs
     */

    public final void configure(AuthenticationManagerBuilder auth) throws Exception {
        configure(auth, super.stormpathAuthenticationProvider);
    }

    /**
     * This method has been marked as final in order to be sure that the required Stormpath configuration is actually applied and not mistakenly overwritten by sub-classes.
     * Instead, users can configure the <code>WebSecurity</code> by overriding the {@link #doConfigure(WebSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param web
     *            the {@link WebSecurity} to use
     * @throws Exception
     *             if an error occurs
     */

    public final void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

}

