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
package com.stormpath.tutorial;

import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.tutorial.config.SpringSecurityWebAppConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

/**
 * @since 1.3.0
 */
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SpringSecurityWebAppConfig.class);
        sc.addListener(new ContextLoaderListener(context));

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic dispatcher = sc.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        //Spring Security Filter: in front of Stormpath
        FilterRegistration.Dynamic securityFilter = sc.addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, DelegatingFilterProxy.class);
        securityFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        //Stormpath Filter (after Spring Security)
        FilterRegistration.Dynamic stormpathFilter = sc.addFilter(StormpathFilter.DEFAULT_FILTER_NAME, DelegatingFilterProxy.class);
        EnumSet<DispatcherType> types =
                EnumSet.of(DispatcherType.ERROR, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST);
        stormpathFilter.addMappingForUrlPatterns(types, false, "/*");
    }
}
