package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.impl.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @since 1.0.RC9
 */
public class TestStringResource implements Resource {

    private String string;

    public TestStringResource(String string) {
        this.string = string;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(string.getBytes());
    }
}
