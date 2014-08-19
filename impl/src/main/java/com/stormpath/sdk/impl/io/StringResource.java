package com.stormpath.sdk.impl.io;

import com.stormpath.sdk.lang.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringResource implements Resource {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final String  string;
    private final Charset charset;

    public StringResource(String s) {
        this(s, UTF_8);
    }

    public StringResource(String string, Charset charset) {
        Assert.hasText(string, "String argument cannot be null or empty.");
        Assert.notNull(charset, "Charset argument cannot be null or empty.");
        this.string = string;
        this.charset = charset;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(string.getBytes(charset));
    }
}
