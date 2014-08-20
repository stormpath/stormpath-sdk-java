package com.stormpath.sdk.servlet.config;

import javax.servlet.ServletContext;
import java.util.Properties;

public interface PropertiesResolver {

    Properties getConfig(ServletContext servletContext);
}
