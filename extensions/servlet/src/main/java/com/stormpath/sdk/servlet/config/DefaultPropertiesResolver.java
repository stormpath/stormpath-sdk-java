package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.client.DefaultServletContextClientFactory;

import javax.servlet.ServletContext;
import java.util.Properties;

public class DefaultPropertiesResolver implements PropertiesResolver {

    public static final String ATTR_NAME = DefaultServletContextClientFactory.STORMPATH_CONFIG_PROPERTIES;

    public Properties getConfig(ServletContext servletContext) {
        Object value = servletContext.getAttribute(ATTR_NAME);
        if (value == null) {
            return new Properties();
        }

        Assert.isInstanceOf(Properties.class, value, "ServletContext attribute '" + ATTR_NAME + "' must be a " +
                                                     Properties.class.getName() + " instance.");

        return (Properties)value;
    }
}
