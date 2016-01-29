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
import com.stormpath.sdk.resource.Deletable
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter
import com.stormpath.spring.oauth.Oauth2AuthenticationSpringSecurityProcessingFilter
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
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import javax.servlet.Filter

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = [MinimalStormpathSpringSecurityWebMvcAppConfig.class, TwoAppTenantStormpathConfiguration.class])
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
    Oauth2AuthenticationSpringSecurityProcessingFilter oauth2AuthenticationSpringSecurityProcessingFilter

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

    def password = "Pass123!" + UUID.randomUUID()

    Account account;

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
        assertNotNull stormpathAuthenticationProvider.applicationRestUrl
        assertNotNull stormpathAuthenticationProvider.client
        assertNotNull stormpathAuthenticationProvider.client
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
        account = createTempAccount(password)

        new CustomDataPermissionsEditor(account.getCustomData()).append("user:edit");
        account.save()

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(account.getEmail(), password))
        assertTrue authentication.authenticated
        assertTrue (((StormpathUserDetails)authentication.principal).getUsername().equals(account.getUsername()))
        assertTrue hasRole(authentication, ["user:edit"] as String[])
        SecurityContextHolder.clearContext()
    }

    @Test
    void testCsrfTokenManager() {
        assertTrue (!(csrfTokenManager instanceof DefaultCsrfTokenManager))
        assertEquals csrfTokenManager.tokenName, '_csrf'
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
        password = "Pass123!" + UUID.randomUUID();
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
