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

import com.stormpath.sdk.group.Group;
import com.stormpath.spring.security.authz.permission.Permission;

import java.util.Set;

/**
 * A {@code GroupPermissionResolver} inspects a Stormpath {@link Group} and returns that {@code Group}'s assigned
 * {@link Permission}s.
 * <p/>
 * Spring Security will check these permissions to determine whether or not a {@link org.springframework.security.core.Authentication Authentication}
 * associated with the {@code Group} is permitted to do something.
 *
 * @see AccountPermissionResolver
 * @since 0.2.0
 */
public interface GroupPermissionResolver {

    /**
     * Returns a set of {@link Permission Permission}s assigned to a particular Stormpath {@link Group}.
     * <p/>
     * Spring Security will check these permissions to determine whether or not a {@link org.springframework.security.core.Authentication Authentication}
     * associated with the {@code Group} is permitted to do something.
     *
     * @param group the Stormpath {@code Group} to inspect to return its assigned Permissions.
     * @return a set of {@link Permission Permission}s assigned to the group, to be used by Spring Security for runtime
     *         permission checks.
     * @see AccountPermissionResolver
     */
    Set<Permission> resolvePermissions(Group group);
}
