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

    /**
     * Sends a password reset email for the specified account username or email address.
     *
     * @param accountUsernameOrEmail a username or email address of an Account that may login to the application.
     * @return the account corresponding to the specified username or email address.
     * @see #verifyPasswordResetToken(String)
     */
    Account sendPasswordResetEmail(String accountUsernameOrEmail);

    /**
     * Verifies a password reset token in a user-clicked link within an email.  When the user clicks the link in
     * the email, the token is extracted from the link HREF and submitted to this method.  If the token is valid, the
     * Account will be returned and you can set the account's new password and save the account.
     * <p/>
     * Usage Example:
     * <pre>
     * String token = httpServletRequest.getParameter("spToken");
     *
     * Account account = verifyPasswordResetToken(token);
     * account.setPassword(user_submitted_new_password);
     *
     * account.save();
     * </pre>
     *
     * @param token the verification token, usually obtained as a request parameter by your application.
     * @return the Account matching the specified token.
     * @since 0.4
     */
    Account verifyPasswordResetToken(String token);

    Account authenticate(AuthenticationRequest request) throws ResourceException;
}
