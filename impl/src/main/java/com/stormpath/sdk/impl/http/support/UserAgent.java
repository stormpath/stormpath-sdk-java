/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.http.support;

import com.stormpath.sdk.lang.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @since 1.0.0
 */
public class UserAgent {

    public static final String USER_AGENT_INTEGRATION = "STORMPATH_USER_AGENT_INTEGRATION";
    public static final String USER_AGENT_INTEGRATION_ENVIRONMENT = "STORMPATH_USER_AGENT_INTEGRATION_ENVIRONMENT";

    private static final String STORMPATH_SDK_STRING = "stormpath-sdk-java";
    private static final String RUNTIME_STRING = "java";
    private static final String VERSION_SEPARATOR = "/";
    private static final String SEPARATOR = " ";

    private static final String USER_AGENT = createUserAgentString();

    public static String getDefaultUserAgentString() {
        return USER_AGENT;
    }

    private static String createUserAgentString() {
        return getIntegrationEnvironmentString() +
                getIntegrationString() +
                getStormpathSdkString() + SEPARATOR +
                getRuntimeString() + SEPARATOR +
                getOSString();
    }

    private static String getStormpathSdkString() {
        return STORMPATH_SDK_STRING + VERSION_SEPARATOR + Version.getClientVersion();
    }

    private static String getRuntimeString() {
        return RUNTIME_STRING + VERSION_SEPARATOR + System.getProperty("java.version");
    }

    private static String getOSString() {
        return System.getProperty("os.name") + VERSION_SEPARATOR + System.getProperty("os.version");
    }

    private static String getIntegrationString() {
        //return getString("/com/stormpath/sdk/user_agent_integration.properties");
        //return getString("/com/stormpath/sdk/user_agent_integration_environment.properties");
        return getStringFromSystemProperty(USER_AGENT_INTEGRATION);
    }

    private static String getIntegrationEnvironmentString() {
        return getStringFromSystemProperty(USER_AGENT_INTEGRATION_ENVIRONMENT);
        //return getString("/com/stormpath/sdk/user_agent_integration_environment.properties");
    }

    private static String getStringFromSystemProperty(String environmentVariable) {
        //return getString("/com/stormpath/sdk/user_agent_integration_environment.properties");
        String property = System.getProperty(environmentVariable);
        if(Strings.hasText(property)) {
            return property + SEPARATOR;
        }
        return "";
    }


    private static String getString(String filePath) {
        String integrationString = readLine(filePath);
        if (Strings.hasText(integrationString)) {
            return integrationString + SEPARATOR;
        }
        return "";
    }

    private static String readLine(String filePath) {
        Class clazz = UserAgent.class;
        String integrationString = "";
        try {
            InputStream inputStream = clazz.getResourceAsStream(filePath);
            if(inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                integrationString = reader.readLine();
            }
        } catch (IOException e) {
            //there was a problem reading the file, we will swallow the exception
        }

        return integrationString;
    }


}
