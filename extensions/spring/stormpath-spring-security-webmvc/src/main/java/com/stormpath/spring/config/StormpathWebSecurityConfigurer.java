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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * @since 1.0.RC5
 */
@Configuration
public class StormpathWebSecurityConfigurer extends AbstractHttpConfigurer<StormpathWebSecurityConfigurer, HttpSecurity> {

    private static final Logger log = LoggerFactory.getLogger(StormpathWebSecurityConfigurer.class);

    @Autowired(required = false) //when stormpath.enabled = false then this bean is not present but Spring Boot is still loading this class, so we need required = false here
    @Qualifier("stormpathSecurityConfigurerAdapter")
    protected SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter;

    @Value("#{ @environment['stormpath.enabled'] ?: true }")
    protected boolean stormpathEnabled;

    @Value("#{ @environment['stormpath.spring.security.autoload'] ?: true }")
    protected boolean stormpathSpringSecurityAutoload;

    /**
     * Extend WebSecurityConfigurerAdapter and configure the {@code HttpSecurity} object using
     * the {@link com.stormpath.spring.config.StormpathWebSecurityConfigurer#stormpath stormpath()} utility method.
     * For example:
     *
     * <pre>
     * import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;
     *
     * @Configuration
     * public class SecurityConfig extends WebSecurityConfigurerAdapter {
     *
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception {
     *        http
     *            .apply(stormpath())
     *            //other http config here
     *     }
     * }
     * </pre>
     *
     * @return the StormpathWebSecurityConfigurer object
     */
    public static AbstractHttpConfigurer<?, HttpSecurity> stormpath() {
        return new StormpathWebSecurityConfigurer();
    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     */
    @Override
    public void init(HttpSecurity http) throws Exception {
        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);

        if (stormpathEnabled && stormpathSpringSecurityAutoload) { /// we only need the configurer Stormpath is enabled
            stormpathSecurityConfigurerAdapter.init(http);
        }
    }

}
