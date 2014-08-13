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
package com.stormpath.sdk.servlet.client;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.lang.DefaultEnvironmentVariableNameFactory;
import com.stormpath.sdk.impl.lang.EnvironmentVariableNameFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import javax.servlet.ServletContext;

/** Default {@link ClientFactory} implementation. */
public class DefaultClientFactory implements ClientFactory {

    public static final String STORMPATH_API_KEY_FILE          = "stormpath.apiKey.file";
    public static final String STORMPATH_AUTHENTICATION_SCHEME = "stormpath.authenticationScheme";

    public static final String STORMPATH_PROXY_HOST     = "stormpath.proxy.host";
    public static final String STORMPATH_PROXY_PORT     = "stormpath.proxy.port";
    public static final String STORMPATH_PROXY_USERNAME = "stormpath.proxy.username";
    public static final String STORMAPTH_PROXY_PASSWORD = "stormpath.proxy.password";

    private final EnvironmentVariableNameFactory environmentVariableNameFactory =
        new DefaultEnvironmentVariableNameFactory();

    @Override

    public Client createClient(ServletContext servletContext) {

        ApiKeyBuilder apiKeyBuilder = ApiKeys.builder();

        String location = servletContext.getInitParameter(STORMPATH_API_KEY_FILE);
        if (Strings.hasText(location)) {
            apiKeyBuilder.setFileLocation(location);
        } //otherwise default discovery heuristics will be assumed. See ApiKeyBuilder.build() for these heuristics.

        ApiKey apiKey = apiKeyBuilder.build();

        ClientBuilder builder = Clients.builder().setApiKey(apiKey);

        applyProxyIfNecessary(builder, servletContext);

        String schemeName = servletContext.getInitParameter(STORMPATH_AUTHENTICATION_SCHEME);
        if (Strings.hasText(schemeName)) {
            AuthenticationScheme scheme = AuthenticationScheme.valueOf(schemeName.toUpperCase());
            builder.setAuthenticationScheme(scheme);
        }

        return Clients.builder().setApiKey(apiKey).build();
    }

    protected void applyProxyIfNecessary(ClientBuilder builder, ServletContext servletContext) {
        String proxyHost = getValue(STORMPATH_PROXY_HOST, servletContext);
        if (proxyHost == null) {
            return;
        }

        Proxy proxy;

        //proxy is present!
        int port = 80; //default
        String portValue = getValue(STORMPATH_PROXY_PORT, servletContext);
        if (Strings.hasText(portValue)) {
            port = Integer.parseInt(portValue);
        }

        String proxyUsername = getValue(STORMPATH_PROXY_USERNAME, servletContext);
        String proxyPassword = getValue(STORMAPTH_PROXY_PASSWORD, servletContext);

        if (!Strings.hasText(proxyUsername) && !Strings.hasText(proxyPassword)) {
            proxy = new Proxy(proxyHost, port);
        } else {
            proxy = new Proxy(proxyHost, port, proxyUsername, proxyPassword);
        }

        builder.setProxy(proxy);
    }

    protected String getValue(final String propName, ServletContext servletContext) {
        Assert.hasText(propName, "propName argument cannot be null or empty.");
        Assert.notNull(servletContext, "servletContext cannot be null or empty.");

        final String envVarName = environmentVariableNameFactory.createEnvironmentVariableName(propName);

        String value = servletContext.getInitParameter(propName);
        if (Strings.hasText(value)) {
            return value;
        }

        value = servletContext.getInitParameter(envVarName);
        if (Strings.hasText(value)) {
            return value;
        }

        value = System.getProperty(propName);
        if (Strings.hasText(value)) {
            return value;
        }

        return System.getenv(envVarName);
    }
}
