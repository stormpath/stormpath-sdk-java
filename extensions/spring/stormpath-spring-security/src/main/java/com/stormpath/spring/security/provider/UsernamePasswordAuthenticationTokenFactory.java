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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * A {@link com.stormpath.spring.security.provider.AuthenticationTokenFactory} implementation that creates representation
 * of a username and password token. The principal stored in the token is an instance of {@link StormpathUserDetails} which
 * contains information about the Stormpath Account for the authenticated user, such as href, given name, username, etc.
 *
 * @since 0.2.0
 */
public class UsernamePasswordAuthenticationTokenFactory implements AuthenticationTokenFactory {

    @Override
    public Authentication createAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Account account) {
        UserDetails userDetails = new StormpathUserDetails(principal.toString(), (String) credentials, authorities, account);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
        return authToken;
    }
}
