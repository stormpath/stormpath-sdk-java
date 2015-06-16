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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * @since 1.0.RC4.4
 */
@EnableWebSecurity
@EnableWebMvcSecurity
@ComponentScan
@EnableStormpathSpringSecurityWebMvc //Stormpath Spring Security web mvc beans plus out-of-the-box views
@PropertySource("classpath:application.properties")
public class StormpathWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    protected StormpathAuthenticationProvider stormpathAuthenticationProvider;

    @Autowired
    protected StormpathLoginSuccessHandler stormpathLoginSuccessHandler;

    @Autowired
    protected StormpathLogoutHandler stormpathLogoutHandler;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/login?status=logout' }")
    protected String logoutNextUri;

    @Value("#{ @environment['stormpath.web.login.form.fields.login.placeholder'] ?: 'Username or Email' }")
    protected String loginFormUsernameParameter;

    @Value("#{ @environment['stormpath.web.login.form.fields.password.placeholder'] ?: 'password' }")
    protected String loginFormPasswordParameter;

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
                .loginPage(loginUri)
                .defaultSuccessUrl(loginNextUri)
                .successHandler(stormpathLoginSuccessHandler)
                .usernameParameter("login")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutUrl(logoutUri)
                .logoutSuccessUrl(logoutNextUri)
                .addLogoutHandler(stormpathLogoutHandler)
                .and()
                .httpBasic()
                .and()
                .csrf().disable();

        config(http);
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

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(AuthenticationManagerBuilder)} after
     * auto-configuring all the required properties. You can override this method to define app-specific.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void config(AuthenticationManagerBuilder auth) throws Exception {

    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}
