package com.stormpath.sdk.impl.lang;

import com.stormpath.sdk.lang.Collections;

import java.util.Map;
import java.util.Properties;

public class EnvironmentVariablesPropertiesSource implements PropertiesSource {

    @Override
    public Properties getProperties() {
        Properties props = new Properties();
        Map<String,String> envVars = System.getenv();
        if (!Collections.isEmpty(envVars)) {
            props.putAll(envVars);
        }
        return props;
    }
}
