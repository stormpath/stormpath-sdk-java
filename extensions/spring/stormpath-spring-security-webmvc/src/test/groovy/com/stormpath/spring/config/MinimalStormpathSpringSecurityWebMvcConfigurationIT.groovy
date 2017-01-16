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
import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.lang.Assert
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.servlet.authc.impl.DefaultLogoutRequestEvent
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager
import com.stormpath.sdk.servlet.event.RequestEventListener
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.spring.filter.StormpathWrapperFilter
import com.stormpath.spring.security.authz.CustomDataPermissionsEditor
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import org.apache.http.entity.ContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.servlet.Filter
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.testng.Assert.*

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = [MinimalStormpathSpringSecurityWebMvcTestAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
class MinimalStormpathSpringSecurityWebMvcConfigurationIT extends AbstractClientIT {

    private static final Logger log = LoggerFactory.getLogger(MinimalStormpathSpringSecurityWebMvcConfigurationIT)

    @Autowired
    SecurityConfigurerAdapter stormpathSecurityConfigurerAdapter;

    @Autowired
    ApiKey apiKey;

    @Autowired
    CacheManager stormpathCacheManager;

    @Autowired
    AccountResolverFilter springSecurityResolvedAccountFilter

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

    @Autowired
    AuthenticationEventPublisher authenticationEventPublisher

    @Autowired
    ProviderManager providerManager

    @Autowired
    WebApplicationContext context;

    @Autowired
    protected FilterChainProxy springSecurityFilterChain;

    @Autowired
    protected Filter stormpathFilter;

    @Autowired
    StormpathWrapperFilter stormpathWrapperFilter;

    private MockMvc mvc;

    @BeforeClass
    public void setUp() {

        super.setUp()

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain, "/*") //Spring security in front of Stormpath
                .addFilter(stormpathFilter, "/*")
                .build();
    }

    @Test
    void testRequiredBeans() {
        assertTrue stormpathSecurityConfigurerAdapter instanceof StormpathSecurityConfigurerAdapter //let's assert Stormpath's Spring Security integration is not disabled
        assertNotNull apiKey
        assertNotNull stormpathCacheManager
        assertNotNull client
        assertNotNull application
        assertNotNull stormpathFilter
        assertNotNull springSecurityResolvedAccountFilter
        assertNotNull stormpathWrapperFilter;
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
        assertTrue (((UserDetails)authentication.principal).getUsername().equals(account.getHref()))
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
        expect(httpServletRequest.getServletContext()).andReturn(servletContext).times(2)
        expect(servletContext.getAttribute("com.stormpath.sdk.client.Client")).andReturn(client).times(2)

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
    @Test(enabled=false)
    void testPreAuthenticationCheckOnCookieRequest() {
        HttpServletRequest servletRequest = createStrictMock(HttpServletRequest)
        HttpServletResponse servletResponse = createStrictMock(HttpServletResponse)
        javax.servlet.FilterChain filterChain = createStrictMock(javax.servlet.FilterChain)
        Authentication authentication = createStrictMock(Authentication.class)
        Account account = createStrictMock(Account)

        expect(servletRequest.getAttribute(Account.class.getName())).andReturn(account)

        def userDetails = createStrictMock(UserDetails)

        // set href on account that's retrieved from request
        expect(account.getHref()).andReturn "url"

        Map<String, String> props = new HashMap()
        props.put("href", "url")

        // return matching href on account so authentication is not performed
        expect(userDetails.getUsername()).andReturn('url')

        expect(authentication.getPrincipal()).andStubReturn(userDetails)

        // set authentication
        SecurityContextHolder.getContext().setAuthentication(authentication)

        replay account, authentication, servletRequest, userDetails

        ((AccountResolverFilter)springSecurityResolvedAccountFilter).filter(servletRequest, servletResponse, filterChain)

        verify account, authentication, servletRequest, userDetails

        // verify authentication object was not changed, meaning backend was not contacted
        Assert.isTrue(SecurityContextHolder.getContext().getAuthentication().equals(authentication))
    }

    /**
     * @since 1.3.0
     */
    @Test
    void testAuthenticationSuccessEventIsTriggered() {
        providerManager.setAuthenticationEventPublisher(authenticationEventPublisher)
        MinimalStormpathSpringSecurityWebMvcTestAppConfig.CustomAuthenticationSuccessEventListener.eventWasTriggered = false
        mvc.perform(post("/login").accept(MediaType.TEXT_HTML.toString()).contentType(ContentType.TEXT_HTML.toString()).with(csrf()).param("login", account.getEmail()).param("password", password)).andReturn()
        assertTrue MinimalStormpathSpringSecurityWebMvcTestAppConfig.CustomAuthenticationSuccessEventListener.eventWasTriggered
        SecurityContextHolder.clearContext()
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
