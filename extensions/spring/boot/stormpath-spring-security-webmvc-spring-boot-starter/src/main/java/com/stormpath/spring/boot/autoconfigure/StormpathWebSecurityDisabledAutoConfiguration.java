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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.spring.config.AbstractStormpathWebSecurityDisabledConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * @since 1.3.0
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled"}, matchIfMissing = true)
@ConditionalOnClass({Servlet.class, Filter.class, DispatcherServlet.class})
@ConditionalOnWebApplication
@AutoConfigureAfter(StormpathWebSecurityAutoConfiguration.class)
public class StormpathWebSecurityDisabledAutoConfiguration extends AbstractStormpathWebSecurityDisabledConfiguration {

    @Bean
    @ConditionalOnMissingBean(name="stormpathSecurityConfigurerAdapter")
    @ConditionalOnProperty(name = "stormpath.spring.security.enabled", havingValue = "false")
    public SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter() {
        //This bean will only be created if `stormpath.spring.security.enabled` is false
        return super.stormpathSecurityConfigurerAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLogoutHandler")
    @ConditionalOnProperty(name = "stormpath.spring.security.enabled", havingValue = "false")
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

}
