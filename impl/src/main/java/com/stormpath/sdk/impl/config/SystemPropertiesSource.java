package com.stormpath.sdk.impl.config;

import java.util.Properties;

public class SystemPropertiesSource implements PropertiesSource {

    @Override
    public Properties getProperties() {
        Properties properties = System.getProperties();
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }
}
