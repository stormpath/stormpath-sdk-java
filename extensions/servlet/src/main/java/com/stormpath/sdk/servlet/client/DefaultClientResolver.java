package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;

public class DefaultClientResolver implements ClientResolver {

    private static final String ERROR_MSG = "There is no Client instance accessible via the ServletContext " +
                                            "attribute key [" + ClientLoader.CLIENT_ATTRIBUTE_KEY + "].  This is an " +
                                            "invalid webapp configuration.  Consider defining the " +
                                            DefaultClientLoaderListener.class.getName() +
                                            " in web.xml or manually adding " +
                                            "a Client instance to the ServletContext under this key.  For example:\n\n" +
                                            "<listener>\n" +
                                            "     <listener-class>com.stormpath.sdk.servlet.client.DefaultClientLoaderListener</listener-class>\n" +
                                            " </listener>";

    public Client getClient(final ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");

        Object object = servletContext.getAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY);

        Assert.notNull(object, ERROR_MSG);

        Assert.isInstanceOf(Client.class, object, "Object instance found under servlet context attribute name " +
                                                  ClientLoader.CLIENT_ATTRIBUTE_KEY + "' is not a " +
                                                  Client.class.getName() + " instance as required.  " +
                                                  "Instance is of type: " + object.getClass().getName());
        return (Client)object;
    }
}
