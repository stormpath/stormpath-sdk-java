/*
 * Copyright 2015 Stormpath, Inc.
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
import com.stormpath.sdk.authc.BasicAuthenticationOptions;
import com.stormpath.sdk.authc.UsernamePasswordRequestBuilder;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC5
 */
public class DefaultUsernamePasswordRequestBuilder implements UsernamePasswordRequestBuilder {

    private String usernameOrEmail;
    private char[] password;
    private String host;
    private AccountStore accountStore;
    private BasicAuthenticationOptions options;
    private String organizationNameKey;

    @Override
    public UsernamePasswordRequestBuilder setUsernameOrEmail(String usernameOrEmail) {
        Assert.hasText(usernameOrEmail, "usernameOrEmail cannot be null or empty.");
        this.usernameOrEmail = usernameOrEmail;
        return this;
    }

    @Override
    public UsernamePasswordRequestBuilder setPassword(String password) {
        this.password = password != null ? password.toCharArray() : "".toCharArray();
        return this;
    }

    @Override
    public UsernamePasswordRequestBuilder setPassword(char[] password) {
        this.password = password;
        return this;
    }

    @Override
    public UsernamePasswordRequestBuilder setHost(String host) {
        Assert.notNull(host, "host cannot be null.");
        this.host = host;
        return this;
    }

    @Override
    public UsernamePasswordRequestBuilder inAccountStore(AccountStore accountStore) {
        Assert.notNull(accountStore, "accountStore cannot be null.");
        this.accountStore = accountStore;
        return this;
    }

    @Override
    public UsernamePasswordRequestBuilder withResponseOptions(BasicAuthenticationOptions options) {
        Assert.notNull(options, "options cannot be null.");
        this.options = options;
        return this;
    }

    /**
     * @since 1.2.0
     */
    @Override
    public UsernamePasswordRequestBuilder setOrganizationNameKey(String orgNameKey) {
        Assert.hasText(orgNameKey, "orgNameKey cannot be null or empty.");
        this.organizationNameKey = orgNameKey;
        return this;
    }

    @Override
    public AuthenticationRequest build() {
        Assert.state(this.usernameOrEmail != null, "usernameOrEmail has not been set. It is a required attribute.");
        DefaultUsernamePasswordRequest request = new DefaultUsernamePasswordRequest(usernameOrEmail, password);
        if (this.host != null) { request.setHost(this.host); }
        if (this.accountStore != null) { request.setAccountStore(this.accountStore); }
        if (this.options != null) { request.setResponseOptions(this.options); }
        if (this.organizationNameKey != null) { request.setOrganizationNameKey(this.organizationNameKey); }
        return request;
    }

}
