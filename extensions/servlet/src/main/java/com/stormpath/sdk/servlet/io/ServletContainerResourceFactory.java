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
package com.stormpath.sdk.servlet.io;

import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.FileResource;
import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.impl.io.UrlResource;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public class ServletContainerResourceFactory implements ResourceFactory {

    private final ServletContext servletContext;

    public ServletContainerResourceFactory(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext cannot be null.");
        this.servletContext = servletContext;
    }

    @Override

    public Resource createResource(String location) {
        Assert.hasText(location, "location argument cannot be null or empty.");

        if (location.startsWith(ClasspathResource.SCHEME_PREFIX)) {
            return new ClasspathResource(location);
        }

        String lcase = location.toLowerCase();
        if (location.startsWith(UrlResource.SCHEME_PREFIX) || lcase.startsWith("http:") || lcase.startsWith("https:")) {
            return new UrlResource(location);
        }

        if (location.startsWith(FileResource.SCHEME_PREFIX)) {
            return new FileResource(location);
        }

        //otherwise assume a servlet context resource:
        return new ServletContextResource(location, servletContext);
    }
}
