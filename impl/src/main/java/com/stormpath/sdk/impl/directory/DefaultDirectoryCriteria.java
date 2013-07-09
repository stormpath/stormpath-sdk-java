/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.directory;

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
        return orderBy(DefaultDirectory.NAME);
    }

    @Override
    public DirectoryCriteria orderByDescription() {
        return orderBy(DefaultDirectory.DESCRIPTION);
    }

    @Override
    public DirectoryCriteria orderByStatus() {
        return orderBy(DefaultDirectory.STATUS);
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
