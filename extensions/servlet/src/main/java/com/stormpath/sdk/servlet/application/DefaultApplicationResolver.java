package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.client.DefaultClientResolver;
import com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory;

import javax.servlet.ServletContext;

public class DefaultApplicationResolver implements ApplicationResolver {

    public static final String STORMPATH_APPLICATION_HREF =
        DefaultServletContextClientFactory.STORMPATH_APPLICATION_HREF;

    private static final String APP_HREF_ERROR =
        "The application's stormpath.properties configuration does not have a " + STORMPATH_APPLICATION_HREF +
        " property defined.  This is required when looking up an application by ServletContext.  For example:\n\n" +
        " # in stormpath.properties:\n" +
        " " + STORMPATH_APPLICATION_HREF + " = YOUR_STORMPATH_APPLICATION_HREF_HERE\n";

    private final ClientResolver CLIENT_RESOLVER = new DefaultClientResolver();

    @Override

    public Application getApplication(final ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");

        //get the client:
        Client client = CLIENT_RESOLVER.getClient(servletContext);

        Object attr = servletContext.getAttribute(STORMPATH_APPLICATION_HREF);

        Assert.notNull(attr, APP_HREF_ERROR);

        Assert.isInstanceOf(String.class, attr, STORMPATH_APPLICATION_HREF +
                                                " Servlet Context attribute value must be a String.");
        String href = (String)attr;
        Assert.hasText(href, APP_HREF_ERROR);

        //now lookup the app (will be cached when caching is turned on in the SDK):
        return client.getResource(href, Application.class);
    }
}
