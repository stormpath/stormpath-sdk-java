package com.stormpath.sdk.account;

import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;

import java.util.Date;

/**
 * A {@code AccountLink} represents the link between 2 {@link Account}s
 * <p/>
 * {@link #delete() Deleting} this resource will only delete the association - it will not delete
 * the accounts themselves {@code Account}
 *
 * @since 1.1.0
 */
public interface AccountLink extends Resource, Deletable {

    /**
     * Returns the leftAccount {@link Account} resource.
     *
     * @return the leftAccount {@link Account} resource.
     */
    Account getLeftAccount();

    /**
     * Returns the rightAccount {@link Account} resource.
     *
     * @return the rightAccount {@link Account} resource.
     */
    Account getRightAccount();

    /**
     * Returns the rightAccount {@link Account} resource.
     *
     * @return the rightAccount {@link Account} resource.
     */
    Date getCreatedAt();
}
