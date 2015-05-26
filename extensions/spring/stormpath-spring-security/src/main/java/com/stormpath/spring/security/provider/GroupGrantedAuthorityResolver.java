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
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * A {@code GroupGrantedAuthorityResolver} inspects a Stormpath {@link Group} and returns that {@code Group}'s assigned
 * {@link org.springframework.security.core.GrantedAuthority}s.
 * <p/>
 * Spring Security checks these granted authorities to determine whether or not an {@link org.springframework.security.core.Authentication Authentication}
 * associated with the {@code Group} is permitted to do something.
 *
 * @see com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver
 */
public interface GroupGrantedAuthorityResolver {

    /**
     * Returns a set of {@link org.springframework.security.core.GrantedAuthority GrantedAuthority}s assigned to a particular Stormpath {@link Group}.
     * <p/>
     * Spring Security checks these granted authorities to determine whether or not an {@link org.springframework.security.core.Authentication Authentication}
     * associated with the {@code Group} is permitted to do something.
     *
     * @param group the Stormpath {@code Group} to inspect to return its assigned Spring Security granted authorities.
     * @return a set of Spring Security {@link org.springframework.security.core.GrantedAuthority GrantedAuthority}s assigned to the
     *          group, to be used by Spring Security for runtime authorization checks.
     * @see com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver
     */
    Set<GrantedAuthority> resolveGrantedAuthorities(Group group);

}
