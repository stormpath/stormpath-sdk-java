package com.stormpath.sdk.account;

import com.stormpath.sdk.query.Options;

/**
 * @since 0.8
 */
public interface AccountOptions<T> extends Options {

    T expandDirectory();

    T expandTenant();

    T expandGroups();

    T expandGroups(int limit);

    T expandGroups(int limit, int offset);

    T expandGroupMemberships();

    T expandGroupMemberships(int limit);

    T expandGroupMemberships(int limit, int offset);

}
