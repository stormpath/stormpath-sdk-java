/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class DefaultAccessTokenRequestAuthorizer implements RequestAuthorizer {

    public static final String FORM_MEDIA_TYPE = "application/x-www-form-urlencoded";

    public static final String GRANT_TYPE_PARAM_NAME = "grant_type";

    private final Resolver<Boolean> secureConnectionRequired;
    private final RequestAuthorizer originAuthorizer;

    public DefaultAccessTokenRequestAuthorizer(Resolver<Boolean> secureConnectionRequired, RequestAuthorizer originAuthorizer) {
        Assert.notNull(secureConnectionRequired, "secure resolver cannot be null.");
        Assert.notNull(originAuthorizer, "origin RequestAuthorizer cannot be null.");
        this.secureConnectionRequired = secureConnectionRequired;
        this.originAuthorizer = originAuthorizer;
    }

    public Resolver<Boolean> getSecureConnectionRequired() {
        return secureConnectionRequired;
    }

    public RequestAuthorizer getOriginAuthorizer() {
        return originAuthorizer;
    }

    @Override
    public void assertAuthorized(HttpServletRequest request, HttpServletResponse response) throws OauthException {


        //POST is required: https://tools.ietf.org/html/rfc6749#section-3.2
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            String msg = "HTTP POST is required.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        //Form media type is required: https://tools.ietf.org/html/rfc6749#section-4.3.2
        String contentType = Strings.clean(request.getContentType());
        if (contentType == null || !contentType.startsWith(FORM_MEDIA_TYPE)) {
            String msg = "Content-Type must be " + FORM_MEDIA_TYPE;
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        //grant_type is always required for all token requests:
        String grantType = Strings.clean(request.getParameter(GRANT_TYPE_PARAM_NAME));
        if (grantType == null) {
            String msg = "Missing grant_type value.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }

        //Secure connections are required: https://tools.ietf.org/html/rfc6749#section-3.2
        assertSecure(request, response);

        //assert Origin header matches expected conditions (prevent any random JS client on the web from submitting
        //token requests):
        assertOriginAuthorized(request, response);
    }

    /**
     * Asserts that the OAuth token request is secure as mandated by <a href="https://tools.ietf.org/html/rfc6749#section-3.2">https://tools.ietf.org/html/rfc6749#section-3.2</a>,
     * and if not, throws an appropriate {@link com.stormpath.sdk.servlet.filter.oauth.OauthException OauthException}.
     *
     * <p>This implementation delegates to {@link #isSecureConnectionRequired(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse) isSecureConnectionRequired(request,response)}, and if not secure, throws
     * an exception with an appropriate message, otherwise this method returns quietly.</p>
     *
     * @param request  inbound request
     * @param response outbound response
     * @throws com.stormpath.sdk.servlet.filter.oauth.OauthException if the request is not secure.
     */
    protected void assertSecure(HttpServletRequest request, HttpServletResponse response) throws OauthException {
        if (isSecureConnectionRequired(request, response) && !request.isSecure()) {
            String msg = "A secure HTTPS connection is required for token requests - this is " +
                         "a requirement of the OAuth 2 specification.";
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, msg, null);
        }
    }

    protected boolean isSecureConnectionRequired(HttpServletRequest request, HttpServletResponse response) {
        return getSecureConnectionRequired().get(request, response);
    }

    protected void assertOriginAuthorized(HttpServletRequest request, HttpServletResponse response) throws OauthException {
        getOriginAuthorizer().assertAuthorized(request, response);
    }

}
