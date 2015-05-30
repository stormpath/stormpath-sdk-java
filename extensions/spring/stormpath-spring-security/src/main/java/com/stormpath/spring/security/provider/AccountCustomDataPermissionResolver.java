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
package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.spring.security.authz.permission.Permission;

import java.util.Set;

/**
 * A {@link CustomDataPermissionResolver} implementation that merely delegates lookup logic to the parent
 * class, first calling {@link com.stormpath.sdk.account.Account#getCustomData() account.getCustomData()}.
 *
 * @since 0.2.0
 */
public class AccountCustomDataPermissionResolver extends CustomDataPermissionResolver implements AccountPermissionResolver {

    @Override
    public Set<Permission> resolvePermissions(Account account) {
        return super.getPermissions(account.getCustomData());
    }
}
