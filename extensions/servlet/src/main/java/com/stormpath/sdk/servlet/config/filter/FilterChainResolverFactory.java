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

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.PathMatchingFilterChainResolver;
import com.stormpath.sdk.servlet.filter.PrioritizedFilterChainResolver;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0
 */
public class FilterChainResolverFactory extends ConfigSingletonFactory<FilterChainResolver> {

    @Override
    protected FilterChainResolver createInstance(ServletContext servletContext) throws Exception {

        PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver(servletContext);
        FilterChainManager mgr = getConfig().getFilterChainManager();
        resolver.setFilterChainManager(mgr);

        //ensure the always-on AccountResolverFilter is available:
        AccountResolverFilterFactory factory = new AccountResolverFilterFactory();
        factory.init(servletContext);
        Filter accountFilter = factory.getInstance();

        final List<Filter> priorityFilters = new ArrayList<>();
        priorityFilters.add(accountFilter);

        if (getConfig().isCorsEnabled()) {
            CORSFilterFactory corsFilterFactory = new CORSFilterFactory();
            corsFilterFactory.init(servletContext);
            priorityFilters.add(corsFilterFactory.getInstance());
        }

        //we always want our immediateExecutionFilters to run before the default chain:
        return new PrioritizedFilterChainResolver(resolver, priorityFilters);
    }
}
