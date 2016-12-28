/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.spring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath


/**
 * @since 1.3.0
 */
@Configuration
@EnableStormpathWebSecurity
@PropertySource("classpath:com/stormpath/spring/config/loginAndLogoutHandlers.properties")
class LoginAndLogoutHandlersConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(stormpath());
    }

    @Bean
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler stormpathAuthenticationFailureHandler() {
        return new CustomLoginFailureHandler();
    }

    @Bean
    public LogoutHandler stormpathLogoutHandler() {
        return new CustomLogoutHandler();
    }

    class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

        public boolean invoked = false

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
            invoked = true
        }
    }

    class CustomLoginFailureHandler implements AuthenticationFailureHandler {

        public boolean invoked = false

        @Override
        void onAuthenticationFailure(HttpServletRequest request,
                                     HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            invoked = true
        }
    }

    class CustomLogoutHandler implements LogoutHandler {

        public boolean invoked = false

        @Override
        void logout(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) {
            invoked = true
        }
    }
}
