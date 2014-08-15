package com.stormpath.sdk.servlet.lang;

import com.stormpath.sdk.impl.lang.AbstractResource;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

public class ServletContextResource extends AbstractResource {

    public static final String SCHEME = "servletContext";
    public static final String SCHEME_PREFIX = SCHEME + ":";

    private final ServletContext servletContext;

    public ServletContextResource(String location, ServletContext servletContext) {
        super(qualify(location));
        Assert.notNull(servletContext, "servletContext argument cannot be null.");
        this.servletContext = servletContext;
    }

    @Override
    protected String canonicalize(String input) {
        return qualify(super.canonicalize(input));
    }

    private static String qualify(String location) {
        if (location != null && !location.startsWith("/")) {
            return "/" + location;
        }
        return location;
    }

    @Override
    protected String getScheme() {
        return SCHEME;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return servletContext.getResourceAsStream(getLocation());
    }
}
