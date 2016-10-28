package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.provider.OAuthProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.easymock.IAnswer
import org.springframework.mock.web.MockHttpServletRequest
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasEntry
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.startsWith

abstract class CommonProviderAuthorizationEndpointResolverTest<T extends OAuthProvider> {

    static final String CLIENT_ID = "12345678"
    static final String NEXT_URI = "/next"
    static final String CALLBACK = "/authorize/callback"
    static final String SIGNING_KEY = "not-a-very-secret-key"
    static final String REQUEST_BASE_URI = "https://my-app.com"
    List<String> scopes
    T provider
    MockHttpServletRequest request
    BaseAuthorizationEndpointResolver resolverUT


    protected abstract BaseAuthorizationEndpointResolver newResolverUT();
    protected abstract String getProviderId()
    protected abstract String getBaseUri();


    private static void initClient(MockHttpServletRequest request) {
        ApiKey apiKey = createNiceMock(ApiKey)
        expect(apiKey.secret).andStubReturn(SIGNING_KEY)
        Client client = createNiceMock(Client)
        expect(client.getApiKey()).andStubReturn(apiKey)
        replay(apiKey, client)
        request.setAttribute(Client.class.name, client)
    }

    private static Map<String, String> getState(Map<String, String> params) {
        assertThat(params, hasKey("state"))
        String jwtString = URLDecoder.decode(params['state'], 'UTF-8')
        Jws<Claims> jws = Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(jwtString)
        Map<String, String> body = jws.body as Map<String, String>
        body
    }

    static Map<String, String> extractParams(String href) {
        def paramMatcher = (href =~ /.*\?(.*)/)
        if (paramMatcher) {
            //noinspection GroovyAssignabilityCheck
            def paramList = (paramMatcher[0][1]).split('&') as List<String>
            return paramList.collect({
                return it.split('=').toList()
            }).collectEntries()
        } else {
            return [:]
        }
    }

    @BeforeMethod
    void setUp() {
        provider = createNiceMock(T)
        expect(provider.providerId).andStubReturn(getProviderId())
        expect(provider.clientId).andStubReturn(CLIENT_ID)
        scopes = ['scope1', 'scope2']
        expect(provider.scope).andStubAnswer(new IAnswer<List<String>>() {
            @Override
            List<String> answer() throws Throwable {
                return scopes
            }
        })
        replay(provider)

        request = new MockHttpServletRequest()

        initClient(request)
        request.setRequestURI("/authorize/abc123")
        request.setScheme("https")
        request.setServerName("my-app.com")
        request.setServerPort(443)

        resolverUT = newResolverUT()
        resolverUT.nextUri = NEXT_URI
        resolverUT.callback = CALLBACK
    }



    @Test
    void testProviderId() {
        assertThat(resolverUT.providerId, is(getProviderId()))
    }

    @Test
    void testGetEndpointDefault() {
        def actual = resolverUT.getEndpoint(request, provider)

        assertThat(actual, startsWith(getBaseUri()))
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("client_id", CLIENT_ID))
        assertThat(params, hasEntry("response_type", "code"))
        assertThat(params, hasEntry("scope", URLEncoder.encode("scope1 scope2", 'UTF-8')))
        assertThat(params, hasEntry("redirect_uri", URLEncoder.encode("${REQUEST_BASE_URI}${CALLBACK}", 'UTF-8')))
        Map<String, String> body = getState(params)
        assertThat(body, hasEntry("redirect_uri", REQUEST_BASE_URI + NEXT_URI))
        assertThat(body, hasEntry("provider", provider.providerId))
    }

    @Test
    void testGetEndpointOverrideRedirectUri() {
        request.setParameter("redirect_uri", "/something/else")
        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("redirect_uri", URLEncoder.encode("${REQUEST_BASE_URI}${CALLBACK}", 'UTF-8')))
        Map<String, String> body = getState(params)
        assertThat(body, hasEntry("redirect_uri", REQUEST_BASE_URI + "/something/else"))
    }

    @Test
    void testGetEndpointOvverrideRedirectUriFullyQualified() {
        request.setParameter("redirect_uri", "https://foo.com/something/else")
        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("redirect_uri", URLEncoder.encode("${REQUEST_BASE_URI}${CALLBACK}", 'UTF-8')))
        Map<String, String> body = getState(params)
        assertThat(body, hasEntry("redirect_uri", "https://foo.com/something/else"))
    }
    @Test
    void testGetEndpointOverrideScope() {
        request.setParameter("scope", "foo bar baz")

        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("scope", URLEncoder.encode("foo bar baz", 'UTF-8')))
    }

    @Test
    void testGetEndpointProvidedState() {
        request.setParameter("state", "some-string")
        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry(equalTo("state"), not(equalTo("some-string"))))
        Map<String, String> body = getState(params)
        assertThat(body, hasEntry("state", "some-string"))
    }

    @Test
    void testGetEndpointWithExtraParameters() {
        request.setParameter("foo", "bar")
        request.setParameter("baz", "qux")
        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("foo", "bar"))
        assertThat(params, hasEntry("baz", "qux"))
    }

    @Test
    void testGetEndpointWithExtraReservedParameters() {
        request.setParameter("foo", "bar")
        request.setParameter("response_type", "baz")
        request.setParameter("client_id", "qux")
        def actual = resolverUT.getEndpoint(request, provider)
        Map<String, String> params = extractParams(actual)
        assertThat(params, hasEntry("foo", "bar"))
        assertThat(params, hasEntry("client_id", CLIENT_ID))
        assertThat(params, hasEntry("response_type", "code"))
    }
}
