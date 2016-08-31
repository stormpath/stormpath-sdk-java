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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.DefaultFilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class FilterChainManagerFactory extends ConfigSingletonFactory<FilterChainManager> {

    private static final Logger log = LoggerFactory.getLogger(FilterChainManagerFactory.class);
    public static final String FILTER_CONFIG_PREFIX = "stormpath.web.filters.";

    @Override
    protected FilterChainManager createInstance(ServletContext servletContext) throws Exception {

        Config config = getConfig();
        DefaultFilterChainManager mgr = new DefaultFilterChainManager(servletContext);

        if (config.isStormpathWebEnabled()) {
            //add the defaults:
            for (DefaultFilter defaultFilter : DefaultFilter.values()) {
                Object o = defaultFilter.getFactoryClass();
                if (o == null) {
                    o = defaultFilter.getFilterClass();
                }
                mgr.addFilter(defaultFilter.name(), o);
            }

            //pick up any user-configured filterish things as well as allow them to override the defaults:
            for (String key : config.keySet()) {

                if (key.startsWith(FILTER_CONFIG_PREFIX)) {

                    String instanceName = key.substring(FILTER_CONFIG_PREFIX.length());

                    //if there are any periods in the remainder, then the property is not a class name - it is an
                    // instance-specific config property, so just ignore it:
                    int i = instanceName.indexOf('.');
                    if (i >= 0) {
                        continue;
                    }

                    String className = config.get(key);
                    Object instance = Classes.newInstance(className);
                    mgr.addFilter(instanceName, instance);
                }
            }
        } else {
            log.warn("Stormpath web support disabled, filters not added.");
        }

        return new DefaultFilterChainManagerConfigurer(mgr, servletContext, config).configure();
    }
}
