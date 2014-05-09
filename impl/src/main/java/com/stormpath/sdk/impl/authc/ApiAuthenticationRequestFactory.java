package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.error.authc.MissingApiKeyException;
import com.stormpath.sdk.error.authc.UnsupportedAuthenticationSchemeException;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.impl.error.DefaultErrorBuilder;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.authc.BearerLocation;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * ApiAuthenticationRequestFactory
 *
 * @since 1.0.RC
 */
public class ApiAuthenticationRequestFactory {

    private static final String BASIC_OAUTH_REQUEST_FQCN = "com.stormpath.sdk.impl.oauth.authc.DefaultBasicOauthAuthenticationRequest";

    private static final String BEARER_OAUTH_REQUEST_FQCN = "com.stormpath.sdk.impl.oauth.authc.DefaultBearerOauthAuthenticationRequestBuilder";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String BASIC_AUTHENTICATION_SCHEME = "Basic";
    public static final String BEARER_AUTHENTICATION_SCHEME = "Bearer";

    public static final String GRANT_TYPE_PARAMETER = "grant_type";

    private final static Set<String> SUPPORTED_AUTHENTICATION_SCHEMES;

    static {
        Set<String> tempSet = new HashSet<String>();

        tempSet.add(BASIC_AUTHENTICATION_SCHEME);
        tempSet.add(BEARER_AUTHENTICATION_SCHEME);

        SUPPORTED_AUTHENTICATION_SCHEMES = Collections.unmodifiableSet(tempSet);
    }

    public AuthenticationRequest createFrom(HttpServletRequest httpServletRequest) {

        String authzHeaderValue = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return (AuthenticationRequest) Classes.newInstance(BEARER_OAUTH_REQUEST_FQCN, httpServletRequest, new BearerLocation[]{BearerLocation.HEADER});
        }

        for (Enumeration<String> parameterNames = httpServletRequest.getParameterNames(); parameterNames.hasMoreElements(); ) {

            String parameterName = parameterNames.nextElement();

            if (GRANT_TYPE_PARAMETER.equals(parameterName)) {
                return (AuthenticationRequest) Classes.newInstance(BASIC_OAUTH_REQUEST_FQCN, httpServletRequest, null);
            }
        }

        return new BasicApiAuthenticationRequest(httpServletRequest);
    }

    public AuthenticationRequest createFrom(HttpRequest httpRequest) {

        String authzHeaderValue = httpRequest.getHeader(AUTHORIZATION_HEADER);

        String[] schemeAndValue = getSchemeAndValue(authzHeaderValue);

        if (schemeAndValue[0].equalsIgnoreCase(BEARER_AUTHENTICATION_SCHEME)) {
            return (AuthenticationRequest) Classes.newInstance(BEARER_OAUTH_REQUEST_FQCN, httpRequest, new BearerLocation[]{BearerLocation.HEADER});
        }

        return new BasicApiAuthenticationRequest(httpRequest);
    }


    private void validateSupportedScheme(String scheme) {
        for (String supportedSchema : SUPPORTED_AUTHENTICATION_SCHEMES) {
            if (supportedSchema.equalsIgnoreCase(scheme)) {
                return;
            }
        }
        throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(UnsupportedAuthenticationSchemeException.class);
    }

    private String[] getSchemeAndValue(String authzHeaderValue) {

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
}
