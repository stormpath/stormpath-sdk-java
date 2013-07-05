package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultApplicationOptions extends DefaultOptions<ApplicationOptions> implements ApplicationOptions {

    public ApplicationOptions expandAccounts() {
        return expand(Applications.ACCOUNTS);
    }

    public ApplicationOptions expandAccounts(int limit) {
        return expand(Applications.ACCOUNTS, limit);
    }

    public ApplicationOptions expandAccounts(int limit, int offset) {
        return expand(Applications.ACCOUNTS, limit, offset);
    }

    public ApplicationOptions expandGroups() {
        return expand(Applications.GROUPS);
    }

    public ApplicationOptions expandGroups(int limit) {
        return expand(Applications.GROUPS, limit);
    }

    public ApplicationOptions expandGroups(int limit, int offset) {
        return expand(Applications.GROUPS, limit, offset);
    }

    public ApplicationOptions expandTenant() {
        return expand(Applications.TENANT);
    }
}
