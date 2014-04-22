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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.*;

public class DefaultFacebookAccountRequest extends AbstractProviderAccountRequest {

    private DefaultFacebookAccountRequest(ProviderData providerData) {
        super(providerData);
    }

    public static final class Builder extends ProviderAccountRequest.Builder<FacebookAccountRequestBuilder> implements FacebookAccountRequestBuilder {

        @Override
        public ProviderAccountRequest build() {
            Assert.hasText(this.accessToken, "accessToken is a required property. It must be provided before building.");

            DefaultFacebookProviderData providerData = new DefaultFacebookProviderData(null);

            providerData.setAccessToken(this.accessToken);
            providerData.setProviderId(IdentityProviderType.FACEBOOK.getNameKey());

            return new DefaultFacebookAccountRequest(providerData);
        }
    }

}