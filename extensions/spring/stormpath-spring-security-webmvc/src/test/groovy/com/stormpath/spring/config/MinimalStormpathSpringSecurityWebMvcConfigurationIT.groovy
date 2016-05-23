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
package com.stormpath.spring.config

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.lang.Assert
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.servlet.authc.impl.DefaultLogoutRequestEvent
import com.stormpath.sdk.servlet.client.ClientLoader
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter
import com.stormpath.spring.security.authz.CustomDataPermissionsEditor
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import com.stormpath.spring.security.provider.StormpathUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.servlet.Filter
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = [MinimalStormpathSpringSecurityWebMvcTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
class MinimalStormpathSpringSecurityWebMvcConfigurationIT extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(MinimalStormpathSpringSecurityWebMvcConfigurationIT)

    @Autowired
    StormpathWebSecurityConfigurer c;

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    Client client;

    @Autowired
    Application application;

    @Autowired
    Filter stormpathFilter

    @Autowired
    OAuthAuthenticationSpringSecurityProcessingFilter oauth2AuthenticationSpringSecurityProcessingFilter

    @Autowired
    SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter

    @Autowired
    StormpathAuthenticationProvider stormpathAuthenticationProvider

    @Autowired
    PermissionEvaluator stormpathWildcardPermissionEvaluator

    @Autowired
    MethodSecurityExpressionHandler stormpathMethodSecurityExpressionHandler

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    CsrfTokenManager csrfTokenManager

    @Autowired
    RequestEventPublisher requestEventPublisher

    //State shared by these internal tests
    def password = "Pass123!" + UUID.randomUUID()
    Account account

    @Test
    void testRequiredBeans() {
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull client
        assertNotNull application
        assertNotNull stormpathFilter
        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull oauth2AuthenticationSpringSecurityProcessingFilter
        assertNotNull oauth2AuthenticationSpringSecurityProcessingFilter.authenticationProvider
        assertNotNull authenticationManager
        assertNotNull stormpathWildcardPermissionEvaluator
        assertNotNull stormpathMethodSecurityExpressionHandler
        assertTrue stormpathMethodSecurityExpressionHandler.defaultRolePrefix.equals("")

        assertNotNull stormpathAuthenticationProvider
        assertNotNull stormpathAuthenticationProvider.application
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

    @Test
    void testCsrfTokenManager() {
        assertTrue (!(csrfTokenManager instanceof DefaultCsrfTokenManager))
        assertEquals csrfTokenManager.tokenName, '_csrf'
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
        assertTrue (((StormpathUserDetails)authentication.principal).getUsername().equals(account.getUsername()))
        assertTrue hasRole(authentication, ["user:edit"] as String[])
        SecurityContextHolder.clearContext()
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

    /**
     * Asserts https://github.com/stormpath/stormpath-sdk-java/issues/605
     * @since 1.0.0
     */
    @Test
    void testPreAuthenticationCheckOnCookieRequest() {
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest.class)
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse.class)
        javax.servlet.FilterChain filterChain = createStrictMock(javax.servlet.FilterChain.class)
        Authentication authentication = createStrictMock(Authentication.class)

        SecurityContextHolder.getContext().setAuthentication(authentication)

        ((SpringSecurityResolvedAccountFilter)springSecurityResolvedAccountFilter).filter(servletRequest, servletResponse, filterChain)
        Assert.isTrue(SecurityContextHolder.getContext().getAuthentication().equals(authentication))
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

    protected Directory createTempDir() {
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
