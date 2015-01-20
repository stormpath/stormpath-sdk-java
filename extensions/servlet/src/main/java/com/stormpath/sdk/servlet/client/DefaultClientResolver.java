/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * @since 1.0.RC3
 */
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

    @Override
    public Client getClient(final ServletRequest request) {
        return getClient(request.getServletContext());
    }
}
