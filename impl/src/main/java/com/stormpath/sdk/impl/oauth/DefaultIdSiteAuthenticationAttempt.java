/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
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

import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultIdSiteAuthenticationAttempt extends AbstractResource implements IdSiteAuthenticationAttempt {

    static final StringProperty TOKEN = new StringProperty("token");
    static final StringProperty GRANT_TYPE = new StringProperty("grant_type");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(TOKEN, GRANT_TYPE);

    public DefaultIdSiteAuthenticationAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultIdSiteAuthenticationAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public String getToken() {
        return getString(TOKEN);
    }

    @Override
    public void setToken(String value) {
        setProperty(TOKEN, value);
    }

    @Override
    public void setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
    }

    public String getGrantType(){
        return getString(GRANT_TYPE);
    }
}