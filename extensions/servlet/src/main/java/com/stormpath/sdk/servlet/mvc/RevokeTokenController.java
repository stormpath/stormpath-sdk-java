/*
 * Copyright 2016 Stormpath, Inc.
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
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.OAuthRevocationRequest;
import com.stormpath.sdk.oauth.OAuthRevocationRequestBuilder;
import com.stormpath.sdk.oauth.OAuthTokenRevocators;
import com.stormpath.sdk.oauth.TokenTypeHint;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.oauth.OAuthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.2.0.
 */
public class RevokeTokenController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokenController.class);

    private final static String TOKEN = "token";

    private final static String TOKEN_TYPE_HINT = "token_type_hint";

    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

        OAuthRevocationRequestBuilder builder = OAuthRequests.OAUTH_TOKEN_REVOCATION_REQUEST.builder();

        String tokenTypeHint = request.getParameter(TOKEN_TYPE_HINT);

        if (Strings.hasText(tokenTypeHint)) {
            builder.setTokenTypeHint(TokenTypeHint.fromValue(tokenTypeHint));
        }

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");

        try {
            String token = request.getParameter(TOKEN);

            if (!Strings.hasText(token)) {
                throw new OAuthException(OAuthErrorCode.INVALID_REQUEST);
            }

            this.revoke(getApplication(request), builder.setToken(token).build());

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Length", "0");
        } catch (OAuthException e) {

            log.debug("Error occurred revoking token: {}", e.getMessage());

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            String json = e.toJson();

            response.setHeader("Content-Length", String.valueOf(json.length()));
            response.getWriter().print(json);
            response.getWriter().flush();
        }
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

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    protected Application getApplication(HttpServletRequest request) {
        Application application = (Application) request.getAttribute(Application.class.getName());
        Assert.notNull(application, "request must have an application attribute.");
        return application;
    }

}
