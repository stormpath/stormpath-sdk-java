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
package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class DefaultCreateAccountRequestBuilder implements CreateAccountRequestBuilder {

    private Account account;
    private Boolean registrationWorkflowEnabled;
    private PasswordFormat passwordFormat;
    private AccountOptions options;
    private String organizationNameKey;

    public DefaultCreateAccountRequestBuilder(Account account) {
        Assert.notNull(account, "Account cannot be null.");
        this.account = account;
    }

    public PasswordFormat getPasswordFormat() {
        return passwordFormat;
    }

    @Override
    public CreateAccountRequestBuilder setRegistrationWorkflowEnabled(boolean registrationWorkflowEnabled) {
        this.registrationWorkflowEnabled = registrationWorkflowEnabled;
        return this;
    }

    @Override
    public CreateAccountRequestBuilder setPasswordFormat(PasswordFormat passwordFormat) {
        this.passwordFormat = passwordFormat;
        return this;
    }

    @Override
    public CreateAccountRequestBuilder withResponseOptions(AccountOptions options) {
        Assert.notNull(options);
        this.options = options;
        return this;
    }

    @Override
    public CreateAccountRequestBuilder withOrganizationNameKey(String organizationNameKey) throws IllegalArgumentException {
        this.organizationNameKey = organizationNameKey;
        return this;
    }

    @Override
    public CreateAccountRequest build() {
        return new DefaultCreateAccountRequest(this.account, this.registrationWorkflowEnabled, this.options, this.organizationNameKey).setPasswordFormat(this.passwordFormat);
    }
}
