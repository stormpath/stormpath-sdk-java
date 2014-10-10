package com.stormpath.sdk.servlet.config;

import javax.servlet.ServletContext;

public interface ServletContextConfigFactory {

    Config getConfig(ServletContext servletContext);

}
