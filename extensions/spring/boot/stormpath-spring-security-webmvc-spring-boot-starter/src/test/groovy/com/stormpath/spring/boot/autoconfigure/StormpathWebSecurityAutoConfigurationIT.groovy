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

import autoconfigure.StormpathWebSecurityAutoConfigurationApplication
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.impl.cache.DisabledCacheManager
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.servlet.config.CookieConfig
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver
import com.stormpath.sdk.servlet.mvc.Controller
import com.stormpath.spring.config.TwoAppTenantStormpathConfiguration
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter
import com.stormpath.spring.oauth.OAuth2AuthenticationProcessingFilter
import com.stormpath.spring.security.authz.CustomDataPermissionsEditor
import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5
 */
@SpringApplicationConfiguration(classes = [StormpathWebSecurityAutoConfigurationApplication.class, TwoAppTenantStormpathConfiguration.class])
@WebAppConfiguration
class StormpathWebSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    Client client;

    @Autowired
    Application application;

    @Autowired
    OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter;

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
    CookieConfig stormpathAccountCookieConfig

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
    AuthenticationManager authenticationManager

    @Autowired
    RequestEventPublisher requestEventPublisher

    @Test
    void test() {

        assertNotNull stormpathAuthenticationProvider
        assertNotNull stormpathAuthenticationProvider.applicationRestUrl
        assertNotNull stormpathAuthenticationProvider.client

        assertTrue stormpathAuthenticationProvider.client.dataStore.cacheManager instanceof DisabledCacheManager
        assertTrue stormpathAuthenticationProvider.groupGrantedAuthorityResolver instanceof DefaultGroupGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.groupPermissionResolver instanceof GroupCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.accountGrantedAuthorityResolver instanceof EmptyAccountGrantedAuthorityResolver
        assertTrue stormpathAuthenticationProvider.accountPermissionResolver instanceof AccountCustomDataPermissionResolver
        assertTrue stormpathAuthenticationProvider.authenticationTokenFactory instanceof UsernamePasswordAuthenticationTokenFactory

        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull oAuth2AuthenticationProcessingFilter
        assertNotNull oAuth2AuthenticationProcessingFilter.authenticationProvider

        assertNotNull stormpathWildcardPermissionEvaluator
        assertNotNull stormpathMethodSecurityExpressionHandler
        assertTrue stormpathMethodSecurityExpressionHandler.defaultRolePrefix.equals("")

        //Some WebMVC beans
        assertNotNull stormpathHandlerMapping
        assertNotNull stormpathLayoutInterceptor
        assertNotNull stormpathAccountStoreResolver
        assertNotNull stormpathUsernamePasswordRequestFactory
        assertNotNull stormpathAccountCookieConfig
        assertNotNull stormpathAccessTokenResultFactory
        assertNotNull stormpathCookieAccountResolver
        assertNotNull stormpathLoginController
        assertNotNull stormpathLocaleResolver
    }

    @Test
    void testCsrfTokenManager() {
        assertTrue (csrfTokenManager instanceof DisabledCsrfTokenManager)
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
        Directory directory = createTempDir()
        application.setDefaultAccountStore(directory)

        String password = "Pass123!" + UUID.randomUUID()
        Account account = createTempAccount(password)
        new CustomDataPermissionsEditor(account.getCustomData()).append("user:edit");
        account.save()

        Authentication authentication = authenticationManager.authenticate(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(account.getEmail(), password))
        assertTrue authentication.authenticated
        assertTrue (((StormpathUserDetails)authentication.principal).getUsername().equals(account.getUsername()))
        assertTrue hasRole(authentication, ["user:edit"] as String[])
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

    @BeforeTest
    public void setUp() {
        resourcesToDelete = []
    }

    @AfterTest
    public void tearDown() {
        def reversed = resourcesToDelete.reverse() //delete in opposite order (cleaner - children deleted before parents)

        for (def r : reversed) {
            try {
                r.delete()
            } catch (Throwable t) {
                log.error('Unable to delete resource ' + r, t)
            }
        }
    }

    /**
     * @return true if the user has one of the specified roles.
     */
    protected static boolean hasRole(Authentication authentication, String[] roles) {
        boolean result = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String userRole = authority.getAuthority();
            for (String role : roles) {
                if (role.equals(userRole)) {
                    result = true;
                    break;
                }
            }
            if (result) {
                break;
            }
        }

        return result;
    }
}
