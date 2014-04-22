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

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.oauth.GoogleProviderData;

import java.util.Map;

public class DefaultGoogleProviderData extends AbstractProviderData implements GoogleProviderData {

    // SIMPLE PROPERTIES
    static final StringProperty ACCESS_TOKEN = new StringProperty("accessToken");
    static final StringProperty CODE = new StringProperty("code");
    static final StringProperty REFRESH_TOKEN = new StringProperty("refreshToken");

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PROVIDER_ID, CREATED_AT, MODIFIED_AT, ACCESS_TOKEN, CODE, REFRESH_TOKEN);

    public DefaultGoogleProviderData(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGoogleProviderData(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    public void setAccessToken(String accessToken) {
        setProperty(ACCESS_TOKEN, accessToken);
    }

    @Override
    public String getCode() {
        return getString(CODE);
    }

    public void setCode(String code) {
        setProperty(CODE, code);
    }

    @Override
    public String getRefreshToken() {
        return getString(REFRESH_TOKEN);
    }

    public void setRefreshToken(String refreshToken) {
        setProperty(REFRESH_TOKEN, refreshToken);
    }

//    @Override
//    public void visit(DefaultProviderData defaultProviderData) {
//        for (String property : this.getPropertyDescriptors().keySet()) {
//            copyPropertyValue(property, defaultProviderData);
//        }
//        this.setProperty(HREF_PROP_NAME, defaultProviderData.getHref());
//    }
//
//    private void copyPropertyValue(String property, DefaultProviderData from) {
//        Object propertyValue = from.getProperty(property);
//        if(property != null) {
//            this.setProperty(property, propertyValue);
//        }
//    }

//    @Override
//    public void setCode(String code) {
//        setProperty(CODE, code);
//    }

}
