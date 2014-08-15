package com.stormpath.sdk.impl.lang;

import com.stormpath.sdk.lang.Classes;

import java.io.IOException;
import java.io.InputStream;

public class ClasspathResource extends AbstractResource {

    public static final String SCHEME = "classpath";
    public static final String SCHEME_PREFIX = SCHEME + ":";

    public ClasspathResource(String location) {
        super(location);
    }

    @Override
    protected String getScheme() {
        return SCHEME;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Classes.getResourceAsStream(getLocation());
    }
}
