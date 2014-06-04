package com.stormpath.sdk.account;

/**
 * Represents the result of the passing an HttpRequest that contains the
 *
 * @see com.stormpath.sdk.application.Application#handleSsoResponse(Object)
 * @since 1.0.RC
 */
public interface AccountResult {

    /**
     * Getter for the {@link Account} Resource containing a user in Stormpath.
     *
     * @return the {@link Account} Resource containing a user in Stormpath.
     */
    Account getAccount();

    /**
     * If this request generated a new Account in Stormpath, this method will return `true`.
     *
     * @return <code>true</code> if a new {@link Account} was generated in Stormpath as result of the request; <code>false</code> otherwise.
     */
    boolean isNewAccount();


    /**
     * Getter for the {@code state}
     *
     * @return the state passed during the initial
     */
    String getState();

}
