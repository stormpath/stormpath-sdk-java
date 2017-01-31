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
package autoconfigure;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @since 1.0.RC5.2
 */
@Configuration
@EnableAutoConfiguration
public class StormpathWebSecurityAutoConfigurationTestApplication {

    private static final Logger log = LoggerFactory.getLogger(StormpathWebSecurityAutoConfigurationTestApplication.class);

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(StormpathWebSecurityAutoConfigurationTestApplication.class, args);

        log.info("Beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }
    }

    @Bean
    public WebHandler registerPreHandler() {
        return new CustomRegisterPreHandler();
    }

    @Bean
    public WebHandler registerPostHandler() {
        return new CustomRegisterPostHandler();
    }

    @Bean
    public WebHandler loginPreHandler() {
        return new CustomLoginPreHandler();
    }

    @Bean
    public WebHandler loginPostHandler() {
        return new CustomLoginPostHandler();
    }

    public class CustomRegisterPreHandler implements WebHandler {
        @Override
        public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
            return false;
        }
    }

    public class CustomRegisterPostHandler implements WebHandler {
        @Override
        public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
            return false;
        }
    }

    public class CustomLoginPreHandler implements WebHandler {
        @Override
        public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
            return false;
        }
    }

    public class CustomLoginPostHandler implements WebHandler {
        @Override
        public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
            return false;
        }
    }
}
