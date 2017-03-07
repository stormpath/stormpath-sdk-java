/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.*;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.oauth.OAuthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OAuthException;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Saver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * https://github.com/stormpath/stormpath-sdk-java/issues/1247
 *
 * @since 1.5.0
 */
public class RevokeTokenController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokenController.class);

    private final static String TOKEN = "token";
    private final static String TOKEN_TYPE_HINT = "token_type_hint";

    private Saver<AuthenticationResult> authenticationResultSaver;

    public void init() {
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String method = request.getMethod();

        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            return doPost(request, response);
        }

        return super.handleRequest(request, response);
    }

    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        OAuthRevocationRequestBuilder builder = OAuthRequests.OAUTH_TOKEN_REVOCATION_REQUEST.builder();

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");

        try {

            //Form media type is required: https://tools.ietf.org/html/rfc6749#section-4.3.2
            String contentType = Strings.clean(request.getContentType());
            if (contentType == null || !contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                String msg = "Content-Type must be " + MediaType.APPLICATION_FORM_URLENCODED_VALUE;
                throw new OAuthException(OAuthErrorCode.INVALID_REQUEST, msg, null);
            }

            String tokenTypeHint = request.getParameter(TOKEN_TYPE_HINT);

            if (Strings.hasText(tokenTypeHint)) {
                builder.setTokenTypeHint(TokenTypeHint.fromValue(tokenTypeHint));
            }

            String token = request.getParameter(TOKEN);

            if (!Strings.hasText(token)) {
                throw new OAuthException(OAuthErrorCode.INVALID_REQUEST);
            }

            this.revoke(getApplication(request), builder.setToken(token).build());

            authenticationResultSaver.set(request, response, null);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Length", "0");

        } catch (OAuthException e) {

            log.debug("Error occurred revoking token: {}", e.getMessage());

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            String json = e.toJson();

            response.setHeader("Content-Length", String.valueOf(json.length()));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(json);
            response.getWriter().flush();
        }

        //we rendered the response directly - no need for a view to be resolved, so return null:
        return null;
    }

    private void revoke(Application application, OAuthRevocationRequest request) throws OAuthException {
        try {
            OAuthTokenRevocators.OAUTH_TOKEN_REVOCATOR.forApplication(application).revoke(request);
        } catch (ResourceException e) {
            com.stormpath.sdk.error.Error error = e.getStormpathError();
            String message = error.getMessage();

            OAuthErrorCode oauthError = OAuthErrorCode.INVALID_REQUEST;
            if (error instanceof DefaultError) {
                Object errorObject = ((DefaultError) error).getProperty("error");
                oauthError = errorObject == null ? oauthError : new OAuthErrorCode(errorObject.toString());
            }

            throw new OAuthException(oauthError, message);
        }
    }

}
