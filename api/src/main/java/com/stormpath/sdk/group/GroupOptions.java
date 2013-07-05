package com.stormpath.sdk.group;

import com.stormpath.sdk.query.Options;

/**
 * @since 0.8
 */
public interface GroupOptions<T> extends Options {

    T expandDirectory();

    T expandTenant();

    T expandAccounts();

    T expandAccounts(int limit);

    T expandAccounts(int limit, int offset);

    T expandAccountMemberships();

    T expandAccountMemberships(int limit);

    T expandAccountMemberships(int limit, int offset);
}