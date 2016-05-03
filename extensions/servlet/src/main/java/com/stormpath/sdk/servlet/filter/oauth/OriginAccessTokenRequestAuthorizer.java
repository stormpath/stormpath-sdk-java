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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;

/**
 * @since 1.0.RC3
 */
public class OriginAccessTokenRequestAuthorizer implements RequestAuthorizer {

    private static final Logger log = LoggerFactory.getLogger(OriginAccessTokenRequestAuthorizer.class);

    public static final String ORIGIN_HEADER_NAME = "Origin";
    //yes, 'Referer' is supposed to be spelled incorrectly: https://tools.ietf.org/html/rfc7231#section-5.5.2 :
    public static final String REFERER_HEADER_NAME = "Referer";
    public static final String ORIGIN_URIS_CONFIG_PROPERTY_NAME =
        "stormpath.web.accessToken.origin.authorizer.originUris";

    private final ServerUriResolver serverUriResolver;
    private final Resolver<Boolean> localhost;
    private final Collection<String> authorizedOriginUrls;

    public OriginAccessTokenRequestAuthorizer(ServerUriResolver serverUriResolver, Resolver<Boolean> localhost,
                                              Collection<String> authorizedOriginUrls) {
        Assert.notNull(serverUriResolver, "ServerUriResolver cannot be null.");
        Assert.notNull(localhost, "localhost resolver cannot be null.");
        this.serverUriResolver = serverUriResolver;
        this.localhost = localhost;
        if (authorizedOriginUrls == null) {
            this.authorizedOriginUrls = Collections.emptyList();
        } else {
            this.authorizedOriginUrls = authorizedOriginUrls;
        }
    }

    public ServerUriResolver getServerUriResolver() {
        return serverUriResolver;
    }

    public Resolver<Boolean> getLocalhostResolver() {
        return localhost;
    }

    public Collection<String> getAuthorizedOriginUrls() {
        return authorizedOriginUrls;
    }

    @Override
    public void assertAuthorized(HttpServletRequest request, HttpServletResponse response) throws OAuthException {

        boolean localhostClient = isLocalhostClient(request, response);

        String origin = Strings.clean(request.getHeader(ORIGIN_HEADER_NAME));
        boolean fallbackToReferer = false;

        if (origin == null) {
            //fall back to referer:
            origin = Strings.clean(request.getHeader(REFERER_HEADER_NAME));
            if (origin != null) {
                fallbackToReferer = true;
            }
        }

        if (!Strings.hasText(origin)) {

            String errorMessage = null;

            if (localhostClient) {
                //convenient message during localhost testing:
                errorMessage = "Missing Origin or Referer header (Origin preferred).";
            }

            //it is unexpected that a modern browser wouldn't send the Origin or Referer header.
            //Because of this, the request could likely represent someone doing something sneaky, so we should
            //deny the request and not give them a specific error message: a detailed error message
            //would tell them how to fix the issue and side-step the authorization check
            //since you could just easily spoof the origin header if not a browser:

            //however, we will log a message though production environments can see why the
            //request failed:
            log.debug(
                "Request client (remoteAddr={}) did not specify an Origin or Referer header. Access Token request is denied",
                request.getRemoteAddr());

            throw new OAuthException(OAuthErrorCode.INVALID_CLIENT, errorMessage, null);
        }

        if (!isAuthorizedOrigin(request, response, origin)) {

            String errorMessage = null;

            if (localhostClient) {
                //give a specific message to the developer:
                errorMessage =
                    "Unauthorized request " + (fallbackToReferer ? "origin (via Referer header)." : "Origin.");
            }

            // otherwise don't give a potentially-malicious client any information as to why the request failed
            // but we will log the message:
            log.debug("Unauthorized {} header value: {}.  If this is unexpected, you might want to specify " +
                      "one or more comma-delimited URLs via the {} property.",
                      new Object[]{ (fallbackToReferer ? REFERER_HEADER_NAME : ORIGIN_HEADER_NAME), origin,
                          ORIGIN_URIS_CONFIG_PROPERTY_NAME });

            throw new OAuthException(OAuthErrorCode.INVALID_CLIENT, errorMessage, null);
        }
    }

    protected boolean isAuthorizedOrigin(HttpServletRequest request, HttpServletResponse response, String origin) {

        String requestedServerUri = getServerUriResolver().getServerUri(request);

        if (origin.startsWith(requestedServerUri)) {
            return true;
        }

        for (String authorizedOriginUri : getAuthorizedOriginUrls()) {
            if (origin.startsWith(authorizedOriginUri)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isLocalhostClient(HttpServletRequest request, HttpServletResponse response) {
        return getLocalhostResolver().get(request, response);
    }
}
