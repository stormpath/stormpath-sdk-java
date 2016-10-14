package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.directory.AccountStore
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.resource.Resource
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
    ApplicationResolver applicationResolver
    ProviderAuthorizationEndpointResolver externalAuthorizationEndpointResolver
    AuthorizeController controllerUT = new AuthorizeController()
    MockHttpServletRequest servletRequest
    MockHttpServletResponse servletResponse
    Application application
    ApplicationAccountStoreMappingList applicationAccountStoreMappingList
    List<ApplicationAccountStoreMapping> accountStoreMappings

    @BeforeMethod
    void setUp() {
        servletRequest = new MockHttpServletRequest()
        servletResponse = new MockHttpServletResponse()

        application = createNiceMock(Application)
        applicationResolver = createNiceMock(ApplicationResolver)

        applicationAccountStoreMappingList = initAccountStoreMappingList()

        expect(applicationResolver.getApplication(servletRequest)).andStubReturn(application)
        expect(application.getAccountStoreMappings()).andStubReturn(applicationAccountStoreMappingList)

        replay(applicationResolver, application)

        externalAuthorizationEndpointResolver = createNiceMock(ProviderAuthorizationEndpointResolver)

        controllerUT.applicationResolver = applicationResolver
        controllerUT.providerAuthorizationEndpointResolver = externalAuthorizationEndpointResolver
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
        def dirUid = getUid(expectedDirectory)
        servletRequest.setRequestURI("http://blah.com/authorize/${dirUid}")
        servletRequest.setMethod("GET")
        expect(externalAuthorizationEndpointResolver.getEndpoint(servletRequest, expectedDirectory.getProvider()))
                .andStubReturn("https://got-there.com")
        replay(externalAuthorizationEndpointResolver)

        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(notNullValue()))
        assertThat("redirect", viewModel.redirect, is(true))
        assertThat("viewName", viewModel.viewName, is("https://got-there.com"))
    }

    @Test
    void testGetResponseWhenDirectoryNotFound() {
        servletRequest.setRequestURI("http://blah.com/authorize/non-existent-uid")
        servletRequest.setMethod("GET")
        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(nullValue()))
        assertThat("response status", servletResponse.status, is(404))
    }

    @Test
    void testGetResponseWhenUidIsNotDirectory() {
        accountStoreMappings.add(getMockMapping(Group, "groupUid"))
        servletRequest.setRequestURI("http://blah.com/authorize/groupUid")
        servletRequest.setMethod("GET")
        def viewModel = controllerUT.handleRequest(servletRequest, servletResponse)
        assertThat("ViewModel", viewModel, is(nullValue()))
        assertThat("response status", servletResponse.status, is(404))
    }

    private static String getUid(Resource resource) {
        //noinspection GroovyAssignabilityCheck
        String dirUid = (resource.href =~ /.*\/(.*)/)[0][1]
        dirUid
    }
}
