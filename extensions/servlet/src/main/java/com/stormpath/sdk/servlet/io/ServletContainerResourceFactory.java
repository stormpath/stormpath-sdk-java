package com.stormpath.sdk.servlet.io;

import com.stormpath.sdk.impl.io.ClasspathResource;
import com.stormpath.sdk.impl.io.FileResource;
import com.stormpath.sdk.impl.io.Resource;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.impl.io.UrlResource;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;

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
