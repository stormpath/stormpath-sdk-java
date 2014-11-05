package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.servlet.config.impl.DefaultConfigResolver;

import javax.servlet.ServletContext;

public interface ConfigResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigResolver DefaultConfigResolver}.
     */
    public static final ConfigResolver INSTANCE = new DefaultConfigResolver();

    Config getConfig(ServletContext servletContext);
}
