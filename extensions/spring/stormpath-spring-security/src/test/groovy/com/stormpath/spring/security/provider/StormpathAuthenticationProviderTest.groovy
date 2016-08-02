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
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.spring.security.token.ProviderAuthenticationToken
import org.easymock.IAnswer
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.getCurrentArguments
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class StormpathAuthenticationProviderTest {

    StormpathAuthenticationProvider authenticationProvider

    //Test account attributes
    def username = 'myUsername'
    def password = 'secret'
    def acctUsername = 'jsmith'
    def acctHref = 'https://api.stormpath.com/v1/accounts/123'
    def acctEmail = 'jsmith@foo.com'
    def acctGivenName = 'John'
    def acctMiddleName = 'A'
    def acctSurname = 'Smith'
    def acctStatus = AccountStatus.ENABLED

    Application application

    @BeforeMethod
    void setUp() {
        application = createStrictMock(Application)
        authenticationProvider = new StormpathAuthenticationProvider(application)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testInitWithNullApplication() {
        new StormpathAuthenticationProvider(null)
    }

    void expectAccountBasicAttributes(Account mockAccount) {
        expect(mockAccount.href).andReturn acctHref
    }

    @Test
    void testDoGetAuthenticationInfoDisableGroup() {
        def groupStatus = GroupStatus.DISABLED
        Set<String> groupSpringSecurityGrantedAuthorities = new HashSet<>();
        groupSpringSecurityGrantedAuthorities.add("groupSpringSecurityPermissionsItem");
        Set<String> accountSpringSecurityGrantedAuthorities = new HashSet<>();
        accountSpringSecurityGrantedAuthorities.add("accountSpringSecurityPermissionsItem");

        def authentication = createStrictMock(UsernamePasswordAuthenticationToken)
        def authenticationResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)
        def groupList = createStrictMock(GroupList)
        def iterator = createStrictMock(Iterator)
        def group = createStrictMock(Group)
        def groupCustomData = createStrictMock(CustomData)
        def accountCustomData = createStrictMock(CustomData)
        def accountGrantedAuthorityResolver = createStrictMock(AccountGrantedAuthorityResolver)
        def groupGrantedAuthorityResolver = createStrictMock(GroupGrantedAuthorityResolver)
        def accountGrantedAuthority = createStrictMock(GrantedAuthority)

        def accountGrantedAuthoritySet = new HashSet<GrantedAuthority>()
        accountGrantedAuthoritySet.add(accountGrantedAuthority)

        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(accountGrantedAuthority.getAuthority()).andReturn("").times(2)
        expect(application.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer(new IAnswer<AuthenticationResult>() {
            AuthenticationResult answer() throws Throwable {
                def authcRequest = getCurrentArguments()[0] as AuthenticationRequest

                assertEquals username, authcRequest.principals
                assertTrue Arrays.equals(password.toCharArray(), authcRequest.credentials as char[])

                return authenticationResult
            }
        })
        expect(group.status).andReturn groupStatus
        expect(authenticationResult.account).andReturn account
        expect(account.groups).andReturn groupList
        expect(account.customData).andReturn accountCustomData
        expect(accountCustomData.get("springSecurityPermissions")).andReturn accountSpringSecurityGrantedAuthorities
        expectAccountBasicAttributes(account)
        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(groupList.iterator()).andReturn iterator
        expect(iterator.hasNext()).andReturn true
        expect(iterator.next()).andReturn group
        expect(iterator.hasNext()).andReturn false
        expect(accountGrantedAuthorityResolver.resolveGrantedAuthorities(account)).andReturn accountGrantedAuthoritySet

        replay authentication, application, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, accountGrantedAuthority

        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver

        Authentication info = authenticationProvider.authenticate(authentication)

        assertTrue info instanceof UsernamePasswordAuthenticationToken
        assertTrue info.authenticated

        assertEquals acctHref, ((UserDetails) info.principal).username
        assertEquals password, ((UserDetails) info.principal).password
        assertEquals 2, info.authorities.size()
        assertTrue info.authorities.contains(accountGrantedAuthority)
        assertTrue(((UserDetails) info.principal).enabled)
        assertTrue(((UserDetails) info.principal).accountNonLocked)
        assertTrue(((UserDetails) info.principal).accountNonExpired)
        assertTrue(((UserDetails) info.principal).credentialsNonExpired)

        verify authentication, application, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, accountGrantedAuthority
    }

    @Test
    void testDoGetAuthenticationInfoSuccess() {
        def groupStatus = GroupStatus.ENABLED
        Set<String> groupSpringSecurityGrantedAuthorities = new HashSet<>();
        groupSpringSecurityGrantedAuthorities.add("groupSpringSecurityPermissionsItem");
        Set<String> accountSpringSecurityGrantedAuthorities = new HashSet<>();
        accountSpringSecurityGrantedAuthorities.add("accountSpringSecurityPermissionsItem");

        def authentication = createStrictMock(UsernamePasswordAuthenticationToken)
        def authenticationResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)
        def groupList = createStrictMock(GroupList)
        def iterator = createStrictMock(Iterator)
        def group = createStrictMock(Group)
        def groupCustomData = createStrictMock(CustomData)
        def accountCustomData = createStrictMock(CustomData)
        def accountGrantedAuthorityResolver = createStrictMock(AccountGrantedAuthorityResolver)
        def groupGrantedAuthorityResolver = createStrictMock(GroupGrantedAuthorityResolver)
        def groupGrantedAuthority = createStrictMock(GrantedAuthority)
        def accountGrantedAuthority = createStrictMock(GrantedAuthority)
        def groupGrantedAuthoritySet = new HashSet<GrantedAuthority>()
        groupGrantedAuthoritySet.add(groupGrantedAuthority)
        def accountGrantedAuthoritySet = new HashSet<GrantedAuthority>()
        accountGrantedAuthoritySet.add(accountGrantedAuthority)

        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(accountGrantedAuthority.getAuthority()).andReturn("").times(2)
        expect(groupGrantedAuthority.getAuthority()).andReturn("").times(2)
        expect(application.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer(new IAnswer<AuthenticationResult>() {
            AuthenticationResult answer() throws Throwable {
                def authcRequest = getCurrentArguments()[0] as AuthenticationRequest

                assertEquals username, authcRequest.principals
                assertTrue Arrays.equals(password.toCharArray(), authcRequest.credentials as char[])

                return authenticationResult
            }
        })
        expect(authenticationResult.account).andReturn account
        expect(account.groups).andReturn groupList
        expect(group.status).andReturn groupStatus
        expect(group.customData).andReturn groupCustomData
        expect(groupCustomData.get("springSecurityPermissions")).andReturn groupSpringSecurityGrantedAuthorities
        expect(account.customData).andReturn accountCustomData
        expect(accountCustomData.get("springSecurityPermissions")).andReturn accountSpringSecurityGrantedAuthorities
        expectAccountBasicAttributes(account)
        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(groupList.iterator()).andReturn iterator
        expect(iterator.hasNext()).andReturn true
        expect(iterator.next()).andReturn group
        expect(groupGrantedAuthorityResolver.resolveGrantedAuthorities(group)).andReturn groupGrantedAuthoritySet
        expect(iterator.hasNext()).andReturn false
        expect(accountGrantedAuthorityResolver.resolveGrantedAuthorities(account)).andReturn accountGrantedAuthoritySet

        replay authentication, application, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, groupGrantedAuthority, accountGrantedAuthority

        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver

        Authentication info = authenticationProvider.authenticate(authentication)

        assertTrue info instanceof UsernamePasswordAuthenticationToken
        assertTrue info.authenticated

        assertEquals acctUsername, ((UserDetails) info.principal).username
        assertEquals null, ((UserDetails) info.principal).password
        assertEquals 4, info.authorities.size()
        assertTrue info.authorities.contains(groupGrantedAuthority)
        assertTrue info.authorities.contains(accountGrantedAuthority)
        assertEquals acctHref, info.principal
        assertTrue(((UserDetails) info.principal).enabled)
        assertTrue(((UserDetails) info.principal).accountNonLocked)
        assertTrue(((UserDetails) info.principal).accountNonExpired)
        assertTrue(((UserDetails) info.principal).credentialsNonExpired)

        verify authentication, application, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, groupGrantedAuthority, accountGrantedAuthority
    }

    @Test(expectedExceptions = AuthenticationServiceException)
    void testAuthenticateException() {
        int status = 400
        int code = 400
        def msg = 'Invalid username or password.'
        def devMsg = 'Invalid username or password.'
        def moreInfo = 'mailto:support@stormpath.com'

        def error = new SimpleError(status: status, code: code, message: msg, developerMessage: devMsg, moreInfo: moreInfo)

        expect(application.authenticateAccount(anyObject() as AuthenticationRequest)).andThrow(new com.stormpath.sdk.resource.ResourceException(error))

        replay application

        def token = new UsernamePasswordAuthenticationToken('foo', 'bar')
        try {
            authenticationProvider.authenticate(token)
        }
        finally {
            verify application
        }
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInvalidAuthenticationTokenFactory() {
        authenticationProvider.authenticationTokenFactory = null
    }

    @Test
    void testSetAuthenticationTokenFactory() {
        def authenticationTokenFactory = new UsernamePasswordAuthenticationTokenFactory()
        authenticationProvider.authenticationTokenFactory = authenticationTokenFactory
        assertEquals authenticationTokenFactory, authenticationProvider.authenticationTokenFactory
    }

    @Test
    void testSetAccountGrantedAuthorityResolver() {
        def accountGrantedAuthorityResolver = createNiceMock(AccountGrantedAuthorityResolver)
        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        assertEquals accountGrantedAuthorityResolver, authenticationProvider.accountGrantedAuthorityResolver
    }

    @Test
    void testSetGroupGrantedAuthorityResolver() {
        def groupGrantedAuthorityResolver = createNiceMock(GroupGrantedAuthorityResolver)
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver
        assertEquals groupGrantedAuthorityResolver, authenticationProvider.groupGrantedAuthorityResolver
    }

    @Test
    void testSupports() {
        assertTrue authenticationProvider.supports(UsernamePasswordAuthenticationToken)
        assertTrue authenticationProvider.supports(ProviderAuthenticationToken)
    }

}
