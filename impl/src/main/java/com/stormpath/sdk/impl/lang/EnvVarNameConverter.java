package com.stormpath.sdk.impl.lang;

public interface EnvVarNameConverter {

    String toEnvVarName(String dottedPropertyName);

    String toDottedPropertyName(String envVarName);
}
