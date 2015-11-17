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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.http.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * @since 1.0.RC5
 */
@Configuration
@EnableStormpathWebSecurity
public class StormpathWebSecurityConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    protected Client client;

    @Autowired
    @Qualifier("stormpathLogoutHandler")
    protected LogoutHandler logoutHandler;

    @Autowired
    @Qualifier("stormpathAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Autowired
    @Qualifier("stormpathCsrfTokenRepository")
    protected CsrfTokenRepository csrfTokenRepository;

    @Autowired
    @Qualifier("stormpathCsrfTokenManager")
    protected CsrfTokenManager csrfTokenManager;

    @Autowired
    @Qualifier("stormpathAuthenticationProvider")
    protected AuthenticationProvider stormpathAuthenticationProvider; //provided by stormpath-spring-security

    @Autowired(required = false) //required = false when stormpath.web.enabled = false
    @Qualifier("stormpathAuthenticationResultSaver")
    protected Saver<AuthenticationResult> authenticationResultSaver; //provided by stormpath-spring-webmvc

    @Value("#{ @environment['stormpath.spring.security.enabled'] ?: true }")
    protected boolean stormpathSecuritybEnabled;

    @Value("#{ @environment['stormpath.web.enabled'] ?: true }")
    protected boolean stormpathWebEnabled;

    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    protected boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/login?status=logout' }")
    protected String logoutNextUri;

    @Value("#{ @environment['stormpath.web.forgot.enabled'] ?: true }")
    protected boolean forgotEnabled;

    @Value("#{ @environment['stormpath.web.forgot.nextUri'] ?: '/forgot' }")
    protected String forgotUri;

    @Value("#{ @environment['stormpath.web.change.enabled'] ?: true }")
    protected boolean changeEnabled;

    @Value("#{ @environment['stormpath.web.change.nextUri'] ?: '/change' }")
    protected String changeUri;

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.nextUri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.verify.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verify.nextUri'] ?: '/verify' }")
    protected String verifyUri;

    @Value("#{ @environment['stormpath.web.csrfProtection.enabled'] ?: true }")
    protected boolean csrfProtectionEnabled;

    @Value("#{ @environment['stormpath.spring.security.fullyAuthenticated.enabled'] ?: true }")
    protected boolean fullyAuthenticatedEnabled;

    public static StormpathWebSecurityConfigurerAdapter stormpathDSL() {
        return new StormpathWebSecurityConfigurerAdapter();
    }

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
    public void init(HttpSecurity http) throws Exception {

        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);

        if (stormpathWebEnabled) {
            if (loginEnabled) {
                http
                        .formLogin()
                        .loginPage(loginUri)
                        .defaultSuccessUrl(loginNextUri)
                        .successHandler(successHandler)
                        .usernameParameter("login")
                        .passwordParameter("password")
                        .and().authorizeRequests()
                        .antMatchers(loginUri).permitAll();
            }

            if (logoutEnabled) {
                http
                        .logout()
                        .invalidateHttpSession(true)
                        .logoutUrl(logoutUri)
                        .logoutSuccessUrl(logoutNextUri)
                        .addLogoutHandler(logoutHandler)
                        .and().authorizeRequests()
                        .antMatchers(logoutUri).permitAll();

            }

            if (!csrfProtectionEnabled) {
                http.csrf().disable();
            } else {
                //Let's configure HttpSessionCsrfTokenRepository to play nicely with our Controllers' forms
                http.csrf().csrfTokenRepository(csrfTokenRepository);
            }

            if (forgotEnabled) {
                http.authorizeRequests().antMatchers(forgotUri).permitAll();
            }
            if (changeEnabled) {
                http.authorizeRequests().antMatchers(changeUri).permitAll();
            }
            if (registerEnabled) {
                http.authorizeRequests().antMatchers(registerUri).permitAll();
            }
            if (verifyEnabled) {
                http.authorizeRequests().antMatchers(verifyUri).permitAll();
            }
        }
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
    @Deprecated
    protected void doConfigure(HttpSecurity http) throws Exception {

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
    @Deprecated
    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {

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
    @Deprecated
    protected void doConfigure(WebSecurity web) throws Exception {

    }

}
