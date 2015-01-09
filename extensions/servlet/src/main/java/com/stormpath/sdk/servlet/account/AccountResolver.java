package com.stormpath.sdk.servlet.account;

import com.stormpath.sdk.account.Account;

import javax.servlet.ServletRequest;

public interface AccountResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a {@link DefaultAccountResolver
     * DefaultAccountResolver}.
     */
    public static final AccountResolver INSTANCE = new DefaultAccountResolver();

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
     *
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
     *
     * This <em>check-then-use</em> pattern helps eliminate NullPointerExceptions and conditional branching bugs when
     * working with user identities - often desirable in sensitive logic.
     *
     * @param request the current servlet request.
     * @return the current user account identity associated with the request
     */
    Account getRequiredAccount(ServletRequest request) throws IllegalArgumentException;
}
