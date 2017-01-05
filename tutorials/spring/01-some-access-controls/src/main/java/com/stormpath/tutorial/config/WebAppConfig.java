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
package com.stormpath.tutorial.config;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.config.EnableStormpath;
import com.stormpath.spring.config.EnableStormpathWebMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @since 1.3.0
 */
@Configuration
@EnableWebMvc
@EnableStormpath //Stormpath base beans
@EnableStormpathWebMvc //Stormpath web mvc beans plus out-of-the-box views
@ComponentScan("com.stormpath.tutorial")
@PropertySource("classpath:application.properties")
public class WebAppConfig {

    @Autowired
    private Application stormpathApplication; //the REST resource in Stormpath that represents this app

    @Autowired
    private Client client; //can be used to interact with all things in your Stormpath tenant

    /**
     * This bean and the @PropertySource annotation above allow you to configure Stormpath beans with properties
     * prefixed with {@code stormpath.}, i.e. {@code stormpath.application.href}, {@code stormpath.client.apiKey.file}, etc.
     *
     * <p>Combine this with Spring's Profile support to override property values based on specific runtime environments,
     * e.g. development, production, etc.</p>
     *
     * @return the application's PropertySourcesPlaceholderConfigurer that enables property placeholder substitution.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
