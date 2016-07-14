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

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthPasswordGrantAuthenticationAttempt extends AbstractResource implements OAuthPasswordGrantAuthenticationAttempt {

    static final StringProperty LOGIN = new StringProperty("username");
    static final StringProperty PASSWORD = new StringProperty("password");
    static final StringProperty ACCOUNT_STORE_HREF = new StringProperty("accountStore");
    static final StringProperty GRANT_TYPE = new StringProperty("grant_type");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(LOGIN, PASSWORD, ACCOUNT_STORE_HREF, GRANT_TYPE);

    public DefaultOAuthPasswordGrantAuthenticationAttempt(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOAuthPasswordGrantAuthenticationAttempt(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void setPassword(String value) {
        setProperty(PASSWORD, value);
    }

    @Override
    public void setLogin(String value) {
        setProperty(LOGIN, value);
    }

    @Override
    public void setAccountStore(AccountStore value) {
        setProperty(ACCOUNT_STORE_HREF, value.getHref());
    }

    public String getLogin() {
        return getString(LOGIN);
    }

    @Override
    public void setGrantType(String grantType) {
        setProperty(GRANT_TYPE, grantType);
    }

    public String getPassword() {
        return getString(PASSWORD);
    }

    public String getAccountStoreHref() {
        return getString(ACCOUNT_STORE_HREF);
    }

    public String getGrantType(){
        return getString(GRANT_TYPE);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
