/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.impl.account.DefaultPasswordResetToken;
import com.stormpath.sdk.impl.authc.BasicAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultApplication extends AbstractInstanceResource implements Application {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";
    private static final String TENANT = "tenant";
    private static final String ACCOUNTS = "accounts";
    private static final String PASSWORD_RESET_TOKENS = "passwordResetTokens";

    public DefaultApplication(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultApplication(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getStringProperty(NAME);
    }

    @Override
    public void setName(String name) {
        setProperty(NAME, name);
    }

    @Override
    public String getDescription() {
        return getStringProperty(DESCRIPTION);
    }

    @Override
    public void setDescription(String description) {
        setProperty(DESCRIPTION, description);
    }

    @Override
    public Status getStatus() {
        String value = getStringProperty(STATUS);
        if (value == null) {
            return null;
        }
        return Status.valueOf(value.toUpperCase());
    }

    @Override
    public void setStatus(Status status) {
        setProperty(STATUS, status.name());
    }

    @Override
    public AccountList getAccounts() {
        return getResourceProperty(ACCOUNTS, AccountList.class);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT, Tenant.class);
    }

    @Override
    public PasswordResetToken getPasswordResetToken() {
        return getResourceProperty(PASSWORD_RESET_TOKENS, PasswordResetToken.class);
    }

    @Override
    public PasswordResetToken createPasswordResetToken(String email) {
        String href = getPasswordResetToken().getHref();
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("email", email);
        PasswordResetToken passwordResetToken = getDataStore().instantiate(PasswordResetToken.class, props);
//        passwordResetToken.setEmail(email);
        return getDataStore().create(href, passwordResetToken);
    }

    public PasswordResetToken verifyPasswordResetToken(String token) {
        String href = getPasswordResetToken().getHref();
        href += "/" + token;
        Map<String, Object> props = new LinkedHashMap<String, Object>(1);
        props.put("href", href);
        return getDataStore().instantiate(PasswordResetToken.class, props);
    }

    @Override
    public Account authenticate(AuthenticationRequest request) {
        return new BasicAuthenticator(getDataStore()).authenticate(getHref(), request);
    }
}
