package com.stormpath.sdk.servlet.filter.account

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.servlet.account.DefaultAccountResolver
import com.stormpath.sdk.servlet.http.Resolver
import org.easymock.EasyMockSupport

import org.testng.annotations.BeforeMethod
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify

/**
 * @since 1.0.RC9
 */
class AccountResolverFilterTest {

    HttpServletRequest mockRequest
    HttpServletResponse mockResponse
    FilterChain mockFilterChain
    Resolver<Account> mockAccountResolver

    AccountResolverFilter accountResolverFilter

    @BeforeMethod
    void setup() {
        mockRequest = createStrictMock(HttpServletRequest)
        mockResponse = createStrictMock(HttpServletResponse)
        mockFilterChain = createStrictMock(FilterChain)

        mockAccountResolver = createStrictMock(Resolver)

        accountResolverFilter = new AccountResolverFilter()
        accountResolverFilter.resolvers = [mockAccountResolver]
    }

    @Test
    void testAccountResolved() {
        Account mockAccount = createStrictMock(Account)

        expect(mockAccountResolver.get(mockRequest, mockResponse)).andReturn mockAccount
        expect(mockResponse.isCommitted()).andReturn false
        expect(mockAccount.getStatus()).andReturn AccountStatus.ENABLED
        expect(mockRequest.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, mockAccount))
        expect(mockRequest.setAttribute("account", mockAccount))
        expect(mockRequest.getAuthType()).andReturn "form"
        expect(mockFilterChain.doFilter(mockRequest, mockResponse))

        replay mockRequest, mockResponse, mockFilterChain, mockAccountResolver, mockAccount

        accountResolverFilter.filter(mockRequest, mockResponse, mockFilterChain)

        verify mockRequest, mockResponse, mockFilterChain, mockAccountResolver, mockAccount
    }

    @Test
    void testAccountDisabled() {
        Account mockAccount = createStrictMock(Account)

        expect(mockAccountResolver.get(mockRequest, mockResponse)).andReturn mockAccount
        expect(mockResponse.isCommitted()).andReturn false
        expect(mockAccount.getStatus()).andReturn AccountStatus.DISABLED
        expect(mockFilterChain.doFilter(mockRequest, mockResponse))

        replay mockRequest, mockResponse, mockFilterChain, mockAccountResolver, mockAccount

        accountResolverFilter.filter(mockRequest, mockResponse, mockFilterChain)

        verify mockRequest, mockResponse, mockFilterChain, mockAccountResolver, mockAccount
    }

    @Test
    void testAccountNotResolved() {
        expect(mockAccountResolver.get(mockRequest, mockResponse)).andReturn null
        expect(mockResponse.isCommitted()).andReturn false
        expect(mockFilterChain.doFilter(mockRequest, mockResponse))

        replay mockRequest, mockResponse, mockFilterChain, mockAccountResolver

        accountResolverFilter.filter(mockRequest, mockResponse, mockFilterChain)

        verify mockRequest, mockResponse, mockFilterChain, mockAccountResolver
    }
}
