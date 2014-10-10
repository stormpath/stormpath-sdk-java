package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Collections;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnvironmentVariablesPropertiesSource implements PropertiesSource {

    @Override
    public Map<String,String> getProperties() {

        Map<String,String> envVars = System.getenv();

        if (!Collections.isEmpty(envVars)) {
            return new LinkedHashMap<String, String>(envVars);
        }

        return java.util.Collections.emptyMap();
    }
}
