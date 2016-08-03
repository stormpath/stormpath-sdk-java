package com.stormpath.sdk.account;

import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;

/**
 * A {@code AccountLink} represents the link between 2 accounts {@link Account}
 * <p/>
 * {@link #delete() Deleting} this resource will only delete the association - it will not delete
 * the accounts {@code Account}
 *
 * @since 0.4
 */
public interface AccountLink extends Resource, Deletable {

    /**
     * Returns the leftAccount {@link Account} resource.
     *
     * @return the leftAccount {@link Account} resource.
     */
    Account getleftAccount();

    /**
     * Returns the rightAccount {@link Account} resource.
     *
     * @return the rightAccount {@link Account} resource.
     */
    Account getRightAccount();
}
