package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.client.DefaultClientResolver;
import com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory;
import com.stormpath.sdk.servlet.config.DefaultPropertiesResolver;
import com.stormpath.sdk.servlet.config.PropertiesResolver;

import javax.servlet.ServletContext;
import java.util.Properties;

public class DefaultApplicationResolver implements ApplicationResolver {

    public static final String STORMPATH_APPLICATION_HREF =
        DefaultServletContextClientFactory.STORMPATH_APPLICATION_HREF;

    private static final String APP_HREF_ERROR =
        "The application's stormpath.properties configuration does not have a " + STORMPATH_APPLICATION_HREF +
        " property defined.  This property is required required when looking up an application by ServletContext and " +
        "you have more than one application in Stormpath.  For example:\n\n" +
        " # in stormpath.properties:\n" +
        " " + STORMPATH_APPLICATION_HREF + " = YOUR_STORMPATH_APPLICATION_HREF_HERE\n";

    private final ClientResolver CLIENT_RESOLVER = new DefaultClientResolver();

    private final PropertiesResolver CONFIG_RESOLVER = new DefaultPropertiesResolver();

    @Override

    public Application getApplication(final ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");

        //get the client:
        Client client = CLIENT_RESOLVER.getClient(servletContext);

        Properties config = CONFIG_RESOLVER.getConfig(servletContext);

        String href = config.getProperty(STORMPATH_APPLICATION_HREF);

        if (href == null) {

            //no stormpath.application.href property was configured.  Let's try to find their application:

            ApplicationList apps = client.getApplications();

            Application single = null;

            for (Application app : apps) {
                if (app.getName().equalsIgnoreCase("Stormpath")) { //ignore the admin app
                    continue;
                }
                if (single != null) {
                    //there is more than one application in the tenant, and we can't infer which one should be used
                    //for this particular application.  Let them know:
                    throw new IllegalStateException(APP_HREF_ERROR);
                }
                single = app;
            }

            if (single != null) {
                //save the href for later so we don't have to query the collection again:
                servletContext.setAttribute(STORMPATH_APPLICATION_HREF, single.getHref());
            }

            return single;

        } else {
            Assert.hasText(href, "The specified " + STORMPATH_APPLICATION_HREF + " property value cannot be empty.");

            //now lookup the app (will be cached when caching is turned on in the SDK):
            try {
                return client.getResource(href, Application.class);
            } catch (Exception e) {
                String msg = "Unable to lookup Stormpath application reference by " + STORMPATH_APPLICATION_HREF +
                             " [" + href + "].  Please ensure this href is accurate and reflects an application " +
                             "registered in Stormpath.";
                throw new IllegalArgumentException(msg, e);
            }
        }
    }
}
