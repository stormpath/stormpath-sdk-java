package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Strings;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class SystemPropertiesSource implements PropertiesSource {

    @Override
    public Map<String,String> getProperties() {

        Map<String,String> properties = new LinkedHashMap<String, String>();

        Properties systemProps = System.getProperties();

        if (systemProps != null && !systemProps.isEmpty()) {

            Enumeration e = systemProps.propertyNames();

            while(e.hasMoreElements()) {

                Object name = e.nextElement();
                String key = String.valueOf(name);
                String value = systemProps.getProperty(key);

                if (Strings.hasText(value)) {
                    properties.put(key, value);
                }
            }
        }

        return properties;
    }
}
