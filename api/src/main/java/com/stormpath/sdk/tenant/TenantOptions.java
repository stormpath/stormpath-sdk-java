package com.stormpath.sdk.tenant;

import com.stormpath.sdk.query.Options;

/**
 * @since 0.8
 */
public interface TenantOptions<T> extends Options {

    T expandApplications();

    T expandApplications(int limit);

    T expandApplications(int limit, int offset);

    T expandDirectories();

    T expandDirectories(int limit);

    T expandDirectories(int limit, int offset);
}
