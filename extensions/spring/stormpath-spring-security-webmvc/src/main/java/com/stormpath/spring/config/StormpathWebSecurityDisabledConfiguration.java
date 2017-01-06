/*
 * Copyright 2017 Stormpath, Inc.
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
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.3.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@Order(100) //Must come after StormpathWebSecurityConfiguration
public class StormpathWebSecurityDisabledConfiguration extends AbstractStormpathWebSecurityDisabledConfiguration {

    @Bean
    public SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter() {
        //This bean will only be created if `stormpath.spring.security.enabled` is false
        return super.stormpathSecurityConfigurerAdapter();
    }

    @Bean
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }
}
