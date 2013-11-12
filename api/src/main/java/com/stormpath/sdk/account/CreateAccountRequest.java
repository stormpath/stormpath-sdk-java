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
package com.stormpath.sdk.account;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.account.Account} record in Stormpath.
 *
 * @see com.stormpath.sdk.application.Application#createAccount(com.stormpath.sdk.account.CreateAccountRequest)
 * @since 0.9
 */
public interface CreateAccountRequest {

    /**
     * Returns the Account instance for which a new record will be created in Stormpath.
     *
     * @return the Account instance for which a new record will be created in Stormpath.
     */
    Account getAccount();

    /**
     * Returns {@code true} if the the request reflects that the backing directory's registration workflow
     * should be explicitly enabled or disabled, {@code false} if the request should reflect the backing directory's
     * default behavior.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #isRegistrationWorkflowEnabled()} method.
     *
     * @return {@code true} if the the request reflects that the backing directory's registration workflow
     *         should be explicitly enabled or disabled, {@code false} if the request should reflect the backing directory's
     *         default behavior.
     */
    boolean isRegistrationWorkflowOptionSpecified();

    /**
     * Returns {@code true} if the backing directory's registration workflow must execute, {@code false} if the
     * backing directory's registration workflow must not execute.  This value explicitly overrides whatever
     * the directory might have as a default.
     * <p/>
     * Always call the {@link #isRegistrationWorkflowOptionSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return {@code true} if the backing directory's registration workflow must execute, {@code false} if the
     *         backing directory's registration workflow must not execute.
     * @throws IllegalStateException if this method is called but {@link #isRegistrationWorkflowOptionSpecified()} is {@code false}.
     */
    boolean isRegistrationWorkflowEnabled() throws IllegalStateException;

}
