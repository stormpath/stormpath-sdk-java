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
package com.stormpath.sdk.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.1
 */
public interface Application extends Resource, Saveable {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Status getStatus();

    void setStatus(Status status);

    AccountList getAccounts();

    Tenant getTenant();

    PasswordResetToken getPasswordResetToken();

    PasswordResetToken createPasswordResetToken(String email);

    PasswordResetToken verifyPasswordResetToken(String token);

    Account authenticate(AuthenticationRequest request) throws ResourceException;
}
