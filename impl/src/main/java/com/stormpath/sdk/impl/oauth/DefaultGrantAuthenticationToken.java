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
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;

import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.RefreshToken;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultGrantAuthenticationToken extends AbstractInstanceResource implements GrantAuthenticationToken {

    static final StringProperty ACCESS_TOKEN = new StringProperty("access_token");
    static final StringProperty REFRESH_TOKEN = new StringProperty("refresh_token");
    static final StringProperty TOKEN_TYPE = new StringProperty("token_type");
    static final StringProperty EXPIRES_IN = new StringProperty("expires_in");
    static final StringProperty ACCESS_TOKEN_HREF = new StringProperty("stormpath_access_token_href");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ACCESS_TOKEN, REFRESH_TOKEN, EXPIRES_IN, TOKEN_TYPE, ACCESS_TOKEN_HREF);

    public DefaultGrantAuthenticationToken(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGrantAuthenticationToken(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    public String getRefreshToken() {
        return getString(REFRESH_TOKEN);
    }

    public String getTokenType() {
        return getString(TOKEN_TYPE);
    }

    public String getExpiresIn() {
        return getString(EXPIRES_IN);
    }

    public String getAccessTokenHref() {
        return getString(ACCESS_TOKEN_HREF);
    }

    public AccessToken getAsAccessToken(){
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", this.getAccessTokenHref());
        return getDataStore().instantiate(AccessToken.class, props);
    }

    public RefreshToken getAsRefreshToken(){
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        String refreshTokenID = (String)((Map)this.getAsAccessToken().getExpandedJwt().get("claims")).get("rti");
        props.put("href", "/refreshTokens/" + refreshTokenID);
        return getDataStore().instantiate(RefreshToken.class, props);
    }
}
