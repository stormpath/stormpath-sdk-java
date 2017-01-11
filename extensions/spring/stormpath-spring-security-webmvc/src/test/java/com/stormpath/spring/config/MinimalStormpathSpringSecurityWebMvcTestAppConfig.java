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

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

/**
 * @since 1.0.RC5
 */
@Configuration
@EnableStormpathWebSecurity
public class MinimalStormpathSpringSecurityWebMvcTestAppConfig  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(stormpath());
    }

    /** @since 1.3.0 */
    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessEventListener() {
        return new CustomAuthenticationSuccessEventListener();
    }

    /** @since 1.3.0 */
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher() {
        return new DefaultAuthenticationEventPublisher();
    }

    /** @since 1.3.0 */
    static class CustomAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

        static boolean eventWasTriggered = false;

        @Override
        public void onApplicationEvent(AuthenticationSuccessEvent event) {
            eventWasTriggered = true;
        }
    }
}
