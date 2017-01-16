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
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.3.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@Conditional(StormpathSpringSecurityOrSecurityBasicDisabled.class)
public class StormpathWebSecurityDisabledConfiguration extends AbstractStormpathWebSecurityDisabledConfiguration {

    @Bean
    @Conditional(SecurityBasicEnabled.class) //we only need our Disabled Configurer Adapter if Spring Security is enabled
    public SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter() {
        //This bean will only be created if Spring Security integration is disabled but Spring Security is enabled
        return super.stormpathSecurityConfigurerAdapter();
    }

    @Bean
    @Conditional(SecurityBasicEnabled.class) //we only need our Disabled Configurer Adapter if Spring Security is enabled
    public LogoutHandler stormpathLogoutHandler() {
        //This bean will only be created if Spring Security integration is disabled but Spring Security is enabled
        return super.stormpathLogoutHandler();
    }
}
