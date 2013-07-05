package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.DirectoryOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultDirectoryOptions extends DefaultOptions<DirectoryOptions> implements DirectoryOptions {

    public DirectoryOptions expandAccounts() {
        return expand(Directories.ACCOUNTS);
    }

    public DirectoryOptions expandAccounts(int limit) {
        return expand(Directories.ACCOUNTS, limit);
    }

    public DirectoryOptions expandAccounts(int limit, int offset) {
        return expand(Directories.ACCOUNTS, limit, offset);
    }

    public DirectoryOptions expandGroups() {
        return expand(Directories.GROUPS);
    }

    public DirectoryOptions expandGroups(int limit) {
        return expand(Directories.GROUPS, limit);
    }

    public DirectoryOptions expandGroups(int limit, int offset) {
        return expand(Directories.GROUPS, limit, offset);
    }

    public DirectoryOptions expandTenant() {
        return expand(Directories.TENANT);
    }
}
