package com.stormpath.sdk.servlet.filter.oauth

import com.stormpath.sdk.lang.Strings
import com.stormpath.sdk.servlet.config.Config
import com.stormpath.sdk.servlet.config.ConfigLoader
import com.stormpath.sdk.servlet.filter.ServerUriResolver
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.http.Resolver
import org.powermock.modules.testng.PowerMockTestCase
import org.springframework.mock.web.MockServletContext
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.stormpath.sdk.servlet.filter.oauth.config.OriginAccessTokenRequestAuthorizerFactory.*
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.same
import static org.powermock.api.easymock.PowerMock.*
import static org.testng.Assert.*
/**
 * Tests to confirm that Origin header is not required for JSON requests:
 * <a href="https://github.com/stormpath/stormpath-sdk-java/issues/659">#659</a>.
 *
 * @since 1.0.0
 */
class OriginAccessTokenRequestAuthorizerTest extends PowerMockTestCase {

    MockServletContext mockServletContext
    Config config
    ServerUriResolver resolver
    Collection<String> additionalOriginUris
    List<MediaType> producesMimeTypes

    @BeforeClass
    void setup() {
        mockServletContext = new MockServletContext()
        config = new ConfigLoader().createConfig(mockServletContext)

        // copied from OriginAccessTokenRequestAuthorizerFactory since it's not visible
        resolver = config.getInstance(SERVER_URI_RESOLVER);
        String uris = config.get(ORIGIN_URIS);
        String produces = config.get(PRODUCES_MIME_TYPES);

        additionalOriginUris = Collections.emptyList();

        if (Strings.hasText(uris)) {
            String[] values = Strings.split(uris);
            additionalOriginUris = Arrays.asList(values);
        }

        producesMimeTypes = MediaType.parseMediaTypes(produces);
    }

    @Test
    void testPasswordGrantWithOrigin() {
        def localhostResolver = createMock(Resolver.class)
        producesMimeTypes = MediaType.parseMediaTypes("text/html,application/json")
        def authorizer = new OriginAccessTokenRequestAuthorizer(resolver, localhostResolver, additionalOriginUris, producesMimeTypes);

        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.ACCEPTS_HEADER_NAME)).andReturn("*/*")
        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.ORIGIN_HEADER_NAME)).andReturn("http://localhost:8080")
        expect(localhostResolver.get(same(request), same(response))).andReturn(true)

        // called by serverUriResolver
        expect(request.getScheme()).andReturn("http")
        expect(request.getHeader("Host")).andReturn("localhost:8080");

        replay request, response, localhostResolver

        try {
            authorizer.assertAuthorized(request, response)
        } finally {
            verify request, response, localhostResolver
        }
    }

    @Test
    void testPasswordGrantWithoutOrigin() {
        def localhostResolver = createMock(Resolver.class)
        producesMimeTypes = MediaType.parseMediaTypes("text/html,application/json")
        def authorizer = new OriginAccessTokenRequestAuthorizer(resolver, localhostResolver, additionalOriginUris, producesMimeTypes);

        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.ACCEPTS_HEADER_NAME)).andReturn("*/*")
        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.ORIGIN_HEADER_NAME)).andReturn(null)
        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.REFERER_HEADER_NAME)).andReturn(null)
        expect(request.getRemoteAddr()).andReturn(null)
        expect(localhostResolver.get(same(request), same(response))).andReturn(true)

        replay request, response, localhostResolver

        try {
            authorizer.assertAuthorized(request, response)
        } catch (OAuthException exception) {
            assertNotNull exception
            assertEquals(exception.getMessage(), "Missing Origin or Referer header (Origin preferred).")
        } finally {
            verify request, response, localhostResolver
        }
    }

    @Test
    void testPasswordGrantWithAcceptsJSON() {
        def localhostResolver = createMock(Resolver.class)
        producesMimeTypes = MediaType.parseMediaTypes("text/html,application/json")
        def authorizer = new OriginAccessTokenRequestAuthorizer(resolver, localhostResolver, additionalOriginUris, producesMimeTypes);

        HttpServletRequest request = createMock(HttpServletRequest.class)
        HttpServletResponse response = createMock(HttpServletResponse.class)

        expect(request.getHeader(OriginAccessTokenRequestAuthorizer.ACCEPTS_HEADER_NAME)).andReturn("application/json")

        replay request, response, localhostResolver

        try {
            authorizer.assertAuthorized(request, response)
        } finally {
            verify request, response, localhostResolver
        }
    }
}

