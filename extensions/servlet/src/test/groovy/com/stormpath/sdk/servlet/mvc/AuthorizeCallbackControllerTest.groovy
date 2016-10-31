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
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.easymock.Capture
import org.joda.time.DateTime
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.stormpath.matchers.JwtMatchers.hasClaim
import static com.stormpath.matchers.JwtMatchers.hasHeader
import static org.easymock.EasyMock.capture
import static org.easymock.EasyMock.createMock
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.eq
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.expectLastCall
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.both
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.greaterThanOrEqualTo
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.lessThanOrEqualTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.startsWith

class AuthorizeCallbackControllerTest {
    static final String SIGNING_KEY = "not-a-very-secret-key"
    static final String KEY_ID = "test-key-id"
    static final String REDIRECT_URI = 'http://myapp.com/redirected'
    static final String REQUEST_URI = '/authorize/callback'
    static final String CODE = 'some-code'
    static final String PROVIDER_ID = 'some-provider'
    static final String ORGANIZATION_HREF = "http://href.org"
    static final String ORGANIZATION_NAME_KEY = "org-name"
    static final String ACCOUNT_HREF = "http://account-href"
    static final String APPLICATION_HREF = "http://application-href"
    static final String APP_STATE = "some-application-state"
    public static final String NONCE_FROM_AUTHORIZE = "nonce-from-authorize"

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
        expect(providerAccountRequestResolver.getProviderAccountRequest(PROVIDER_ID, CODE, request.getRequestURL().toString()))
                .andStubReturn(providerAccountRequest)
        replay(providerAccountRequestResolver)
        providerAccountRequestResolver
    }

    private ApplicationResolver initApplicationResolver(MockHttpServletRequest request) {
        application = createNiceMock(Application)
        expect(application.href).andStubReturn(APPLICATION_HREF)
        applicationResolver = createNiceMock(ApplicationResolver)
        expect(applicationResolver.getApplication(request)).andStubReturn(application)
        replay(applicationResolver)
        applicationResolver
    }

    private static void initClient(MockHttpServletRequest request) {
        ApiKey apiKey = createNiceMock(ApiKey)
        expect(apiKey.secret).andStubReturn(SIGNING_KEY)
        expect(apiKey.id).andStubReturn(KEY_ID)
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
        request.setParameter("code", CODE)
        request.setParameter("state", getSignedJws([provider    : PROVIDER_ID,
                                                    redirect_uri: REDIRECT_URI,
                                                    state       : APP_STATE,
                                                    jti         : NONCE_FROM_AUTHORIZE]))

        ProviderAccountResult providerAccountResult = createNiceMock(ProviderAccountResult)
        Account account = createNiceMock(Account)
        expect(account.href).andStubReturn(ACCOUNT_HREF)
        expect(providerAccountResult.account).andReturn(account)
        expect(application.getAccount(providerAccountRequest)).andReturn(providerAccountResult)
        Capture<AuthenticationResult> authenticationResultCapture = new Capture<>()
        authenticationResultSaver.set(eq(request), eq(response), capture(authenticationResultCapture))
        expectLastCall()
        Capture<RequestEvent> requestEventCapture = new Capture<>()
        eventPublisher.publish(capture(requestEventCapture))
        expectLastCall()

        replay(account, application, providerAccountResult, authenticationResultSaver, eventPublisher)

        DateTime now = new DateTime()
        DateTime expectedMaxIat = now.plusMillis(100)
        DateTime expectedMinExp = now.plusSeconds(60)
        DateTime expectedMaxExp = expectedMinExp.plusSeconds(1)
        def actual = controllerUT.handleRequest(request, response)
        assertThat("model view", actual, notNullValue())
        assertThat("redirect", actual.redirect, is(true))
        assertThat("view name", actual.viewName, startsWith(REDIRECT_URI))
        def queryStringMap = getQueryStringMap(actual)
        assertThat("view name query string", queryStringMap, hasKey("state"))
        assertThat("view name query staring", queryStringMap, hasKey("jwtResponse"))
        String jwtResponse = queryStringMap['jwtResponse']
        Jwt<Header, Claims> jwt = Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(jwtResponse)
        assertThat("jwtResponse", jwt, hasHeader("kid", KEY_ID))
        assertThat("jwtResponse", jwt, hasHeader("stt", "assertion"))
        assertThat("jwtResponse", jwt, hasClaim("sub", ACCOUNT_HREF))
        assertThat("jwtResponse", jwt, hasClaim("jti"))
        assertThat("jwtResponse", jwt, hasClaim("irt", NONCE_FROM_AUTHORIZE))
        assertThat("jwtResponse", jwt, hasClaim("iat",
                both(greaterThanOrEqualTo(toNumericDate(now))) &
                        lessThanOrEqualTo(toNumericDate(expectedMaxIat))))
        assertThat("jwtResponse", jwt, hasClaim("exp",
                both(greaterThanOrEqualTo(toNumericDate(expectedMinExp))) &
                        lessThanOrEqualTo(toNumericDate(expectedMaxExp))))
        assertThat("jwtResponse", jwt, hasClaim("iss", application.href))
        assertThat("jwtResponse", jwt, hasClaim("status", "authenticated"))
        assertThat("jwtResponse", jwt, hasClaim("aud", KEY_ID))
        assertThat("jwtResponse", jwt, hasClaim("isNewSub", false))
        assertThat("jwtResponse", jwt, hasClaim("state", APP_STATE))

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

    static int toNumericDate(DateTime dateTime) {
        return dateTime.getMillis() / 1000
    }

    private static Map getQueryStringMap(ViewModel actual) {
        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(URI.create(actual.viewName), "UTF-8")
        def queryStringMap = [:]
        nameValuePairs.each {
            queryStringMap.put(it.name, it.value)
        }
        queryStringMap
    }

    @Test(enabled = false)
    void testSuccessfulCallbackWithOrganizationHref() {
        request.setParameter("code", CODE)
        request.setParameter("state", getSignedJws([provider         : PROVIDER_ID,
                                                    redirect_uri     : REDIRECT_URI,
                                                    organization_href: ORGANIZATION_HREF]))
        // TODO write this test
        Assert.fail("Test not implemented")

    }

    @Test(enabled = false)
    void testSuccessfulCallbackWithOrganizationNameKey() {
        request.setParameter("code", CODE)
        request.setParameter("state", getSignedJws([provider             : PROVIDER_ID,
                                                    redirect_uri         : REDIRECT_URI,
                                                    organziation_name_key: ORGANIZATION_NAME_KEY]))
        // TODO write this test
        Assert.fail("Test not implemented")

    }

}
