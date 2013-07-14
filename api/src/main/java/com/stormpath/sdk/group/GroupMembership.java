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
package com.stormpath.sdk.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;

/**
 * A {@code GroupMembership} represents the association of an {@link Account} and a {@link Group}.
 * <p/>
 * {@link #delete() Deleting} this resource will only delete the association - it will not delete either the
 * {@code Account} or {@code Group}.
 *
 * @since 0.4
 */
public interface GroupMembership extends Resource, Deletable {

    /**
     * Returns this membership's {@link Account} resource.
     *
     * @return this membership's {@link Account} resource.
     */
    Account getAccount();

    /**
     * Returns this membership's {@link Group} resource.
     *
     * @return this membership's {@link Group} resource.
     */
    Group getGroup();
}
