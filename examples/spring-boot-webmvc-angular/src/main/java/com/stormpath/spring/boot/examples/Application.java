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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Bean
    public WebHandler registerPreHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                log.debug("----> PreRegisterHandler");
                return true;
            }
        };
    }

    @Bean
    public WebHandler registerPostHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                log.debug("----> PostRegisterHandler");
                return true;
            }
        };
    }

    @Bean
    public WebHandler loginPreHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                log.debug("----> PreLoginHandler");
                return true;
            }
        };
    }

    @Bean
    public WebHandler loginPostHandler() {
        return new WebHandler() {
            @Override
            public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
                log.debug("----> PostLoginHandler");
                return true;
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
