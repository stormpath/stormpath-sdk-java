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
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static org.easymock.EasyMock.*
import static org.hamcrest.core.IsInstanceOf.instanceOf
import static org.junit.Assert.*

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
        expect(account.username).andReturn acctUsername
        expect(account.email).andReturn acctEmail
        expect(account.givenName).andReturn acctGivenName
        expect(account.middleName).andReturn acctMiddleName
        expect(account.surname).andReturn acctSurname
        expect(account.status).andReturn acctStatus times 2
        expect(account.username).andReturn acctUsername

        replay account

        def token = authenticationTokenFactory.createAuthenticationToken("foo", "bar", gas, account)
        assertNotNull token
        assertThat token, instanceOf(UsernamePasswordAuthenticationToken.class)
        assertEquals acctUsername, ((StormpathUserDetails)token.principal).getUsername()
        assertEquals "bar", token.getCredentials()
        assertArrayEquals gas.toArray(), token.getAuthorities().toArray()
        assertEquals gas.size(), token.getAuthorities().size()
        assertEquals acctHref, ((StormpathUserDetails)token.principal).properties.get("href")
        assertEquals acctUsername, ((StormpathUserDetails)token.principal).properties.get("username")
        assertEquals acctEmail, ((StormpathUserDetails)token.principal).properties.get("email")
        assertEquals acctGivenName, ((StormpathUserDetails)token.principal).properties.get("givenName")
        assertEquals acctMiddleName, ((StormpathUserDetails)token.principal).properties.get("middleName")
        assertEquals acctSurname, ((StormpathUserDetails)token.principal).properties.get("surname")
        assertEquals acctStatus.toString(), ((StormpathUserDetails)token.principal).properties.get("status")
        assertTrue(((StormpathUserDetails)token.principal).enabled)
        assertTrue(((StormpathUserDetails)token.principal).accountNonLocked)
        assertTrue(((StormpathUserDetails)token.principal).accountNonExpired)
        assertTrue(((StormpathUserDetails)token.principal).credentialsNonExpired)

        verify account

    }


}
