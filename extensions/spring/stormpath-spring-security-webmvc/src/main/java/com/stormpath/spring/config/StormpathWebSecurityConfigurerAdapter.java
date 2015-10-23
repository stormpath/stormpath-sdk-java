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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @since 1.0.RC5
 */
@Configuration
public class StormpathWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    protected StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer;

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing
     * all the required configurations. Instead, users can extend this class and configure their applications by overriding
     * the {@link #doConfigure(HttpSecurity)} method. This way the configuration can be explicitly modified but not overwritten
     * by mistake.</p>
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     * @see #doConfigure(HttpSecurity)
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        stormpathWebSecurityConfigurer.configure(http);
        doConfigure(http);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked
     * by {@link #configure(HttpSecurity)} after configuring all the properties required by Stormpath. You can override
     * this method to define app-specific security settings like:
     *
     * <pre>
     * http
     *   .authorizeRequests()
     *   .antMatchers("/account").fullyAuthenticated()
     *   .antMatchers("/admin").hasRole("ADMIN");
     * </pre>
     *
     * @param http
     *            the {@link HttpSecurity} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(HttpSecurity http) throws Exception {

    }

    /**
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code>
     * thus removing its required configuration. Instead, users can configure the <code>AuthenticationManagerBuilder</code>
     * by overriding the {@link #doConfigure(AuthenticationManagerBuilder)} method. This way the configuration can be
     * explicitly modified but not overwritten by mistake.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     *             if an error occurs
     */
    @Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        stormpathWebSecurityConfigurer.configure(auth);
        doConfigure(auth);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked
     * by {@link #configure(AuthenticationManagerBuilder)} after configuring all the properties required by Stormpath. You can
     * override this method to define app-specific ones.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {

    }

    /**
     * This method has been marked as final in order to be sure that the required Stormpath configuration is actually applied
     * and not mistakenly overwritten by sub-classes. Instead, users can configure the <code>WebSecurity</code> by overriding
     * the {@link #doConfigure(WebSecurity)} method. This way the configuration can be explicitly modified but not
     * overwritten by mistake.
     *
     * @param web
     *            the {@link WebSecurity} to use
     * @throws Exception
     *             if an error occurs
     */
    @Override
    public final void configure(WebSecurity web) throws Exception {
        stormpathWebSecurityConfigurer.configure(web);
        doConfigure(web);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked
     * by {@link #configure(WebSecurity)} after configuring all the properties required by Stormpath. You can override
     * this method to define app-specific ones.
     *
     * @param web
     *            the {@link WebSecurity} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(WebSecurity web) throws Exception {

    }

    @Bean //required in the Spring case
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
