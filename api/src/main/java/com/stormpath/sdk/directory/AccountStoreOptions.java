package com.stormpath.sdk.directory;

import com.stormpath.sdk.query.Options;

/**
 * @since 0.8
 */
public interface AccountStoreOptions<T> extends Options {

    T expandAccounts();

    T expandAccounts(int limit);

    T expandAccounts(int limit, int offset);

    T expandGroups();

    T expandGroups(int limit);

    T expandGroups(int limit, int offset);

    T expandTenant();
}
