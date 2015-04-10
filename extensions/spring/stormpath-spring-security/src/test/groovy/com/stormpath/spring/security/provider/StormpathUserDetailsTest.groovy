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
package com.stormpath.spring.security.provider

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import org.junit.Test
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import static org.easymock.EasyMock.*
import static org.junit.Assert.assertTrue

class StormpathUserDetailsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testUserNotNull(){
        def account = createMock(Account)
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        new StormpathUserDetails(null, "psswd", grantedAuthorities, account);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrantedAuthoritiesNotNull(){
        def account = createMock(Account)
        new StormpathUserDetails("username", "psswd", null, account);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAccountNotNull(){
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        new StormpathUserDetails("username", "psswd", grantedAuthorities, null);
    }

    @Test
    public void test(){
        def account = createMock(Account)

        def acctUsername = 'jsmith'
        def acctHref = 'https://api.stormpath.com/v1/accounts/123'
        def acctEmail = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'
        def acctStatus = AccountStatus.ENABLED

        expect(account.href).andReturn acctHref
        expect(account.username).andReturn acctUsername times 2
        expect(account.email).andReturn acctEmail
        expect(account.givenName).andReturn acctGivenName
        expect(account.middleName).andReturn acctMiddleName
        expect(account.surname).andReturn acctSurname
        expect(account.getStatus()).andReturn acctStatus times 2

        replay account

        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        UserDetails userDetails = new StormpathUserDetails("username", "psswd", grantedAuthorities, account);
        assertTrue(userDetails.accountNonExpired)
        assertTrue(userDetails.accountNonLocked)
        assertTrue(userDetails.credentialsNonExpired)

        verify account
    }

}
