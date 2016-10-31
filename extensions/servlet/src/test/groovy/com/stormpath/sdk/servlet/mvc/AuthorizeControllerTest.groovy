package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.servlet.application.ApplicationResolver
import com.stormpath.sdk.servlet.mvc.provider.ProviderAuthorizationEndpointResolver
import org.easymock.IAnswer
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue

class AuthorizeControllerTest {
    public static final String AUTHORIZED_CALLBACK1 = "https://foo.com"
    public static final String AUTHORIZED_CALLBACK2 = "https://bar.com"
    ApplicationResolver applicationResolver
    ProviderAuthorizationEndpointResolver providerAuthorizationEndpointResolver
    AuthorizeController controllerUT = new AuthorizeController()
    MockHttpServletRequest servletRequest
    MockHttpServletResponse servletResponse
    Application application
    ApplicationAccountStoreMappingList applicationAccountStoreMappingList
    List<ApplicationAccountStoreMapping> accountStoreMappings
    List<String> authorizedCallbacks

    @BeforeMethod
    void setUp() {
        servletRequest = new MockHttpServletRequest()
        servletResponse = new MockHttpServletResponse()

        servletRequest.setParameter("response_type", "stormpath_token")
        application = createNiceMock(Application)
        applicationResolver = createNiceMock(ApplicationResolver)

        applicationAccountStoreMappingList = initAccountStoreMappingList()

        expect(applicationResolver.getApplication(servletRequest)).andStubReturn(application)
        expect(application.getAccountStoreMappings()).andStubReturn(applicationAccountStoreMappingList)

        authorizedCallbacks = [AUTHORIZED_CALLBACK1, AUTHORIZED_CALLBACK2]
        expect(application.getAuthorizedCallbackUris()).andStubReturn(authorizedCallbacks)

        replay(applicationResolver, application)

        providerAuthorizationEndpointResolver = createNiceMock(ProviderAuthorizationEndpointResolver)

        controllerUT.applicationResolver = applicationResolver
        controllerUT.providerAuthorizationEndpointResolver = providerAuthorizationEndpointResolver
        controllerUT.nextUri = "test"

    }

    private ApplicationAccountStoreMappingList initAccountStoreMappingList() {
        def applicationAccountStoreMappingList = createNiceMock(ApplicationAccountStoreMappingList)
        accountStoreMappings = []
        accountStoreMappings.add(getMockMapping(Directory, "dir1"))
        accountStoreMappings.add(getMockMapping(Directory, "dir2"))
        accountStoreMappings.add(getMockMapping(Directory, "dir3"))
        expect(applicationAccountStoreMappingList.iterator())
                .andStubAnswer(new IAnswer<Iterator<ApplicationAccountStoreMapping>>() {
            @Override
            Iterator<ApplicationAccountStoreMapping> answer() throws Throwable {
                return accountStoreMappings.iterator();
            }
        })
        replay(applicationAccountStoreMappingList)
        applicationAccountStoreMappingList
    }

    private ApplicationAccountStoreMapping getMockMapping(Class<? extends AccountStore> type, uid) {
        ApplicationAccountStoreMapping mapping = createNiceMock(ApplicationAccountStoreMapping)
        expect(mapping.application).andStubReturn(application)
        AccountStore accountStore = createNiceMock(type)
        String href = "http://test.com/v1/store/${uid}"
        expect(accountStore.href).andStubReturn(href)
        expect(mapping.accountStore).andStubReturn(accountStore)
        replay(mapping, accountStore)
        mapping
    }

    @Test
    void testInit() {
        controllerUT.init()
    }

    @Test
    void testIsNotAllowedIfAuthenticated() {
        assertThat(controllerUT.notAllowedIfAuthenticated, is(true))
    }

    @Test
    void testGetResponseWhenDirectoryFound() {
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.setMethod("GET")
        expect(providerAuthorizationEndpointResolver.getEndpoint(servletRequest, AUTHORIZED_CALLBACK1, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(providerAuthorizationEndpointResolver)

        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(notNullValue()))
        assertThat("redirect", viewModel.redirect, is(true))
        assertThat("viewName", viewModel.viewName, is("https://got-there.com"))
    }

    @Test
    void testGetResponseWhenDirectoryNotFound() {
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setMethod("GET")
        servletRequest.setParameter("account_store_href", "http://no-such-href")
        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(nullValue()))
        assertThat("response status", servletResponse.status, is(404))
    }

    @Test
    void testGetResponseWhenWithRedirectUriSpecified() {
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.setParameter("redirect_uri", AUTHORIZED_CALLBACK2)
        servletRequest.setMethod("GET")
        expect(providerAuthorizationEndpointResolver.getEndpoint(servletRequest, AUTHORIZED_CALLBACK2, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(providerAuthorizationEndpointResolver)

        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(notNullValue()))
        assertThat("redirect", viewModel.redirect, is(true))
        assertThat("viewName", viewModel.viewName, is("https://got-there.com"))
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetResponseWhenWithInvalidRedirectUriSpecified() {
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.setParameter("redirect_uri", "http://badone.com")
        servletRequest.setMethod("GET")
        replay(providerAuthorizationEndpointResolver)

        controllerUT.handleRequest(servletRequest, servletResponse)
    }

    @Test
    void testGetResponseWhenUidIsNotDirectory() {
        def mapping = getMockMapping(Group, "groupUid")
        accountStoreMappings.add(mapping)
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setMethod("GET")
        servletRequest.setParameter("account_store_href", mapping.accountStore.href)
        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(nullValue()))
        assertThat("response status", servletResponse.status, is(404))
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetResponseWithEmptyAuthorizedCallbackUris() {
        authorizedCallbacks.clear()
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.setMethod("GET")
        expect(providerAuthorizationEndpointResolver.getEndpoint(servletRequest, AUTHORIZED_CALLBACK1, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(providerAuthorizationEndpointResolver)

        controllerUT.handleRequest(servletRequest, servletResponse)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetResponseWithNoResponseType() {
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.removeParameter("response_type")
        servletRequest.setMethod("GET")
        expect(providerAuthorizationEndpointResolver.getEndpoint(servletRequest, AUTHORIZED_CALLBACK1, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(providerAuthorizationEndpointResolver)

        controllerUT.handleRequest(servletRequest, servletResponse)
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGetResponseWithInvalidResponseType() {
        Directory expectedDirectory = accountStoreMappings[2].accountStore as Directory
        servletRequest.setRequestURI("http://blah.com/authorize")
        servletRequest.setParameter("account_store_href", expectedDirectory.href)
        servletRequest.setParameter("response_type", "invalid")
        servletRequest.setMethod("GET")
        expect(providerAuthorizationEndpointResolver.getEndpoint(servletRequest, AUTHORIZED_CALLBACK1, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(providerAuthorizationEndpointResolver)

        controllerUT.handleRequest(servletRequest, servletResponse)
    }

}
