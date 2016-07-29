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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class DefaultFilterBuilder implements FilterBuilder {

    private static final String PATH_CONFIG_INIT_PARAM_NAME = "pathConfig";

    private Class<? extends Filter> filterClass;
    private Filter instance;
    private ServletContext servletContext;
    private String name;
    private final Map<String,String> initParams;

    public DefaultFilterBuilder() {
        this.initParams = new LinkedHashMap<>();
    }

    @Override
    public FilterBuilder setFilterClass(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
        return this;
    }

    @Override
    public FilterBuilder setFilter(Filter filter) {
        this.instance = filter;
        return this;
    }

    @Override
    public FilterBuilder setServletContext(ServletContext sc) {
        this.servletContext = sc;
        return this;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public FilterBuilder setName(String name) {
        this.name = Strings.clean(name);
        return this;
    }

    @Override
    public FilterBuilder setInitParam(String name, String value) {
        name = Strings.clean(name);
        Assert.notNull(name, "name argument cannot be null or empty.");
        value = Strings.clean(value);

        if (value == null) {
            this.initParams.remove(name);
        } else {
            this.initParams.put(name, value);
        }

        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public FilterBuilder setPathConfig(String pathConfig) {
        setInitParam(PATH_CONFIG_INIT_PARAM_NAME, pathConfig);
        return this;
    }

    @Override
    public String getPathConfig() {
        return this.initParams.get(PATH_CONFIG_INIT_PARAM_NAME);
    }

    @Override
    public Filter build() throws ServletException {

        Assert.isTrue(this.instance != null || this.filterClass != null,
            "Either a filter instance or a filterClass must be specified.");

        Assert.notNull(this.servletContext, "servletContext must be specified.");
        Assert.notNull(this.name, "A non-null/non-empty name must be specified.");

        Filter filter = instance != null ? instance : Classes.newInstance(this.filterClass);
        FilterConfig filterConfig = new DefaultFilterConfig(this.servletContext, this.name, this.initParams);
        filter.init(filterConfig);
        return filter;
    }
}
