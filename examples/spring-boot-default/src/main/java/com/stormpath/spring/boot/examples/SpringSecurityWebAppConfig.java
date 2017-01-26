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
package com.stormpath.spring.boot.examples;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC6
 */
@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Access to all paths is restricted by default.
        // We want to restrict access to one path and leave all other paths open.
        // Starting with Spring Security 4.2 we do not need to explicitly apply the Stormpath configuration in Spring Boot
        // any more (note that it is still required in regular Spring)
        http
            .authorizeRequests()
            .antMatchers("/restricted").fullyAuthenticated()
            .antMatchers("/**").permitAll();
    }

    @Bean
    public WebHandler loginPreHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                System.out.print("AAAAAAA pre");
                return true;
            }
        };
    }

    @Bean
    public WebHandler loginPostHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                System.out.print("AAAAAAA post");
                return true;
            }
        };
    }
}