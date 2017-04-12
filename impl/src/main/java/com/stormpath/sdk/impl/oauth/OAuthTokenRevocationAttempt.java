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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

/**
 * @since 1.2.0
 */
public class OAuthTokenRevocationAttempt extends AbstractResource implements Resource {

    public static final StringProperty TOKEN = new StringProperty("token");
    public static final StringProperty TOKEN_TYPE_HINT = new StringProperty("token_type_hint");

    private static final Map<String, Property> DESCRIPTOR_MAP = createPropertyDescriptorMap(TOKEN, TOKEN_TYPE_HINT);

    public OAuthTokenRevocationAttempt(InternalDataStore dataStore, String token) {
        super(dataStore);
        Assert.hasText(token, "token cannot be null or empty.");
        setProperty(TOKEN, token);
        setProperty("token_type_hint", "access_token");
    }

    public void setTokenTypeHint(String tokenTypeHint) {
        setProperty(TOKEN_TYPE_HINT, tokenTypeHint);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return DESCRIPTOR_MAP;
    }

}
