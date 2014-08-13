package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.client.DefaultClientResolver;

import javax.servlet.ServletContext;

public class DefaultApplicationResolver implements ApplicationResolver {

    public static final  String STORMPATH_APPLICATION_HREF_CONTEXT_PARAM_NAME = "stormpath.application.href";
    private static final String APP_HREF_ERROR                                =
        "The web application's web.xml does not have a " + STORMPATH_APPLICATION_HREF_CONTEXT_PARAM_NAME +
        " context-param defined with a param-value of the href of a Stormpath registered " +
        " application.  This is required when looking up an application by ServletContext.  For example:\n\n" +
        "<context-param>\n" +
        "  <param-name>" + STORMPATH_APPLICATION_HREF_CONTEXT_PARAM_NAME + "</param-name>\n" +
        "  <param-value>YOUR_STORMPATH_APPLICATION_HREF</param-value>\n" +
        "</context-param>";

    private final ClientResolver CLIENT_RESOLVER = new DefaultClientResolver();

    @Override

    public Application getApplication(final ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");

        //get the client:
        Client client = CLIENT_RESOLVER.getClient(servletContext);

        //now lookup the app:
        String href = servletContext.getInitParameter(STORMPATH_APPLICATION_HREF_CONTEXT_PARAM_NAME);
        Assert.hasText(href, APP_HREF_ERROR);
        return client.getResource(href, Application.class);
    }
}
