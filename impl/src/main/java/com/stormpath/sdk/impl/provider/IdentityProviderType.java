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
import com.stormpath.sdk.provider.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Placeholder for provider-specific classes mappings.
 *
 * @since 1.0.beta
 */
public enum IdentityProviderType {

    STORMPATH("stormpath", Provider.class, ProviderData.class),
    FACEBOOK("facebook", FacebookProvider.class, FacebookProviderData.class),
    GOOGLE("google", GoogleProvider.class, GoogleProviderData.class);

    private static final Map<String, IdentityProviderType> IDENTITY_PROVIDER_MAP;
    public static final Map<String, Class<? extends Provider>> IDENTITY_PROVIDER_CLASS_MAP;
    public static final Map<String, Class<? extends ProviderData>> IDENTITY_PROVIDERDATA_CLASS_MAP;

    static {
        IDENTITY_PROVIDER_MAP = new HashMap<String, IdentityProviderType>();
        for (IdentityProviderType identityProviderType : IdentityProviderType.values()) {
            IDENTITY_PROVIDER_MAP.put(identityProviderType.nameKey, identityProviderType);
        }

        Map<String, Class<? extends Provider>> identityProviderClassMap = new HashMap<String, Class<? extends Provider>>();
        for (IdentityProviderType identityProviderType : IdentityProviderType.values()) {
            identityProviderClassMap.put(identityProviderType.nameKey, identityProviderType.getProviderClass());
        }
        IDENTITY_PROVIDER_CLASS_MAP = Collections.unmodifiableMap(identityProviderClassMap);

        Map<String, Class<? extends ProviderData>> identityProviderDataClassMap = new HashMap<String, Class<? extends ProviderData>>();
        for (IdentityProviderType identityProviderType : IdentityProviderType.values()) {
            identityProviderDataClassMap.put(identityProviderType.nameKey, identityProviderType.getProviderDataClass());
        }
        IDENTITY_PROVIDERDATA_CLASS_MAP = Collections.unmodifiableMap(identityProviderDataClassMap);
    }

    private String nameKey;
    private Class<? extends Provider> providerClass;
    private Class<? extends ProviderData> providerDataClass;

    private IdentityProviderType(String nameKey, Class<? extends Provider> providerClass, Class<? extends ProviderData> providerDataClass) {
        this.nameKey = nameKey;
        this.providerClass = providerClass;
        this.providerDataClass = providerDataClass;
    }

    public static IdentityProviderType fromNameKey(String nameKey) {
        if (nameKey == null) {
            return null;
        }

        IdentityProviderType identityProviderType = IDENTITY_PROVIDER_MAP.get(nameKey.toLowerCase());
        Assert.notNull(identityProviderType, "The nameKey provided doesn't match a valid IdentityProviderType: " + nameKey);

        return identityProviderType;
    }

    public String getNameKey() {
        return this.nameKey;
    }

    public Class<? extends Provider> getProviderClass() {
        return this.providerClass;
    }

    public Class<? extends ProviderData> getProviderDataClass() {
        return this.providerDataClass;
    }
}
