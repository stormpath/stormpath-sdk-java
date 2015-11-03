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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @since 1.0.RC5
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class StormpathMethodSecurityConfiguration extends AbstractStormpathMethodSecurityConfiguration {

    @Bean
    @Override
    public PermissionEvaluator stormpathWildcardPermissionEvaluator() {
        return super.stormpathWildcardPermissionEvaluator();
    }

    @Bean
    @Override
    public MethodSecurityExpressionHandler stormpathMethodSecurityExpressionHandler() {
        return super.stormpathMethodSecurityExpressionHandler();
    }

}
