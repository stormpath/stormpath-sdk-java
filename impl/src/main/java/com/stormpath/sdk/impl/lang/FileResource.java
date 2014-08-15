package com.stormpath.sdk.impl.lang;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileResource extends AbstractResource {

    public static final String SCHEME = "file";
    public static final String SCHEME_PREFIX = SCHEME + ":";

    public FileResource(String location) {
        super(location);
    }

    @Override
    protected String getScheme() {
        return SCHEME;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(getLocation());
    }
}
