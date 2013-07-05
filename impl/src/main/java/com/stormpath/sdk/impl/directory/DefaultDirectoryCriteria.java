package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.directory.DirectoryOptions;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultDirectoryCriteria extends DefaultCriteria<DirectoryCriteria, DirectoryOptions> implements DirectoryCriteria {

    public DefaultDirectoryCriteria() {
        super(new DefaultDirectoryOptions());
    }

    @Override
    public DirectoryCriteria orderByName() {
        return orderBy(Directories.NAME);
    }

    @Override
    public DirectoryCriteria orderByDescription() {
        return orderBy(Directories.DESCRIPTION);
    }

    @Override
    public DirectoryCriteria orderByStatus() {
        return orderBy(Directories.STATUS);
    }

    public DirectoryCriteria expandAccounts() {
        getOptions().expandAccounts();
        return this;
    }

    public DirectoryCriteria expandAccounts(int limit) {
        getOptions().expandAccounts(limit);
        return this;
    }

    public DirectoryCriteria expandAccounts(int limit, int offset) {
        getOptions().expandAccounts(limit, offset);
        return this;
    }

    public DirectoryCriteria expandGroups() {
        getOptions().expandGroups();
        return this;
    }

    public DirectoryCriteria expandGroups(int limit) {
        getOptions().expandGroups(limit);
        return this;
    }

    public DirectoryCriteria expandGroups(int limit, int offset) {
        getOptions().expandGroups(limit, offset);
        return this;
    }

    public DirectoryCriteria expandTenant() {
        getOptions().expandTenant();
        return this;
    }
}
