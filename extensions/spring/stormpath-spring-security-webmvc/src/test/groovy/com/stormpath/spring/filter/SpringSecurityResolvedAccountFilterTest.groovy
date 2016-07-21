package com.stormpath.spring.filter

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.servlet.account.AccountResolver
import com.stormpath.spring.security.provider.StormpathUserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.*

/**
 * @since 1.0.0
 */

class SpringSecurityResolvedAccountFilterTest {

    def SpringSecurityResolvedAccountFilter filter
    def request, response, filterChain
    def account, accountResolver, authentication, authenticationProvider

    @BeforeMethod
    public void setup() {
        filter = new SpringSecurityResolvedAccountFilter();
        request = createStrictMock(HttpServletRequest.class)
        response = createStrictMock(HttpServletResponse.class)
        filterChain = createStrictMock(FilterChain.class)

        account = createStrictMock(Account)
        accountResolver = createStrictMock(AccountResolver)
        authentication = createStrictMock(Authentication)
        authenticationProvider = createStrictMock(AuthenticationProvider)

        filter.authenticationProvider = authenticationProvider
        filter.accountResolver = accountResolver
    }

    @AfterMethod
    public void teardown() {
        reset request, response, filterChain, account, accountResolver, authentication, authenticationProvider
    }

    @Test
    public void testAuthenticationRefreshedWhenAccountExists() {
        expect(accountResolver.getAccount(request)).andReturn account
        expect(authenticationProvider.authenticate(isA(Authentication.class))).andReturn authentication
        expect(account.getEmail()).andReturn("foo@bar.com").times(2)
        expect(filterChain.doFilter(request, response)).times(1)

        replay account, accountResolver, authenticationProvider, filterChain, request

        filter.filter(request, response, filterChain)

        verify account, accountResolver, authenticationProvider, filterChain, request
    }

    @Test
    public void testAuthenticationNotRefreshedWhenHrefInUserDetailsMatchesAccount() {
        def userDetails = createStrictMock(StormpathUserDetails)

        expect(accountResolver.getAccount(request)).andReturn account
        expect(account.getHref()).andReturn "url"
        expect(filterChain.doFilter(request, response)).times(1)

        Map<String, String> props = new HashMap()
        props.put("href", "url")

        expect(userDetails.getProperties()).andReturn props
        expect(authentication.getPrincipal()).andReturn(userDetails).times(2)
        SecurityContextHolder.getContext().setAuthentication(authentication)

        replay account, accountResolver, authentication, authenticationProvider, filterChain, request, userDetails

        filter.filter(request, response, filterChain)

        verify account, accountResolver, authentication, authenticationProvider, filterChain, request, userDetails
    }
}
