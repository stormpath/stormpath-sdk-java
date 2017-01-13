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
package com.stormpath.spring.config;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.servlet.ServletContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class SecurityMockMvcLoginRequestBuilder {

    public static FormLoginRequestBuilder formLogin() {
        return new FormLoginRequestBuilder();
    }

    static final class FormLoginRequestBuilder implements RequestBuilder {
        private String username = "user";
        private String login = "null";
        private String password = "password";
        private RequestPostProcessor postProcessor = csrf();

        public MockHttpServletRequest buildRequest(ServletContext servletContext) {
            MockHttpServletRequestBuilder builder = post("/login")
                .param("password", password)
                .accept(MediaType.APPLICATION_FORM_URLENCODED);
            if (username == null) {
                builder.param("login", login);
            } else {
                builder.param("username", username);
            }
            MockHttpServletRequest request = builder.buildRequest(servletContext);
            return postProcessor.postProcessRequest(request);
        }

        public FormLoginRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        //username and login are mutually exclusive. When Stormpath is in place the param name is 'login', but in SpringSec it is called 'username' by default
        public FormLoginRequestBuilder username(String username) {
            this.login = null;
            this.username = username;
            return this;
        }

        //username and login are mutually exclusive. When Stormpath is in place the param name is 'login', but in SpringSec it is called 'username' by default
        public FormLoginRequestBuilder login(String login) {
            this.username = null;
            this.login = login;
            return this;
        }

        private FormLoginRequestBuilder() {
        }
    }
}
