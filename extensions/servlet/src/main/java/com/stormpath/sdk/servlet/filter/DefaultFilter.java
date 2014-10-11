/*
 * Copyright 2014 Stormpath, Inc.
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

import com.stormpath.sdk.lang.Classes;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enum representing all of the default Stormpath Filter instances available to web applications.
 *
 * @since 1.0
 */
public enum DefaultFilter {

    login(LoginFilter.class),
    logout(LogoutFilter.class),
    register(RegisterFilter.class),
    verify(VerifyFilter.class);

    private final Class<? extends Filter> filterClass;

    private DefaultFilter(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public Filter newInstance() {
        return Classes.newInstance(this.filterClass);
    }

    public Class<? extends Filter> getFilterClass() {
        return this.filterClass;
    }

    public static Map<String, Filter> createInstanceMap(final ServletContext servletContext) throws ServletException {

        Map<String, Filter> filters = new LinkedHashMap<String, Filter>(values().length);

        for (final DefaultFilter defaultFilter : values()) {

            final String name = defaultFilter.name();

            final Filter filter = defaultFilter.newInstance();

            FilterConfig config = new FilterConfig() {
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
                    return null;
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return new Enumeration<String>() {
                        @Override
                        public boolean hasMoreElements() {
                            return false;
                        }

                        @Override
                        public String nextElement() {
                            return null;
                        }
                    };
                }
            };

            try {
                filter.init(config);
            } catch (ServletException e) {
                String msg = "Unable to initialize default filter '" + name + "': " + e.getMessage();
                throw new ServletException(msg, e);
            }

            filters.put(defaultFilter.name(), filter);

        }
        return filters;
    }
}
