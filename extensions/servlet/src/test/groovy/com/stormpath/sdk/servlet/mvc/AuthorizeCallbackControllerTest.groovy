package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.provider.ProviderAccountRequest
import com.stormpath.sdk.provider.ProviderAccountResult
import com.stormpath.sdk.servlet.application.ApplicationResolver
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent
import com.stormpath.sdk.servlet.event.RequestEvent
import com.stormpath.sdk.servlet.event.impl.Publisher
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver
import com.stormpath.sdk.servlet.mvc.provider.ProviderAccountRequestResolver
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.easymock.Capture
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.capture
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.eq
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.expectLastCall
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue

class AuthorizeCallbackControllerTest {
    static final String SIGNING_KEY = "not-a-very-secret-key"
    static final String REDIRECT_URI = 'http://myapp.com/redirected'
    static final String REQUEST_URI = 'http://myapp.com/authorize/callback'
    static final String CODE = 'some-code'
    public static final String PROVIDER_ID = 'some-provider'

    AuthorizeCallbackController controllerUT

    ApplicationResolver applicationResolver
    ProviderAccountRequestResolver providerAccountRequestResolver
    Application application
    AuthenticationResultSaver authenticationResultSaver
    Publisher<RequestEvent> eventPublisher

    MockHttpServletRequest request
    MockHttpServletResponse response
    ProviderAccountRequest providerAccountRequest

    @BeforeMethod
    void setUp() {
        request = new MockHttpServletRequest()
        request.requestURI = REQUEST_URI
        request.method = 'GET'
        initClient(request)

        response = new MockHttpServletResponse()

        applicationResolver = initApplicationResolver(request)

        providerAccountRequestResolver = initProviderAccountResolver()

        authenticationResultSaver = createMock(AuthenticationResultSaver)

        eventPublisher = createMock(Publisher)

        initControllerUT()
    }

    private void initControllerUT() {
        controllerUT = new AuthorizeCallbackController()
        controllerUT.setApplicationResolver(applicationResolver)
        controllerUT.setAuthenticationResultSaver(authenticationResultSaver)
        controllerUT.setEventPublisher(eventPublisher)
        controllerUT.setProviderAccountRequestResolver(providerAccountRequestResolver)
        controllerUT.init()
    }

    private ProviderAccountRequestResolver initProviderAccountResolver() {
        providerAccountRequest = createNiceMock(ProviderAccountRequest)
        providerAccountRequestResolver = createNiceMock(ProviderAccountRequestResolver)
        expect(providerAccountRequestResolver.getProviderAccountRequest(PROVIDER_ID, CODE, REQUEST_URI))
                .andStubReturn(providerAccountRequest)
        replay(providerAccountRequestResolver)
        providerAccountRequestResolver
    }

    private ApplicationResolver initApplicationResolver(MockHttpServletRequest request) {
        application = createNiceMock(Application)
        applicationResolver = createNiceMock(ApplicationResolver)
        expect(applicationResolver.getApplication(request)).andStubReturn(application)
        replay(applicationResolver)
        applicationResolver
    }

    private static void initClient(MockHttpServletRequest request) {
        ApiKey apiKey = createNiceMock(ApiKey)
        expect(apiKey.secret).andStubReturn(SIGNING_KEY)
        Client client = createNiceMock(Client)
        expect(client.getApiKey()).andStubReturn(apiKey)
        replay(apiKey, client)
        request.setAttribute(Client.class.name, client)
    }

    private static String getSignedJws(Map<String, Object> map) {
        def builder = Jwts.builder().signWith(SignatureAlgorithm.HS256, SIGNING_KEY).setClaims(map)
        return builder.compact()
    }


    @Test
    void testIsNotAllowedIfAuthenticated() {
        assertThat(controllerUT.notAllowedIfAuthenticated, is(true))
    }

    @Test
    void testSuccessfulCallback() {
        request.setParameter("code", "a-valid-code")
        request.setParameter("state", getSignedJws([provider  : PROVIDER_ID,
                                                    code        : CODE,
                                                    redirect_uri: REDIRECT_URI]))

        ProviderAccountResult providerAccountResult = createNiceMock(ProviderAccountResult)
        Account account = createNiceMock(Account)
        expect(providerAccountResult.account).andReturn(account)
        expect(application.getAccount(providerAccountRequest)).andReturn(providerAccountResult)
        Capture<AuthenticationResult> authenticationResultCapture = new Capture<>()
        authenticationResultSaver.set(eq(request), eq(response), capture(authenticationResultCapture))
        expectLastCall()
        Capture<RequestEvent> requestEventCapture = new Capture<>()
        eventPublisher.publish(capture(requestEventCapture))
        expectLastCall()

        replay(application, providerAccountResult, authenticationResultSaver, eventPublisher)

        def actual = controllerUT.handleRequest(request, response)
        assertThat("model view", actual, notNullValue())
        assertThat("redirect", actual.redirect, is(true))
        assertThat("view name", actual.viewName, is(REDIRECT_URI))

        verify(authenticationResultSaver)
        def actualAuthenticationResult = authenticationResultCapture.value
        assertThat(actualAuthenticationResult.account, is(account))

        verify(eventPublisher)
        def actualRequestEvent = requestEventCapture.value
        assertThat("request event", actualRequestEvent, instanceOf(SuccessfulAuthenticationRequestEvent))
        def authRequestEvent = actualRequestEvent as SuccessfulAuthenticationRequestEvent
        assertThat("request event request", authRequestEvent.request, equalTo(request as HttpServletRequest))
        assertThat("request event response", authRequestEvent.response, equalTo(response as HttpServletResponse))
        assertThat("request event value", authRequestEvent.authenticationResult, equalTo(actualAuthenticationResult))
    }

}
