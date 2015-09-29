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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OauthGrantAuthenticationResult;
import com.stormpath.sdk.authc.PasswordGrantAuthenticator;
import com.stormpath.sdk.authc.PasswordGrantRequest;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;

public class DefaultPasswordGrantAuthenticator implements PasswordGrantAuthenticator {

    private Application application;

    private InternalDataStore dataStore;

    final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultPasswordGrantAuthenticator(Application application, DataStore dataStore) {
        this.application = application;
        this.dataStore = (InternalDataStore) dataStore;
    }

    @Override
    public OauthGrantAuthenticationResult authenticate(PasswordGrantRequest passwordGrantRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        CreateOauthTokenAttempt createOauthTokenAttempt = new DefaultCreateOauthTokenAttempt(dataStore);
        createOauthTokenAttempt.setLogin(passwordGrantRequest.getLogin());
        createOauthTokenAttempt.setPassword(passwordGrantRequest.getPassword());
        createOauthTokenAttempt.setGrantType(passwordGrantRequest.getGrantType());
        if (passwordGrantRequest.getAccountStore() != null){
            createOauthTokenAttempt.setAccountStore(passwordGrantRequest.getAccountStore());
        }
        dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, createOauthTokenAttempt, MediaType.APPLICATION_FORM_URLENCODED);
        return null;
    }

    @Override
    public PasswordGrantAuthenticator forApplication(Application application) {
        Assert.notNull(application, "application cannot be null or empty.");
        this.application = application;
        return this;
    }
}
