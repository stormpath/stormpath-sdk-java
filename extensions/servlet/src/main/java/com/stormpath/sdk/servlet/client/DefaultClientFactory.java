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
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.lang.Strings;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Default {@link ClientFactory} implementation that assumes a {@code $HOME/.stormpath/apiKey.properties} file is
 * available.
 */
public class DefaultClientFactory implements ClientFactory {

    public static final String DEFAULT_API_KEY_FILE_LOCATION =
        System.getProperty("user.home") + File.separatorChar +
        ".stormpath" + File.separatorChar +
        "apiKey.properties";

    public static final String STORMPATH_API_KEY_FILE_LOCATION_PARAM_NAME = "stormpathApiKeyFileLocation";
    public static final String STORMPATH_CLIENT_AUTHENTICATION_SCHEME_PARAM_NAME = "stormpathClientAuthenticationScheme";

    @Override
    public Client createClient(ServletContext servletContext) {

        String location = servletContext.getInitParameter(STORMPATH_API_KEY_FILE_LOCATION_PARAM_NAME);

        if (!Strings.hasText(location)) {
            location = DEFAULT_API_KEY_FILE_LOCATION;
        }

        ApiKey apiKey = ApiKeys.builder().setFileLocation(location).build();

        ClientBuilder builder = Clients.builder().setApiKey(apiKey);

        String schemeName = servletContext.getInitParameter(STORMPATH_CLIENT_AUTHENTICATION_SCHEME_PARAM_NAME);
        if (Strings.hasText(schemeName)) {
            AuthenticationScheme scheme = AuthenticationScheme.valueOf(schemeName.toUpperCase());
            builder.setAuthenticationScheme(scheme);
        }

        return Clients.builder().setApiKey(apiKey).build();
    }
}
