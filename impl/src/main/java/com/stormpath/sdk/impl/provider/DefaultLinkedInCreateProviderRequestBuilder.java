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
package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.LinkedInCreateProviderRequestBuilder;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultLinkedInCreateProviderRequestBuilder extends AbstractCreateProviderRequestBuilder<LinkedInCreateProviderRequestBuilder> implements LinkedInCreateProviderRequestBuilder {

    private String redirectUri;

    @Override
    public LinkedInCreateProviderRequestBuilder setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    @Override
    protected String getConcreteProviderId() {
        return IdentityProviderType.LINKEDIN.getNameKey();
    }

    @Override
    protected CreateProviderRequest doBuild(Map<String, Object> map) {
        DefaultLinkedInProvider provider = new DefaultLinkedInProvider(null, map);
        provider.setClientId(super.clientId);
        provider.setClientSecret(super.clientSecret);

        if (Strings.hasText(redirectUri)) {
            provider.setRedirectUri(redirectUri);
        }

        if (super.userInfoMappingRules != null) {
            provider.setUserInfoMappingRules(super.userInfoMappingRules);
        }
        return new DefaultCreateProviderRequest(provider);
    }
}