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

import com.stormpath.sdk.servlet.filter.StormpathFilter;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;

/**
 * @since 1.2.0
 */
public class StormpathMockMvcConfigurer extends MockMvcConfigurerAdapter {

    private Filter stormpathFilter;
    private Filter springSecurityFilter;

    @Override
    public RequestPostProcessor beforeMockMvcCreated(
            ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {

        String stormpathFilterBeanId = "stormpathFilter";
        String springSecurityFilterBeanId = BeanIds.SPRING_SECURITY_FILTER_CHAIN;
        if (this.springSecurityFilter == null && context.containsBean(springSecurityFilterBeanId)) {
            this.springSecurityFilter = context.getBean(springSecurityFilterBeanId, Filter.class);
        }

        if (this.stormpathFilter == null && context.containsBean(stormpathFilterBeanId)) {
            this.stormpathFilter = context.getBean(stormpathFilterBeanId, Filter.class);
        }

        if (this.springSecurityFilter == null) {
            throw new IllegalStateException(
                    "springSecurityFilter cannot be null. Ensure a Bean with the name "
                            + springSecurityFilterBeanId
                            + " implementing Filter is present or inject the Filter to be used.");
        }

        if (this.stormpathFilter == null) {
            throw new IllegalStateException(
                    "stormpathFilter cannot be null. Ensure a Bean with the name "
                            + stormpathFilterBeanId
                            + " implementing Filter is present or inject the Filter to be used.");
        }

        builder.addFilters(this.stormpathFilter);
        builder.addFilters(this.springSecurityFilter);
        context.getServletContext().setAttribute(StormpathFilter.class.getName(), this.stormpathFilter.getClass().getName());
        context.getServletContext().setAttribute(BeanIds.SPRING_SECURITY_FILTER_CHAIN, this.springSecurityFilter);

        return testSecurityContext();
    }

}
