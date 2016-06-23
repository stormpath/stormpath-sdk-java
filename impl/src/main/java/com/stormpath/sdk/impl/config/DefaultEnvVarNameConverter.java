package com.stormpath.sdk.impl.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

public class DefaultEnvVarNameConverter implements EnvVarNameConverter {

    @Override
    public String toEnvVarName(String dottedPropertyName) {
        Assert.hasText(dottedPropertyName, "dottedPropertyName argument cannot be null or empty.");
        dottedPropertyName = Strings.trimWhitespace(dottedPropertyName);

        //special cases (camel case):
        if ("stormpath.client.apiKey.id".equals(dottedPropertyName)) {
            return "STORMPATH_API_KEY_ID";
        }
        if ("stormpath.client.apiKey.secret".equals(dottedPropertyName)) {
            return "STORMPATH_API_KEY_SECRET";
        }
        if ("stormpath.client.apiKey.file".equals(dottedPropertyName)) {
            return "STORMPATH_API_KEY_FILE";
        }
        if ("stormpath.client.authenticationScheme".equals(dottedPropertyName)) {
            return "STORMPATH_AUTHENTICATION_SCHEME";
        }

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
            return "stormpath.client.apiKey.id";
        }
        if ("STORMPATH_API_KEY_SECRET".equals(envVarName)) {
            return "stormpath.client.apiKey.secret";
        }
        if ("STORMPATH_API_KEY_FILE".equals(envVarName)) {
            return "stormpath.client.apiKey.file";
        }
        if ("STORMPATH_AUTHENTICATION_SCHEME".equals(envVarName)) {
            return "stormpath.client.authenticationScheme";
        }
        if ("STORMPATH_BASEURL".equals(envVarName)) {
            return "stormpath.baseUrl";
        }
        if ("STORMPATH_WEB_VERIFYEMAIL_ENABLED".equals((envVarName))) {
            return "stormpath.web.verifyEmail.enabled";
        }
        if ("STORMPATH_WEB_FORGOTPASSWORD_ENABLED".equals((envVarName))) {
            return "stormpath.web.forgotPassword.enabled";
        }
        if ("STORMPATH_WEB_CHANGEPASSWORD_ENABLED".equals((envVarName))) {
            return "stormpath.web.changePassword.enabled";
        }
        if ("STORMPATH_WEB_IDSITE_ENABLED".equals((envVarName))) {
            return "stormpath.web.idSite.enabled";
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
