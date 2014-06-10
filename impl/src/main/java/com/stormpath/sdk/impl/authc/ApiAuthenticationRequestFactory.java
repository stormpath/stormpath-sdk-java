package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.error.authc.InvalidAuthenticationException;
import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.error.authc.UnsupportedAuthenticationSchemeException;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.authc.RequestLocation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @since 1.0.RC
 */
public class ApiAuthenticationRequestFactory {

    private static final String OAUTH_REQUEST_NOT_AVAILABLE_MSG;

    private static final String BASIC_OAUTH_REQUEST_FQCN = "com.stormpath.sdk.impl.oauth.authc.DefaultBasicOauthAuthenticationRequest";

    private static final String BEARER_OAUTH_REQUEST_FQCN = "com.stormpath.sdk.impl.oauth.authc.DefaultBearerOauthAuthenticationRequest";

    private static final Class<AuthenticationRequest> BASIC_OAUTH_REQUEST_CLASS;

    private static final Class<AuthenticationRequest> BEARER_OAUTH_REQUEST_CLASS;

    private final static Set<String> SUPPORTED_AUTHENTICATION_SCHEMES;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    public static final String BASIC_AUTHENTICATION_SCHEME = "Basic";

    public static final String BEARER_AUTHENTICATION_SCHEME = "Bearer";

    public static final String GRANT_TYPE_PARAMETER = "grant_type";

    static {
        if (Classes.isAvailable(BASIC_OAUTH_REQUEST_FQCN)) {
            BASIC_OAUTH_REQUEST_CLASS = Classes.forName(BASIC_OAUTH_REQUEST_FQCN);
        } else {
            BASIC_OAUTH_REQUEST_CLASS = null;
        }

        if (Classes.isAvailable(BEARER_OAUTH_REQUEST_FQCN)) {
            BEARER_OAUTH_REQUEST_CLASS = Classes.forName(BEARER_OAUTH_REQUEST_FQCN);
        } else {
            BEARER_OAUTH_REQUEST_CLASS = null;
        }

        OAUTH_REQUEST_NOT_AVAILABLE_MSG = "Unable to find the OauthRequest implementation on the classpath. Please ensure you have added the stormpath-sdk-oauth-{version}.jar " +
                "file to your runtime classpath.";

        Set<String> tempSet = new HashSet<String>();

        tempSet.add(BASIC_AUTHENTICATION_SCHEME);
        tempSet.add(BEARER_AUTHENTICATION_SCHEME);

        SUPPORTED_AUTHENTICATION_SCHEMES = Collections.unmodifiableSet(tempSet);
    }

    public AuthenticationRequest createFrom(HttpServletRequestWrapper servletRequestWrapper) {
        String authzHeaderValue = servletRequestWrapper.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        Object httpRequest = servletRequestWrapper.getHttpServletRequest();

        if (schemeAndValue == null) {

            HttpMethod method = HttpMethod.fromName(servletRequestWrapper.getMethod());

            RequestLocation[] requestLocations = getRequestLocations(false, method, servletRequestWrapper.getHeader(CONTENT_TYPE_HEADER));

            if (requestLocations.length > 0) {
                return createResourceOauthRequest(BEARER_OAUTH_REQUEST_CLASS, httpRequest, requestLocations);
            }
        } else {
            if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {

                RequestLocation[] requestLocations = getRequestLocations(true, HttpMethod.fromName(servletRequestWrapper.getMethod()), servletRequestWrapper.getHeader(CONTENT_TYPE_HEADER));

                return createResourceOauthRequest(BEARER_OAUTH_REQUEST_CLASS, httpRequest, requestLocations);
            }

            if (schemeAndValue[0].equalsIgnoreCase(BASIC_AUTHENTICATION_SCHEME)) {

                if (hasContentType(servletRequestWrapper.getHeader(CONTENT_TYPE_HEADER), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    Map<String, String[]> parameterMap = servletRequestWrapper.getParameterMap();

                    if (parameterMap != null && parameterMap.containsKey(GRANT_TYPE_PARAMETER)) {
                        return createOauthRequest(BASIC_OAUTH_REQUEST_CLASS, httpRequest);
                    }
                }
                return new DefaultBasicApiAuthenticationRequest(servletRequestWrapper);
            }
        }
        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(InvalidAuthenticationException.class);
    }

    public AuthenticationRequest createFrom(HttpRequest httpRequest) {
        String authzHeaderValue = httpRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue == null) {

            HttpMethod method = httpRequest.getMethod();

            RequestLocation[] requestLocations = getRequestLocations(false, method, httpRequest.getHeader(CONTENT_TYPE_HEADER));

            if (requestLocations.length > 0) {
                return createResourceOauthRequest(BEARER_OAUTH_REQUEST_CLASS, httpRequest, requestLocations);
            }
        } else {
            if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {

                RequestLocation[] requestLocations = getRequestLocations(true, httpRequest.getMethod(), httpRequest.getHeader(CONTENT_TYPE_HEADER));

                return createResourceOauthRequest(BEARER_OAUTH_REQUEST_CLASS, httpRequest, requestLocations);
            }

            if (schemeAndValue[0].equalsIgnoreCase(BASIC_AUTHENTICATION_SCHEME)) {

                if (hasContentType(httpRequest.getHeader(CONTENT_TYPE_HEADER), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    Map<String, String[]> parameterMap = httpRequest.getParameters();

                    if (parameterMap != null && parameterMap.containsKey(GRANT_TYPE_PARAMETER)) {
                        return createOauthRequest(BASIC_OAUTH_REQUEST_CLASS, httpRequest);
                    }
                }
                return new DefaultBasicApiAuthenticationRequest(httpRequest);
            }
        }

        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(InvalidAuthenticationException.class);
    }

    private AuthenticationRequest createOauthRequest(Class<AuthenticationRequest> oauthRequestClass, Object httpRequest) {
        Assert.notNull(oauthRequestClass, OAUTH_REQUEST_NOT_AVAILABLE_MSG);
        Constructor<AuthenticationRequest> constructor = Classes.getConstructor(oauthRequestClass, Object.class);

        try {
            return Classes.instantiate(constructor, httpRequest);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }
    }

    private AuthenticationRequest createResourceOauthRequest(Class<AuthenticationRequest> oauthRequestClass, Object httpRequest, RequestLocation[] requestLocations) {
        Assert.notNull(oauthRequestClass, OAUTH_REQUEST_NOT_AVAILABLE_MSG);
        Constructor<AuthenticationRequest> constructor = Classes.getConstructor(oauthRequestClass, Object.class, RequestLocation[].class);

        try {
            return Classes.instantiate(constructor, httpRequest, requestLocations);
        } catch (Exception e) {
            throw ApiAuthenticationExceptionFactory.newOauthException(OauthAuthenticationException.class, OauthAuthenticationException.INVALID_REQUEST);
        }
    }

    protected String[] getSchemeAndValue(String authzHeaderValue) {
        if (authzHeaderValue == null) {
            return null;
        }
        String[] tokens = authzHeaderValue.split(" ", 2);

        if (tokens == null || tokens.length != 2) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(MissingApiKeyException.class);
        }
        validateSupportedScheme(tokens[0]);
        return tokens;
    }

    private void validateSupportedScheme(String scheme) {
        for (String supportedScheme : SUPPORTED_AUTHENTICATION_SCHEMES) {
            if (supportedScheme.equalsIgnoreCase(scheme)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(UnsupportedAuthenticationSchemeException.class);
    }

    protected boolean hasContentType(String requestContentType, String requiredContentType) {
        if (!Strings.hasText(requiredContentType) || !Strings.hasText(requestContentType)) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(requestContentType, ";");
        while (tokenizer.hasMoreTokens()) {
            if (requiredContentType.equals(tokenizer.nextToken())) {
                return true;
            }
        }

        return false;
    }

    protected RequestLocation[] getRequestLocations(boolean addHeaderLocation, HttpMethod httpMethod, String requestContentType) {
        List<RequestLocation> requestLocationList = new ArrayList<RequestLocation>();
        if (addHeaderLocation) {
            requestLocationList.add(RequestLocation.HEADER);
        }

        EnumSet<HttpMethod> bodyLocationMethods = EnumSet.of(HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT);

        if (bodyLocationMethods.contains(httpMethod) && hasContentType(requestContentType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            requestLocationList.add(RequestLocation.BODY);
        }
        RequestLocation[] requestLocations = new RequestLocation[requestLocationList.size()];
        return requestLocationList.toArray(requestLocations);
    }
}
