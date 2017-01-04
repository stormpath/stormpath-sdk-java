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
package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;

import javax.servlet.ServletRequest;

/**
 * An AccountResolver can inspect a {@link javax.servlet.ServletRequest ServletRequest} and return any
 * {@link com.stormpath.sdk.account.Account Account} that may be associated with the request due to a previous
 * authentication.
 *
 * @since 1.0.RC3
 */
public interface AccountResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a {@link DefaultAccountResolver
     * DefaultAccountResolver}.
     */
    AccountResolver INSTANCE = new DefaultAccountResolver();

    /**
     * Returns {@code true} if the specified request has an associated user account identity, {@code false} otherwise.
     * Often used as a guard/check before executing {@link #getRequiredAccount(javax.servlet.ServletRequest)}.
     *
     * @param request the current servlet request.
     * @return {@code true} if the specified request has an associated user account identity, {@code false} otherwise.
     * @see #getRequiredAccount(javax.servlet.ServletRequest)
     */
    boolean hasAccount(ServletRequest request);

    /**
     * Returns the current user account associated with the specified request or {@code null} if no user account is
     * associated with the request.
     * <p>In security-sensitive workflows, it might be better to use {@link #getRequiredAccount(javax.servlet.ServletRequest)} to help eliminate NullPointerExceptions and conditional branching bugs.</p>
     *
     * @param request the current servlet request.
     * @return the current user account associated with the specified request or {@code null} if no user account is
     * associated with the request.
     * @see #hasAccount(javax.servlet.ServletRequest)
     * @see #getRequiredAccount(javax.servlet.ServletRequest)
     */
    Account getAccount(ServletRequest request);

    /**
     * Returns the current user account identity associated with the request or throws an IllegalArgumentException
     * exception if there is no account associated with the request.
     * <p>
     * <p>Often this method will be used within a conditional, first checking that {@link
     * #hasAccount(javax.servlet.ServletRequest)} returns {@code true}, for example:</p>
     * <pre>
     *     if (AccountResolver.INSTANCE.hasAccount(servletRequest)) {
     *
     *         Account account = AccountResolver.INSTANCE.getRequiredAccount(servletRequest);
     *         //do something with the account
     *
     *     }
     * </pre>
     * <p>
     * This <em>check-then-use</em> pattern helps eliminate NullPointerExceptions and conditional branching bugs when
     * working with user identities - often desirable in sensitive logic.
     *
     * @param request the current servlet request.
     * @return the current user account identity associated with the request
     */
    Account getRequiredAccount(ServletRequest request) throws IllegalArgumentException;
}
