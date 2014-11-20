package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;

import javax.servlet.ServletContext;
import java.util.LinkedHashMap;

public class DefaultConfigResolver implements ConfigResolver {

    public Config getConfig(ServletContext servletContext) {
        Object value = servletContext.getAttribute(Config.class.getName());
        if (value == null) {
            return new DefaultConfig(servletContext, new LinkedHashMap<String, String>());
        }

        Assert.isInstanceOf(Config.class, value, "ServletContext attribute '" + Config.class.getName() + "' must be a " +
                                                 Config.class.getName() + " instance.");

        return (Config) value;
    }
}
