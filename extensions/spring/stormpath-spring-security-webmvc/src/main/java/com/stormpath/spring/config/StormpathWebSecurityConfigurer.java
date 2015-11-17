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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

/**
* @since 1.0.RC5
*/
@Component
public class StormpathWebSecurityConfigurer {

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

    /**
     * The pre-defined Stormpath access control settings are defined here.
     *
     * @param http the {@link HttpSecurity} to be modified
     * @throws Exception if an error occurs
     */
    public final void configure(HttpSecurity http) throws Exception {

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

            //http.authenticationProvider(stormpathAuthenticationProvider);
        }
    }

    public void configure(WebSecurity web) throws Exception {
        if (stormpathWebEnabled) {
            web.ignoring()
                .antMatchers("/assets/css/stormpath.css")
                .antMatchers("/assets/css/custom.stormpath.css");
        }
    }

    /**
     * Method to specify the {@link AuthenticationProvider} that Spring Security will use when processing authentications.
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception if an error occurs
     */
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (stormpathWebEnabled && stormpathSecuritybEnabled) {
            auth.authenticationProvider(stormpathAuthenticationProvider);
        }
    }
}
