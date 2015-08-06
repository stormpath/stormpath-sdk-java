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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @since 1.0.RC4.6
 */
@Configuration
@EnableAutoConfiguration
public class SpringSecurityBootTestApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityBootTestApplication.class);

    @Autowired
    private Client client;

    @Bean
    public Application stormpathApplication() {

        //purposefully return the Stormpath admin console app.
        //Real apps would never do this, but this is an easy way to test that bean overrides work:

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //return the admin app
                return app;
            }
        }

        throw new IllegalStateException("Stormpath application is always available in Stormpath.");
    }

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(SpringSecurityBootTestApplication.class, args);

        log.info("Beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }
    }

}
