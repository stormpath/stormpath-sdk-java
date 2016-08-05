package com.stormpath.sdk.account;

/**
 * Represents an attempt to create a new {@link com.stormpath.sdk.account.AccountLink} record in Stormpath.
 *
 */
public interface CreateAccountLinkRequest {

    /**
     * Returns the AccountLink instance for which a new record will be created in Stormpath.
     *
     * @return the AccountLink instance for which a new record will be created in Stormpath.
     */
    AccountLink getAccountLink(); // TODO : Is this required?

    /**
     * Returns the leftAccount href for which a new AccountLink will be created (with rightAccount) in Stormpath.
     *
     * @return the leftAccount href for which a new AccountLink will be created in Stormpath.
     */
    String getLeftAccountHref();

    /**
     * Returns the rightAccount href for which a new AccountLink will be created (with leftAccount) in Stormpath.
     *
     * @return the rightAccount href for which a new AccountLink will be created in Stormpath.
     */
    String getRightAccountHref();

    /**
     * Returns {@code true} if the the request reflects that the CreateAccountLink (POST) message will be send with
     * URL query parameters to retrieve the accountLink's references as part of Stormpath's response upon successful
     * accountLink creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getAccountLinkOptions()} method.
     *
     * @return {@code true} if the the request reflects that the CreateAccountLink (POST) message will be send with
     *         URL query parameters to retrieve the expanded references in the Stormpath's response upon successful
     *         accountLink creation.
     */
    boolean isAccountLinkOptionsSpecified();

    /**
     * Returns the {@code AccountLinkOptions} to be used in the CreateAccountLinkRequest s to retrieve the accountLink's
     * references as part of Stormpath's response upon successful accountLink creation.
     * <p/>
     * Always call the {@link #isAccountLinkOptionsSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return {@link AccountLinkOptions} to be used in the CreateAccountLinkRequest to retrieve the accountLink's
     *         references as part of Stormpath's response upon successful accountLink creation.
     *
     * @throws IllegalStateException if this method is called but {@link #isAccountLinkOptionsSpecified()} is {@code false}.
     */
    AccountLinkOptions getAccountLinkOptions() throws IllegalStateException;
}
