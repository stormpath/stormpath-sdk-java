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

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
* @since 1.0.RC5
*/
@Configuration
@EnableStormpathWebMvc
@EnableStormpathSecurity
@EnableWebSecurity
public class StormpathWebSecurityConfiguration extends AbstractStormpathWebSecurityConfiguration {

    @Bean
    @Conditional(StormpathWebEnabled.class)
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return super.stormpathAuthenticationSuccessHandler();
    }

    @Bean
    @Conditional(StormpathWebEnabled.class)
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Conditional(StormpathWebEnabled.class)
    public CsrfTokenRepository stormpathCsrfTokenRepository() {
        return super.stormpathCsrfTokenRepository();
    }

    @Bean
    @Conditional(StormpathWebEnabled.class)
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * Instead, users can extend this class and configure their applications by overriding the {@link #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     * config(HttpSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     * @see #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        configure(http, stormpathAuthenticationSuccessHandler(), stormpathLogoutHandler());
        doConfigure(http);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(HttpSecurity)} after
     * auto-configuring all the required properties. You can override this method to define app-specific security settings like:
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
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code> thus removing its required configuration.
     * Instead, users can configure the <code>AuthenticationManagerBuilder</code> by overriding the {@link #doConfigure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder) config(AuthenticationManagerBuilder)} method.
     * This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     *             if an error occurs
     */
    @Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        configure(auth, super.stormpathAuthenticationProvider);
        doConfigure(auth);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(AuthenticationManagerBuilder)} after
     * auto-configuring all the required properties. You can override this method to define app-specific.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {
    }

}
