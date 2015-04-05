/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ClientAccessibleProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultProviderModelFactory implements ProviderModelFactory {

    private final String uri;
    private final String view;
    private final String requestedScopes;
    private final ServerUriResolver serverUriResolver;

    public DefaultProviderModelFactory(String uri, String view, String requestedScopes, ServerUriResolver serverUriResolver) {
        this.uri = uri;
        this.view = view;
        this.requestedScopes = Strings.hasText(requestedScopes) ? requestedScopes : "";
        this.serverUriResolver = serverUriResolver;
    }

    @Override
    public ViewModel createModel(ProviderModelContext context) {

        Map<String, Object> model = context.getModel();

        Provider provider = context.getProvider();

        Assert.isInstanceOf(ClientAccessibleProvider.class, provider);

        ClientAccessibleProvider cap = (ClientAccessibleProvider) provider;

        String serverUri = serverUriResolver.getServerUri(context.getRequest());
        model.put("serverUri", serverUri);
        model.put("uri", uri);
        model.put("clientId", cap.getClientId());
        model.put("scopes", requestedScopes);

        return new DefaultViewModel(this.view, model);
    }
}
