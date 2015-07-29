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

import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * @since 1.0.RC4.3
 */
@EnableWebSecurity
@EnableWebMvcSecurity
@ComponentScan
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class StormpathWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    protected StormpathAuthenticationProvider stormpathAuthenticationProvider;

    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Autowired
    protected StormpathLoginSuccessHandler stormpathLoginSuccessHandler;

    @Autowired
    protected StormpathLogoutHandler stormpathLogoutHandler;

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * Instead, users can extend this class and configure their applications by overriding the {@link #config(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     * config(HttpSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     * @see #config(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage(stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginUrl())
                .defaultSuccessUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl())
                .successHandler(stormpathLoginSuccessHandler)
                .usernameParameter("login")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutUrl())
                .logoutSuccessUrl(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl())
                .addLogoutHandler(stormpathLogoutHandler)
                .and()
                .httpBasic()
                .and()
                .csrf().disable();

        config(http);
    }

    /**
     * Override this method to define app-specific security settings like:
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
    protected void config(HttpSecurity http) throws Exception {
    }

    /**
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code> thus removing all this required configuration.
     * Instead, users can configure the <code>AuthenticationManagerBuilder</code> by overriding the {@link #config(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder) config(AuthenticationManagerBuilder)} method.
     * This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     *             if an error occurs
     */
    @Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(stormpathAuthenticationProvider);
        config(auth);
    }

    protected void config(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
