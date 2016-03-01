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
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.oauth.AccessTokenResult
import com.stormpath.spring.security.token.ProviderAuthenticationToken
import org.easymock.IAnswer
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.eq
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.getCurrentArguments
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.same
import static org.easymock.EasyMock.verify
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class StormpathAuthenticationProviderTest {

    StormpathAuthenticationProvider authenticationProvider
    Client client
    String applicationUrl = 'https://api.stormpath.com/v1/applications/foo'

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

    @BeforeMethod
    void setUp() {
        client = createStrictMock(Client)
        authenticationProvider = new StormpathAuthenticationProvider(client, applicationUrl)
    }

    @Test(expectedExceptions = [IllegalArgumentException])
    void testInitWithNullClient() {
        new StormpathAuthenticationProvider(null, "https://somthing")
    }

    @DataProvider(name = "invalidApplicationUrl")
    public Object[][] createInvalidApplicationUrls() {
        [[null], [""]];
    }

    @Test(expectedExceptions = [IllegalArgumentException], dataProvider = "invalidApplicationUrl")
    void testInitWithNullApplicationUrl(String url) {
        new StormpathAuthenticationProvider(createStrictMock(Client), url)
    }

    void expectAccountBasicAttributes(Account mockAccount) {
        expect(mockAccount.href).andReturn acctHref
        expect(mockAccount.username).andReturn acctUsername
        expect(mockAccount.email).andReturn acctEmail
        expect(mockAccount.givenName).andReturn acctGivenName
        expect(mockAccount.middleName).andReturn acctMiddleName
        expect(mockAccount.surname).andReturn acctSurname
        expect(mockAccount.getStatus()).andReturn acctStatus times 2
        expect(mockAccount.username).andReturn acctUsername
    }

    @Test
    void testDoGetAuthenticationInfoDisableGroup() {
        def groupStatus = GroupStatus.DISABLED
        Set<String> groupSpringSecurityGrantedAuthorities = new HashSet<>();
        groupSpringSecurityGrantedAuthorities.add("groupSpringSecurityPermissionsItem");
        Set<String> accountSpringSecurityGrantedAuthorities = new HashSet<>();
        accountSpringSecurityGrantedAuthorities.add("accountSpringSecurityPermissionsItem");

        def authentication = createStrictMock(UsernamePasswordAuthenticationToken)
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)
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
        expect(client.dataStore).andStubReturn(dataStore)
        expect(dataStore.getResource(eq(applicationUrl), same(Application))).andReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer(new IAnswer<AuthenticationResult>() {
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
        expect(groupList.iterator()).andReturn iterator
        expect(iterator.hasNext()).andReturn true
        expect(iterator.next()).andReturn group
        expect(iterator.hasNext()).andReturn false
        expect(accountGrantedAuthorityResolver.resolveGrantedAuthorities(account)).andReturn accountGrantedAuthoritySet

        replay authentication, client, dataStore, app, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, accountGrantedAuthority

        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver

        Authentication info = authenticationProvider.authenticate(authentication)

        assertTrue info instanceof UsernamePasswordAuthenticationToken
        assertTrue info.authenticated

        assertEquals acctUsername, ((UserDetails) info.principal).username
        assertEquals null, ((UserDetails) info.principal).password
        assertEquals 2, info.authorities.size()
        assertTrue info.authorities.contains(accountGrantedAuthority)
        assertEquals acctHref, ((StormpathUserDetails) info.principal).properties.get("href")
        assertEquals acctUsername, ((StormpathUserDetails) info.principal).properties.get("username")
        assertEquals acctEmail, ((StormpathUserDetails) info.principal).properties.get("email")
        assertEquals acctGivenName, ((StormpathUserDetails) info.principal).properties.get("givenName")
        assertEquals acctMiddleName, ((StormpathUserDetails) info.principal).properties.get("middleName")
        assertEquals acctSurname, ((StormpathUserDetails) info.principal).properties.get("surname")
        assertTrue(((UserDetails) info.principal).enabled)
        assertTrue(((UserDetails) info.principal).accountNonLocked)
        assertTrue(((UserDetails) info.principal).accountNonExpired)
        assertTrue(((UserDetails) info.principal).credentialsNonExpired)

        verify authentication, client, dataStore, app, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
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
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)
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
        expect(client.dataStore).andStubReturn(dataStore)
        expect(dataStore.getResource(eq(applicationUrl), same(Application))).andReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer(new IAnswer<AuthenticationResult>() {
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
        expect(groupList.iterator()).andReturn iterator
        expect(iterator.hasNext()).andReturn true
        expect(iterator.next()).andReturn group
        expect(groupGrantedAuthorityResolver.resolveGrantedAuthorities(group)).andReturn groupGrantedAuthoritySet
        expect(iterator.hasNext()).andReturn false
        expect(accountGrantedAuthorityResolver.resolveGrantedAuthorities(account)).andReturn accountGrantedAuthoritySet

        replay authentication, client, dataStore, app, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
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
        assertEquals acctHref, ((StormpathUserDetails) info.principal).properties.get("href")
        assertEquals acctUsername, ((StormpathUserDetails) info.principal).properties.get("username")
        assertEquals acctEmail, ((StormpathUserDetails) info.principal).properties.get("email")
        assertEquals acctGivenName, ((StormpathUserDetails) info.principal).properties.get("givenName")
        assertEquals acctMiddleName, ((StormpathUserDetails) info.principal).properties.get("middleName")
        assertEquals acctSurname, ((StormpathUserDetails) info.principal).properties.get("surname")
        assertTrue(((UserDetails) info.principal).enabled)
        assertTrue(((UserDetails) info.principal).accountNonLocked)
        assertTrue(((UserDetails) info.principal).accountNonExpired)
        assertTrue(((UserDetails) info.principal).credentialsNonExpired)

        verify authentication, client, dataStore, app, authenticationResult, account, accountCustomData, groupList, iterator, group, groupCustomData,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, groupGrantedAuthority, accountGrantedAuthority
    }

    @Test(expectedExceptions = AuthenticationServiceException)
    void testAuthenticateException() {
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)

        int status = 400
        int code = 400
        def msg = 'Invalid username or password.'
        def devMsg = 'Invalid username or password.'
        def moreInfo = 'mailto:support@stormpath.com'

        expect(client.dataStore).andStubReturn(dataStore)

        def error = new SimpleError(status: status, code: code, message: msg, developerMessage: devMsg, moreInfo: moreInfo)

        expect(dataStore.getResource(eq(applicationUrl), same(Application))).andReturn app
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andThrow(new com.stormpath.sdk.resource.ResourceException(error))

        replay client, dataStore, app

        def token = new UsernamePasswordAuthenticationToken('foo', 'bar')
        try {
            authenticationProvider.authenticate(token)
        }
        finally {
            verify client, dataStore, app
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
