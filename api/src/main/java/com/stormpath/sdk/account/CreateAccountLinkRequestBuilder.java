package com.stormpath.sdk.account;

/**
 * A Builder to construct {@link com.stormpath.sdk.account.CreateAccountLinkRequest}s.
 *
 */
public interface CreateAccountLinkRequestBuilder {

    /**
     * Set the leftAccount
     *
     * @param leftAccountHref of the Account Link
     * @return the builder instance for method chaining.
     */
    CreateAccountLinkRequestBuilder withLeftAccount(String leftAccountHref);

    /**
     * Set the rightAccount
     *
     * @param rightAccountHref of the Account Link
     * @return the builder instance for method chaining.
     */
    CreateAccountLinkRequestBuilder withRightAccount(String rightAccountHref);

    /**
     * Ensures that after an AccountLink is created, the creation response is retrieved with the specified accountLink's
     * options. This enhances performance by leveraging a single request to retrieve multiple related
     * resources you know you will use.
     *
     * @return the builder instance for method chaining.
     * @throws IllegalArgumentException if {@code options} is null.
     */
    CreateAccountLinkRequestBuilder withResponseOptions(AccountLinkOptions options) throws IllegalArgumentException;

    /**
     * Creates a new {@code CreateAccountLinkRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateAccountLinkRequest} instance based on the current builder state.
     */
    CreateAccountLinkRequest build();
}
