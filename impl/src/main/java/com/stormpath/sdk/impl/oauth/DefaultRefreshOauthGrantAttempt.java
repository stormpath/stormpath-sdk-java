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
import com.stormpath.sdk.oauth.RefreshOauthGrantAttempt;

import java.util.Map;

/**
 * @since 1.0.RC5
 */
public class DefaultRefreshOauthGrantAttempt extends AbstractResource implements RefreshOauthGrantAttempt{

    static final StringProperty REFRESH_TOKEN = new StringProperty("refresh_token");
    static final StringProperty GRANT_TYPE = new StringProperty("grant_type");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(GRANT_TYPE, REFRESH_TOKEN);

    public DefaultRefreshOauthGrantAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultRefreshOauthGrantAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
    }

    @Override
    public void setRefreshToken(String value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
