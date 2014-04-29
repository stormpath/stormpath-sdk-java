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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.FacebookAccountRequestBuilder;
import com.stormpath.sdk.provider.ProviderAccountRequest;

import java.util.Map;

public class DefaultFacebookAccountRequestBuilder extends AbstractProviderAccountRequestBuilder<FacebookAccountRequestBuilder> implements FacebookAccountRequestBuilder {

    @Override
    protected String getProviderId() {
        return IdentityProviderType.FACEBOOK.getNameKey();
    }

    @Override
    protected ProviderAccountRequest doBuild(Map<String, Object> map) {
        Assert.state(Strings.hasText(this.accessToken), "accessToken is a required property. It must be provided before building.");

        DefaultFacebookProviderData providerData = new DefaultFacebookProviderData(null, map);

        providerData.setAccessToken(this.accessToken);

        return new DefaultProviderAccountRequest(providerData);
    }
}