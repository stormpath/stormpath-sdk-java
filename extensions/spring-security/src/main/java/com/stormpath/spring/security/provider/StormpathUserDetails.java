/*
 * Copyright 2014 Stormpath, Inc.
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
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.spring.security.util.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Models Stormpath account information retrieved by a {@link StormpathAuthenticationProvider}.
 * <p>
 * Note that this implementation is immutable.
 *
 * @since 0.2.0
 */
public class StormpathUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, String> stormpathUserDetails = null;

    public StormpathUserDetails(String username, String password, Collection<? extends GrantedAuthority> grantedAuthorities, Account account) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        if (grantedAuthorities == null) {
            throw new IllegalArgumentException("Granted authorities cannot be null.");
        }
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        this.password = password;
        this.stormpathUserDetails = createUnmodifiableMap(account);
        this.username = account.getUsername();
        this.authorities = grantedAuthorities;
    }

    public Map<String, String> getProperties() {
        return this.stormpathUserDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return AccountStatus.ENABLED.toString().equals(this.stormpathUserDetails.get("status"));
    }

    private Map<String, String> createUnmodifiableMap(Account account) {
        Map<String, String> props = new HashMap<String, String>();
        props.put("href", account.getHref());
        nullSafePut(props, "username", account.getUsername());
        nullSafePut(props, "email", account.getEmail());
        nullSafePut(props, "givenName", account.getGivenName());
        nullSafePut(props, "middleName", account.getMiddleName());
        nullSafePut(props, "surname", account.getSurname());
        if(account.getStatus() != null) {
            nullSafePut(props, "status", account.getStatus().toString());
        }
        return Collections.unmodifiableMap(props);
    }

    private void nullSafePut(Map<String, String> props, String propName, String value) {
        value = StringUtils.clean(value);
        if (value != null) {
            props.put(propName, value);
        }
    }

}
