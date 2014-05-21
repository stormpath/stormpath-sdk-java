package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.error.authc.OauthAuthenticationException;
import com.stormpath.sdk.error.authc.UnsupportedAuthenticationSchemeException;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ApiAuthenticationRequestFactory
 *
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

        Object servletRequest = servletRequestWrapper.getHttpServletRequest();

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return createOauthRequest(BEARER_OAUTH_REQUEST_CLASS, servletRequest);
        }

        for (Enumeration<String> parameterNames = servletRequestWrapper.getParameterNames(); parameterNames.hasMoreElements(); ) {
            String parameterName = parameterNames.nextElement();

            if (GRANT_TYPE_PARAMETER.equals(parameterName)) {
                return createOauthRequest(BASIC_OAUTH_REQUEST_CLASS, servletRequest);
            }
        }
        return new DefaultBasicApiAuthenticationRequest(servletRequestWrapper);
    }

    public AuthenticationRequest createFrom(HttpRequest httpRequest) {
        String authzHeaderValue = httpRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return createOauthRequest(BEARER_OAUTH_REQUEST_CLASS, httpRequest);
        }

        Map<String, String[]> parametersMap = httpRequest.getParameters();

        if (parametersMap != null && parametersMap.containsKey(GRANT_TYPE_PARAMETER)) {
            return createOauthRequest(BASIC_OAUTH_REQUEST_CLASS, httpRequest);
        }
        return new DefaultBasicApiAuthenticationRequest(httpRequest);
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

    protected String[] getSchemeAndValue(String authzHeaderValue) {
        if (authzHeaderValue == null) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(MissingApiKeyException.class);
        }
        String[] tokens = authzHeaderValue.split(" ", 2);

        if (tokens == null || tokens.length != 2) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(MissingApiKeyException.class);
        }
        validateSupportedScheme(tokens[0]);
        return tokens;
    }

    private void validateSupportedScheme(String scheme) {
        for (String supportedSchema : SUPPORTED_AUTHENTICATION_SCHEMES) {
            if (supportedSchema.equalsIgnoreCase(scheme)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(UnsupportedAuthenticationSchemeException.class);
    }
}
