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
package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.PairedApiKey;
import com.stormpath.sdk.impl.application.OktaApplication;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory;
import com.stormpath.sdk.servlet.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class DefaultApplicationResolver implements ApplicationResolver {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultApplicationResolver.class);
    public static final String STORMPATH_APPLICATION_HREF = DefaultServletContextClientFactory.STORMPATH_APPLICATION_HREF;

    private static final String APP_HREF_ERROR =
        "The application's stormpath.properties configuration does not have a " + STORMPATH_APPLICATION_HREF +
            " property defined.  This property is required when looking up an application by ServletContext and " +
            "you have more than one application registered in Stormpath.  For example:\n\n" +
            " # in stormpath.properties:\n" +
            " " + STORMPATH_APPLICATION_HREF + " = YOUR_STORMPATH_APPLICATION_HREF_HERE\n";

    protected Client getClient(ServletContext sc) {
        Client client = (Client) sc.getAttribute(Client.class.getName());
        Assert.notNull(client, "Stormpath Client instance is not available in the ServletContext.  Ensure the " +
            "ClientLoaderListener is defined before the ApplicationLoaderListener.");
        return client;
    }

    protected Config getConfig(ServletContext servletContext) {
        Config config = (Config) servletContext.getAttribute(Config.class.getName());
        Assert.notNull(config, "Stormpath Config instance is not available in the ServletContext.  Ensure the " +
            "ConfigLoaderListener is defined before the ApplicationLoaderListener.");
        return config;
    }

    @Override
    public Application getApplication(final ServletRequest servletRequest) {

        Object attribute = servletRequest.getAttribute(Application.class.getName());
        if (attribute != null) {
            Assert.isInstanceOf(Application.class, attribute);
            return (Application) attribute;
        }

        return getApplication(servletRequest.getServletContext());
    }

    @Override
    public Application getApplication(final ServletContext servletContext) {

        Assert.notNull(servletContext, "ServletContext argument cannot be null.");

        //see if it has been added to the servlet context yet:
        Object attribute = servletContext.getAttribute(Application.class.getName());
        if (attribute != null) {
            Assert.isInstanceOf(Application.class, attribute);
            return (Application) attribute;
        }

        //otherwise we have to do a lookup via the client and cache the result in the app context:

        //get the client:
        Client client = getClient(servletContext);

        Config config = getConfig(servletContext);
        if (Boolean.valueOf(config.get("okta.enabled"))) {

            try {
                EmailService emailService = config.getInstance("stormpath.email.service");

                Map<String, Object> appConfigMap = new LinkedHashMap<>();
                appConfigMap.put(OktaApplication.AUTHORIZATION_SERVER_ID_KEY, config.getOktaAuthorizationServerId());
                appConfigMap.put(OktaApplication.EMAIL_SERVICE_KEY, emailService);
                appConfigMap.put(OktaApplication.REGISTRATION_WORKFLOW_KEY, Boolean.valueOf(config.get("stormpath.registration.workflow.enabled")));
                appConfigMap.put("client", client);

                // TODO: There must be a better way to get the clientId
                OktaApplication oktaApplication = new OktaApplication(((PairedApiKey)client.getApiKey()).getSecondaryApiKey().getId(),
                                                                      (InternalDataStore) client.getDataStore());
                oktaApplication.configureWithProperties(appConfigMap);

                return oktaApplication;

            } catch (ServletException e) {
                LOG.error("Failed to configure EmailService", e);
                throw new ResourceException(new DefaultError()
                        .setCode(500)
                        .setMessage("Failed to resolve Application")
                        .setDeveloperMessage(e.getMessage()));
            }
        }

        //this is a local cached href value that we use in case we have to query applications (see below):
        String href = (String) servletContext.getAttribute(STORMPATH_APPLICATION_HREF);

        if (href == null) {
            //no cached value = try config:
            href = config.get(STORMPATH_APPLICATION_HREF);
        }

        Application application;

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

            application = single;

        } else {
            Assert.hasText(href, "The specified " + STORMPATH_APPLICATION_HREF + " property value cannot be empty.");

            //now lookup the app (will be cached when caching is turned on in the SDK):
            try {
                application = client.getResource(href, Application.class);
            } catch (Exception e) {
                String msg = "Unable to lookup Stormpath application reference by " + STORMPATH_APPLICATION_HREF +
                    " [" + href + "].  Please ensure this href is accurate and reflects an application " +
                    "registered in Stormpath.";
                throw new IllegalArgumentException(msg, e);
            }
        }

        //cache for future lookups:
        servletContext.setAttribute(Application.class.getName(), application);

        return application;
    }
}
