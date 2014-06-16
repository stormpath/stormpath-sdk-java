/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;

import java.io.UnsupportedEncodingException;

/**
 * @since 0.2
 */
public class BasicAuthenticator {

    private InternalDataStore dataStore;

    public BasicAuthenticator(InternalDataStore dataStore) {
        Assert.notNull(dataStore);
        this.dataStore = dataStore;
    }

    public AuthenticationResult authenticate(String parentHref, AuthenticationRequest request) {
        Assert.notNull(parentHref, "href argument must be specified");
        Assert.isInstanceOf(UsernamePasswordRequest.class, request, "Only UsernamePasswordRequest instances are supported.");
        UsernamePasswordRequest upRequest = (UsernamePasswordRequest)request;

        String username = upRequest.getPrincipals();
        username = (username != null) ? username : "";

        char[] password = upRequest.getCredentials();
        String pwString = (password != null && password.length > 0) ? new String(password) : "";

        String value = username + ":" + pwString;
        byte[] valueBytes;
        try {
            valueBytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to acquire UTF-8 bytes!");
        }
        value = Base64.encodeBase64String(valueBytes);

        BasicLoginAttempt attempt = this.dataStore.instantiate(BasicLoginAttempt.class);

        attempt.setType("basic");
        attempt.setValue(value);

        if(request.getAccountStore() != null) {
            attempt.setAccountStore(request.getAccountStore());
        }

        String href = parentHref + "/loginAttempts";

        return this.dataStore.create(href, attempt, AuthenticationResult.class);
    }

}
