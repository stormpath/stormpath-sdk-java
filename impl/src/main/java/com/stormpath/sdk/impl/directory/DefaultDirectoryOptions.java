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
