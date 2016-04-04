package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

public class DefaultEnvVarNameConverter implements EnvVarNameConverter {

    @Override
    public String toEnvVarName(String dottedPropertyName) {
        Assert.hasText(dottedPropertyName, "dottedPropertyName argument cannot be null or empty.");
        dottedPropertyName = Strings.trimWhitespace(dottedPropertyName);

        StringBuilder sb = new StringBuilder();

        for(char c : dottedPropertyName.toCharArray()) {
            if (c == '.') {
                sb.append('_');
                continue;
            }
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
        }

        return sb.toString();
    }

    @Override
    public String toDottedPropertyName(String envVarName) {
        Assert.hasText(envVarName, "envVarName argument cannot be null or empty.");
        envVarName = Strings.trimWhitespace(envVarName);

        //special cases (camel case):
        if ("STORMPATH_API_KEY_ID".equals(envVarName)) {
            return "stormpath.apiKey.id";
        }
        if ("STORMPATH_API_KEY_SECRET".equals(envVarName)) {
            return "stormpath.apiKey.secret";
        }
        if ("STORMPATH_API_KEY_FILE".equals(envVarName)) {
            return "stormpath.apiKey.file";
        }
        if ("STORMPATH_BASEURL".equals(envVarName)) {
            return "stormpath.baseUrl";
        }

        //default cases:
        StringBuilder sb = new StringBuilder();

        for(char c : envVarName.toCharArray()) {
            if (c == '_') {
                sb.append('.');
                continue;
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
}
