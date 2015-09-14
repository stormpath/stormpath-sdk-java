/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.group.Group;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Set;

/**
 * @since 1.0.RC5
 */
public class CustomTestGroupGrantedAuthorityResolver implements GroupGrantedAuthorityResolver {

    @Override
    public Set<GrantedAuthority> resolveGrantedAuthorities(Group group) {
        return Collections.EMPTY_SET;
    }
}
