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
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.0.RC4.3
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableStormpathWebMvc
@Configuration
public class StormpathWebSecurityConfiguration extends AbstractStormpathWebSecurityConfiguration {

    //Ordinarily we shouldn't inject an entire configuration and only @Autowire the things in the config we
    //we need, but for some reason @Autowiring the 'stormpathInternalConfig' bean here causes application context
    //startup to fail.
    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Bean
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return super.stormpathAuthenticationSuccessHandler();
    }

    @Bean
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * Instead, users can extend this class and configure their applications by overriding the {@link #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     * config(HttpSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http the {@link HttpSecurity} to be modified
     * @throws Exception if an error occurs
     * @see #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {

        if (stormpathWebMvcConfiguration.loginEnabled) {
            http
                    .formLogin()
                    .loginPage(stormpathWebMvcConfiguration.loginUri)
                    .defaultSuccessUrl(stormpathWebMvcConfiguration.loginNextUri)
                    .successHandler(stormpathAuthenticationSuccessHandler())
                    .usernameParameter("login")
                    .passwordParameter("password");
        }

        if (stormpathWebMvcConfiguration.logoutEnabled) {
            http
                    .logout()
                    .logoutUrl(stormpathWebMvcConfiguration.logoutUri)
                    .logoutSuccessUrl(stormpathWebMvcConfiguration.logoutNextUri)
                    .addLogoutHandler(stormpathLogoutHandler());

        }

        http.httpBasic().and().

        //TODO: figure out what to do here:
        csrf().disable();

        doConfigure(http);
    }

    /**
     * Override this method to define app-specific security settings like:
     * <p>
     * <pre>
     * http
     *   .authorizeRequests()
     *   .antMatchers("/account").fullyAuthenticated()
     *   .antMatchers("/admin").hasRole("ADMIN");
     * </pre>
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    protected void doConfigure(HttpSecurity http) throws Exception {
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
