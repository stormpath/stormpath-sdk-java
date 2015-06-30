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
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * An {@code AccountGrantedAuthorityResolver} inspects a Stormpath {@link Account} and returns that {@code Account}'s
 * directly assigned {@link org.springframework.security.core.GrantedAuthority}s.
 * <p/>
 * Note that this interface is for resolving granted authorities that are directly assigned to an Account. Granted authorities
 * that are assigned to an account's groups (and therefore implicitly associated with an Account), would be resolved
 * instead by a {@link GroupGrantedAuthorityResolver} instance.
 * <p/>
 * Spring Security checks these granted authorities (in addition to any assigned groups' granted authorities) to determine whether or not an
 * {@link org.springframework.security.core.Authentication Authentication} representing the {@code Account} is permitted to do something.
 *
 * @see GroupGrantedAuthorityResolver
 */
public interface AccountGrantedAuthorityResolver {

    /**
     * Returns a set of {@link org.springframework.security.core.GrantedAuthority GrantedAuthority}s assigned to a particular Stormpath
     * {@link Account}.
     * <p/>
     * Note that method is for resolving granted authorities that are directly assigned to an Account. Granted authorities
     * that are assigned to an account's groups (and therefore implicitly associated with an Account), would be resolved
     * instead by a {@link GroupGrantedAuthorityResolver} instance.
     * <p/>
     * Spring Security checks these granted authorities to determine whether or not an {@link org.springframework.security.core.Authentication Authentication}
     * representing the {@code Account} is permitted to do something.
     *
     * @param account the Stormpath {@code Account} to inspect to return its directly assigned Spring Security granted authorities.
     * @return a set of Spring Security {@link org.springframework.security.core.GrantedAuthority GrantedAuthority}s assigned to the account, to be
     *         used by Spring Security for runtime authorization checks.
     * @see GroupGrantedAuthorityResolver
     */
    Set<GrantedAuthority> resolveGrantedAuthorities(Account account);
}
