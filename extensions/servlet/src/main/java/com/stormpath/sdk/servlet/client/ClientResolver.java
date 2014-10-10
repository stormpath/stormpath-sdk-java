package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.client.Client;

import javax.servlet.ServletContext;

public interface ClientResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link com.stormpath.sdk.servlet.client.DefaultClientResolver DefaultClientResolver}.
     */
    public static final ClientResolver INSTANCE = new DefaultClientResolver();

    Client getClient(ServletContext servletContext);
}
