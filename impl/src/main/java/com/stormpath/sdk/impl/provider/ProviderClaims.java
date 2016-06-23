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
package com.stormpath.sdk.impl.provider;

import io.jsonwebtoken.impl.DefaultClaims;

/**
 * @since 1.0.0
 */
public class ProviderClaims extends DefaultClaims {

    public static final String CALLBACK_URI = "cb_uri";
    public static final String PATH = "path";
    public static final String STATE = "state";
    public static final String ORGANIZATION_NAME_KEY = "onk";
    public static final String SP_TOKEN = "sp_token";

    public String getCallbackUri() {
        return getString(CALLBACK_URI);
    }

    public ProviderClaims setCallbackUri(String callbackUri) {
        setValue(CALLBACK_URI, callbackUri);
        return this;
    }

    public String getState() {
        return getString(STATE);
    }

    public ProviderClaims setState(String state) {
        setValue(STATE, state);
        return this;
    }

    public String getPath() {
        return getString(PATH);
    }

    public ProviderClaims setPath(String path) {
        setValue(PATH, path);
        return this;
    }

    public String getOrganizationNameKey() {
        return getString(ORGANIZATION_NAME_KEY);
    }

    public ProviderClaims setOrganizationNameKey(String organizationNameKey) {
        setValue(ORGANIZATION_NAME_KEY, organizationNameKey);
        return this;
    }

    public String getSpToken() {
        return getString(SP_TOKEN);
    }

    public ProviderClaims setSpToken(String spToken) {
        setValue(SP_TOKEN, spToken);
        return this;
    }


}
