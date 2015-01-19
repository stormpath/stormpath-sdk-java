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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class DefaultFilterConfig implements FilterConfig {

    private final ServletContext servletContext;
    private final String name;
    private final Map<String, String> initParams;

    public DefaultFilterConfig(ServletContext servletContext, String name, Map<String, String> initParams) {
        Assert.notNull(servletContext, "servletContext is required.");
        Assert.hasText(name, "name is required and cannot be null or empty.");
        this.servletContext = servletContext;
        this.name = name;
        this.initParams = initParams != null ? java.util.Collections.unmodifiableMap(initParams) :
                          java.util.Collections.<String, String>emptyMap();
    }

    @Override
    public String getFilterName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        Set<String> names = initParams.keySet();
        return java.util.Collections.enumeration(names);
    }
}
