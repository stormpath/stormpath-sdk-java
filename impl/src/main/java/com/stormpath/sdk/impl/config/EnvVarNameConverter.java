package com.stormpath.sdk.impl.config;

public interface EnvVarNameConverter {

    String toEnvVarName(String dottedPropertyName);

    String toDottedPropertyName(String envVarName);
}
