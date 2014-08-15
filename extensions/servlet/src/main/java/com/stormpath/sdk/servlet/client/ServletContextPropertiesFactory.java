package com.stormpath.sdk.servlet.client;

import javax.servlet.ServletContext;
import java.util.Properties;

public interface ServletContextPropertiesFactory {

    Properties getProperties(ServletContext servletContext);

}
