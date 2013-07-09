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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.impl.query.DefaultOptions;

/**
 * @since 0.8
 */
public class DefaultApplicationOptions extends DefaultOptions<ApplicationOptions> implements ApplicationOptions {

    public ApplicationOptions expandAccounts() {
        return expand(DefaultApplication.ACCOUNTS);
    }

    public ApplicationOptions expandAccounts(int limit) {
        return expand(DefaultApplication.ACCOUNTS, limit);
    }

    public ApplicationOptions expandAccounts(int limit, int offset) {
        return expand(DefaultApplication.ACCOUNTS, limit, offset);
    }

    public ApplicationOptions expandGroups() {
        return expand(DefaultApplication.GROUPS);
    }

    public ApplicationOptions expandGroups(int limit) {
        return expand(DefaultApplication.GROUPS, limit);
    }

    public ApplicationOptions expandGroups(int limit, int offset) {
        return expand(DefaultApplication.GROUPS, limit, offset);
    }

    public ApplicationOptions expandTenant() {
        return expand(DefaultApplication.TENANT);
    }
}
