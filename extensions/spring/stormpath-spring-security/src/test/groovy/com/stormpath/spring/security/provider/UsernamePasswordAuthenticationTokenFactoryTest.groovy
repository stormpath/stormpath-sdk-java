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

package com.stormpath.spring.security.provider

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsInstanceOf.instanceOf
import static org.testng.Assert.*
import static org.testng.AssertJUnit.assertArrayEquals

class UsernamePasswordAuthenticationTokenFactoryTest {

    @Test
    void testSetAuthenticationTokenFactory() {

        def acctHref = 'https://api.stormpath.com/v1/accounts/123'
        def acctUsername = 'jsmith'
        def acctEmail = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'
        def acctStatus = AccountStatus.ENABLED

        def authenticationTokenFactory = new UsernamePasswordAuthenticationTokenFactory()
        def grantedAuthorityA = new SimpleGrantedAuthority("ROLE_A");
        def grantedAuthorityB = new SimpleGrantedAuthority("ROLE_B");
        def Set<GrantedAuthority> gas = new HashSet<>(2);
        gas.add(grantedAuthorityA)
        gas.add(grantedAuthorityB)
        Collection<? extends GrantedAuthority> authorities =  new ArrayList<? extends GrantedAuthority>();
        def account = createStrictMock(Account)

        expect(account.href).andReturn acctHref

        replay account

        def token = authenticationTokenFactory.createAuthenticationToken("foo", "bar", gas, account)
        assertNotNull token
        assertThat token, instanceOf(UsernamePasswordAuthenticationToken.class)
        assertEquals acctHref, ((UserDetails)token.principal).getUsername()
        assertEquals "bar", token.getCredentials()
        assertArrayEquals gas.toArray(), token.getAuthorities().toArray()
        assertEquals gas.size(), token.getAuthorities().size()
        assertTrue(((UserDetails)token.principal).enabled)
        assertTrue(((UserDetails)token.principal).accountNonLocked)
        assertTrue(((UserDetails)token.principal).accountNonExpired)
        assertTrue(((UserDetails)token.principal).credentialsNonExpired)

        verify account

    }


}
