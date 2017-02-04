/*
 * Copyright 2014 Stormpath, Inc.
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

import com.stormpath.sdk.directory.AccountStore;

/**
 * A Builder to construct {@link VerificationEmailRequest}s.
 *
 * @see {@link com.stormpath.sdk.application.Applications#verificationEmailBuilder()}
 * @since 1.0.0
 */
public interface VerificationEmailRequestBuilder {

    /**
     * Setter for the account's login information. Either the username or email identifying the desired account can be used.
     *
     * @param usernameOrEmail the username or email identifying the account that will receive the verification email.
     * @return this builder instance for method chaining.
     */
    VerificationEmailRequestBuilder setLogin(String usernameOrEmail);

    /**
     * Setter for the account's organizationNameKey information.
     *
     * @param organizationNameKey the organizationNameKey where the account lives.
     * @return this builder instance for method chaining.
     */
    VerificationEmailRequestBuilder setOrganizationNameKey(String organizationNameKey);

    /**
     * Setter for the {@link com.stormpath.sdk.directory.AccountStore} where the specified account must be searched. Although
     * this is an optional property it should to be provided when the account's AccountStore is already known since it will
     * speed-up the backend execution time.
     *
     * @param accountStore the {@link com.stormpath.sdk.directory.AccountStore} where the account must be searched by the backend.
     * @return this builder instance for method chaining.
     */
    VerificationEmailRequestBuilder setAccountStore(AccountStore accountStore);

    /**
     * Creates a new {@code VerificationEmailRequest} instance based on the current builder state.
     *
     * @return a new {@code VerificationEmailRequest} instance based on the current builder state.
     */
    VerificationEmailRequest build();

}
