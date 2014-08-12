package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.client.Client;

import javax.servlet.ServletContext;

public interface ClientResolver {

    Client getClient(ServletContext servletContext);
}
