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
 * A Builder to construct {@link com.stormpath.sdk.account.CreateAccountRequest}s.
 *
 * @see com.stormpath.sdk.application.Application#createAccount(com.stormpath.sdk.account.CreateAccountRequest)
 * @since 0.9
 */
public interface CreateAccountRequestBuilder {

    /**
     * Directive to explicitly overwrite the registration workflow of the Login Source for new Accounts.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code true}, the account registration workflow will be triggered
     * no matter what the Login Source configuration is.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code false}, the account registration workflow will <b>NOT</b>
     * be triggered, no matter what the Login Source configuration is.
     * </p>
     * If you want to ensure the registration workflow behavior matches the Login Source default, just do not call this
     * method.
     * <p/>
     *
     * @param registrationWorkflowEnabled whether or not the account registration workflow will be triggered, no matter
     *                                    what the Login Source configuration is.
     * @return the builder instance for method chaining.
     */
    CreateAccountRequestBuilder setRegistrationWorkflowEnabled(boolean registrationWorkflowEnabled);

    /**
     * Ensures that after a Account is created, the Account's {@link Account#getCustomData() customData} is also
     * retrieved in the same successful creation response. This enhances performance by leveraging a single
     * request to retrieve multiple related resources you know you will use.
     *
     * @return the builder instance for method chaining.
     */
    CreateAccountRequestBuilder withCustomData();

    /**
     * Creates a new {@code CreateAccountRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateAccountRequest} instance based on the current builder state.
     */
    CreateAccountRequest build();
}
