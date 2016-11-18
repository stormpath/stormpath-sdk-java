/*
 * Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.directory.Directory;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

/**
 * @since 1.0.0
 */
public class DefaultAccountStoreModel implements AccountStoreModel {

    private final Directory directory;
    private final ProviderModel providerModel;
    private final String authorizeUri;

    public DefaultAccountStoreModel(Directory directory, ProviderModel provider, String authorizeBaseUri) {
        this.directory = directory;
        this.providerModel = provider;
        if (providerModel instanceof OAuthProviderModel && authorizeBaseUri != null) {
            try {
                URIBuilder builder = new URIBuilder(authorizeBaseUri);
                builder.setPath("/authorize");
                builder.addParameter("response_type", "stormpath_token");
                builder.addParameter("account_store_href", directory.getHref());
                authorizeUri = builder.build().toString();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("authorizeBaseUri must be value URI", e);
            }
        } else {
            authorizeUri = null;
        }
    }

    @Override
    public String getHref() {
        return this.directory.getHref();
    }

    @Override
    public String getName() {
        return this.directory.getName();
    }

    @Override
    public String getAuthorizeUri() {
        return authorizeUri;
    }

    @Override
    public ProviderModel getProvider() {
        return this.providerModel;
    }
}
