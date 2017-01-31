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

import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticationBuilder;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC7
 */
public class DefaultOAuthPasswordGrantRequestAuthenticationBuilder implements OAuthPasswordGrantRequestAuthenticationBuilder {

    private String login;
    private String password;
    private AccountStore accountStore;
    private String organizationNameKey;

    @Override
    public OAuthPasswordGrantRequestAuthenticationBuilder setLogin(String login) {
        Assert.hasText(login, "username cannot be null or empty.");
        this.login = login;
        return this;
    }

    @Override
    public OAuthPasswordGrantRequestAuthenticationBuilder setPassword(String password) {
        Assert.notNull(password, "password cannot be null or empty.");
        this.password = password;
        return this;
    }

    @Override
    public OAuthPasswordGrantRequestAuthenticationBuilder setAccountStore(AccountStore accountStore) {
        Assert.notNull(accountStore, "accountStore cannot be null or empty.");
        this.accountStore = accountStore;
        return this;
    }

    @Override
    public OAuthPasswordGrantRequestAuthenticationBuilder setOrganizationNameKey(String organizationNameKey) {
        Assert.hasText(organizationNameKey, "organizationNameKey cannot be null or empty.");
        this.organizationNameKey = organizationNameKey;
        return this;
    }

    @Override
    public OAuthPasswordGrantRequestAuthentication build() {
        Assert.state(this.login != null, "login has not been set. It is a required attribute.");
        Assert.state(this.password != null, "password has not been set. It is a required attribute.");

        DefaultOAuthPasswordGrantRequestAuthentication request = new DefaultOAuthPasswordGrantRequestAuthentication(login, password);

        Assert.isTrue((this.accountStore == null && this.organizationNameKey == null) ||
                this.accountStore != null ^ this.organizationNameKey != null, "only set accountStore or organizationNameKey or neither");

        if (this.accountStore != null) {
            request.setAccountStore(this.accountStore);
        }

        if (this.organizationNameKey != null) {
            request.setOrganizationNameKey(this.organizationNameKey);
        }

        return request;
    }
}
