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
package com.stormpath.spring.boot.autoconfigure

import autoconfigure.StormpathWebSecurityAutoConfigurationTestApplication
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.lang.Assert
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.servlet.authc.impl.DefaultLogoutRequestEvent
import com.stormpath.sdk.servlet.client.ClientLoader
import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.config.TwoAppTenantStormpathTestConfiguration
import com.stormpath.spring.csrf.SpringSecurityCsrfTokenManager
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter
import com.stormpath.spring.security.authz.CustomDataPermissionsEditor
import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.testng.Assert.*
/**
 * @since 1.0.RC5
 */
@SpringBootTest(classes = [StormpathWebSecurityAutoConfigurationTestApplication.class, TwoAppTenantStormpathTestConfiguration.class])
class StormpathWebSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    Client client;

    @Autowired
    Application application;

    @Autowired
    OAuthAuthenticationSpringSecurityProcessingFilter oauth2AuthenticationSpringSecurityProcessingFilter;

    @Autowired
    SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter;

    //Spring Security Bean
    @Autowired
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    @Autowired
    PermissionEvaluator stormpathWildcardPermissionEvaluator

    @Autowired
    MethodSecurityExpressionHandler stormpathMethodSecurityExpressionHandler

    //Some WebMVC Beans
    @Autowired
    HandlerMapping stormpathHandlerMapping

    @Autowired
    HandlerInterceptor stormpathLayoutInterceptor

    @Autowired
    AccountStoreResolver stormpathAccountStoreResolver

    @Autowired
    UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory

    @Autowired
    @Qualifier("stormpathRefreshTokenCookieConfig")
    CookieConfig stormpathRefreshTokenCookieConfig

    @Autowired
    @Qualifier("stormpathAccessTokenCookieConfig")
    CookieConfig stormpathAccessTokenCookieConfig

    @Autowired
    AccessTokenResultFactory stormpathAccessTokenResultFactory

    @Autowired
    Resolver<Account> stormpathCookieAccountResolver

    @Autowired
    Controller stormpathLoginController

    @Autowired
    Resolver<Locale> stormpathLocaleResolver

    @Autowired
    CsrfTokenManager csrfTokenManager

    @Autowired
    CsrfTokenRepository csrfTokenRepository

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    RequestEventPublisher requestEventPublisher

    //State shared by these internal tests
    def password = "Pass123!" + UUID.randomUUID()
    Account account;

    @Test
    void test() {

        assertNotNull stormpathAuthenticationProvider
        assertNotNull stormpathAuthenticationProvider.application

        assertTrue stormpathAuthenticationProvider.groupGrantedAuthorityResolver instanceof DefaultGroupGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.groupPermissionResolver instanceof GroupCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.accountGrantedAuthorityResolver instanceof EmptyAccountGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.accountPermissionResolver instanceof AccountCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.authenticationTokenFactory instanceof UsernamePasswordAuthenticationTokenFactory

        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull oauth2AuthenticationSpringSecurityProcessingFilter
        assertNotNull oauth2AuthenticationSpringSecurityProcessingFilter.authenticationProvider

        assertNotNull stormpathWildcardPermissionEvaluator
        assertNotNull stormpathMethodSecurityExpressionHandler
        assertTrue stormpathMethodSecurityExpressionHandler.defaultRolePrefix.equals("")

        //Some WebMVC beans
        assertNotNull stormpathHandlerMapping
        assertNotNull stormpathLayoutInterceptor
        assertNotNull stormpathAccountStoreResolver
        assertNotNull stormpathUsernamePasswordRequestFactory
        assertNotNull stormpathAccessTokenCookieConfig
        assertNotNull stormpathRefreshTokenCookieConfig
        assertNotNull stormpathAccessTokenResultFactory
        assertNotNull stormpathCookieAccountResolver
        assertNotNull stormpathLoginController
        assertNotNull stormpathLocaleResolver
    }

    @Test
    void testCsrfTokenManager() {
        assertTrue (csrfTokenManager instanceof SpringSecurityCsrfTokenManager)
        assertTrue (csrfTokenRepository instanceof HttpSessionCsrfTokenRepository)
        assertEquals csrfTokenManager.tokenName, '_csrf'
    }

    /**
     * @since 1.0.RC8.3
     */
    @Test
    void testTokenRevocationListener() {
        def hasTokenRevocationListener = false
        for (RequestEventListener listener : requestEventPublisher.listeners) {
            if (listener instanceof TokenRevocationRequestEventListener) {
                hasTokenRevocationListener = true
                break
            }
        }
        assertTrue hasTokenRevocationListener
    }

    /**
     * @since 1.0.RC8.3
     */
    @Test
    void testLogin() {
        new CustomDataPermissionsEditor(account.getCustomData()).append("user:edit");
        account.save()

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getEmail(), password))
        assertTrue authentication.authenticated
        assertTrue (((UserDetails)authentication.principal).getUsername().equals(account.getHref()))
        assertTrue hasRole(authentication, ["user:edit"] as String[])
    }

    /**
     * @since 1.0.RC9
     */
    @Test
    void testAccessTokenRevocation() {
        def httpServletRequest = createStrictMock(HttpServletRequest.class)
        def httpServletResponse = createStrictMock(HttpServletResponse.class)
        def servletContext = createStrictMock(ServletContext.class)

        OAuthPasswordGrantRequestAuthentication passwordGrantRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setLogin(account.getEmail()).setPassword(password).build();
        def result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(application).authenticate(passwordGrantRequest)
        def accessToken = result.getAccessToken()

        expect(httpServletRequest.getHeader("Authorization")).andReturn("Bearer " + accessToken.getJwt())
        expect(httpServletRequest.getServletContext()).andReturn(servletContext)
        expect(httpServletRequest.getAttribute(Client.class.getName())).andReturn(client)
        expect(servletContext.getAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY)).andReturn(client)

        replay(httpServletRequest, httpServletResponse, servletContext)

        Assert.notNull(accessToken.getHref())
        Assert.isTrue(account.getAccessTokens().getSize() == 1)
        Assert.isTrue(account.getRefreshTokens().getSize() == 1)
        def logoutRequestEvent = new DefaultLogoutRequestEvent(httpServletRequest, httpServletResponse, account)
        requestEventPublisher.publish(logoutRequestEvent)
        Assert.isTrue(account.getAccessTokens().getSize() == 0)
        Assert.isTrue(account.getRefreshTokens().getSize() == 0)

        verify(httpServletRequest, httpServletResponse, servletContext)
    }

    ///Supporting properties and methods

    List<Deletable> resourcesToDelete;

    private Account createTempAccount(String password) {
        Account account = client.instantiate(Account.class)
        String username = "foo-account-deleteme-" + UUID.randomUUID();
        account.setEmail(username + "@stormpath.com")
        account.setUsername(username)
        account.setPassword(password)
        account.setGivenName(username)
        account.setSurname(username)
        application.createAccount(account)
        deleteOnTeardown(account)
        return account
    }

    private Directory createTempDir() {
        Directory dir = client.instantiate(Directory.class)
        String name = "foo-dir-deleteme-" + UUID.randomUUID();
        dir.setName(name);
        client.createDirectory(dir);
        deleteOnTeardown(dir)
        return dir
    }

    protected void deleteOnTeardown(Deletable d) {
        this.resourcesToDelete.add(d)
    }

    @BeforeClass
    public void setUp() {
        resourcesToDelete = []
        def dir = createTempDir()
        application.setDefaultAccountStore(dir)
        account = createTempAccount(password)
    }

    @AfterClass
    public void tearDown() {
        def reversed = resourcesToDelete.reverse() //delete in opposite order (cleaner - children deleted before parents)
        reversed.collect { it.delete() }
    }

    /**
     * @return true if the user has one of the specified roles.
     */
    protected static boolean hasRole(Authentication authentication, String[] roles) {
        boolean result = false
        outerloop:
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String userRole = authority.getAuthority();
            for (String role : roles) {
                if (role.equals(userRole)) {
                    result = true
                    break outerloop
                }
            }
        }
        return result;
    }
}
