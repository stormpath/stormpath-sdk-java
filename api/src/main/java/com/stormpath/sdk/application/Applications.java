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
package com.stormpath.sdk.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.resource.ReferenceProperty;
import com.stormpath.sdk.resource.StatusProperty;
import com.stormpath.sdk.resource.StringProperty;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.8
 */
public final class Applications {

    public static final StringProperty NAME = new StringProperty("name", true);
    public static final StringProperty DESCRIPTION = new StringProperty("description");
    public static final StatusProperty STATUS = new StatusProperty("status");
    public static final ReferenceProperty<Tenant> TENANT = new ReferenceProperty<Tenant>("tenant", Tenant.class, true, false);
    public static final ReferenceProperty<Account> ACCOUNTS = new ReferenceProperty<Account>("accounts", Account.class, true, true);
    public static final ReferenceProperty<Group> GROUPS = new ReferenceProperty<Group>("groups", Group.class, true, true);

    public static ApplicationOptions options() {
        return (ApplicationOptions) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultApplicationOptions");
    }

    public static ApplicationCriteria criteria() {
        return (ApplicationCriteria) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultApplicationCriteria");
    }

    public static ApplicationCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }
}
