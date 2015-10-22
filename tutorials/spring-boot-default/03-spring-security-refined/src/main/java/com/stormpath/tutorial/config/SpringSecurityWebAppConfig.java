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
package com.stormpath.tutorial.config;

import com.stormpath.spring.boot.autoconfigure.StormpathWebSecurityConfigurer;
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
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        stormpathWebSecurityConfigurer.configure(http);
        http
            .authorizeRequests()
            .antMatchers("/restricted").fullyAuthenticated();
    }

    @Override
    public final void configure(AuthenticationManagerBuilder auth) throws Exception {
        stormpathWebSecurityConfigurer.configure(auth);
    }

    @Override
    public final void configure(WebSecurity web) throws Exception {
        stormpathWebSecurityConfigurer.configure(web);
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

}