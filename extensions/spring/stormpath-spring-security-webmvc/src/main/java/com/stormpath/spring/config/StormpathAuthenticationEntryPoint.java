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
package com.stormpath.spring.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.filter.DefaultLoginPageRedirector;
import com.stormpath.sdk.servlet.filter.LoginPageRedirector;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/714
 *
 * @since 1.0.0
 */
public class StormpathAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(StormpathAuthenticationEntryPoint.class);

    private String loginUri;
    private String meUri;
    private final List<MediaType> supportedMediaTypes;
    private final LoginPageRedirector loginPageRedirector;

    public StormpathAuthenticationEntryPoint(String loginUri, String produces, String meUri) {
        Assert.hasText(loginUri, "loginUri cannot be null or empty");
        Assert.hasText(produces, "produces cannot be null or empty");
        Assert.hasText(meUri, "meUri cannot be null or empty");

        this.loginUri = loginUri;
        this.meUri = meUri;
        this.supportedMediaTypes = MediaType.parseMediaTypes(produces);
        this.loginPageRedirector = new DefaultLoginPageRedirector(loginUri);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        sendRedirect(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    private void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!isAuthenticated()) {
            try {
                MediaType mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, response, supportedMediaTypes);

                //Me is a special case, if the content type was application/json it should return empty response with 401 status
                //TCK test MeIT#meFailsOnUnauthenticatedRequest()
                if (MediaType.APPLICATION_JSON.equals(mediaType) && request.getRequestURI().startsWith(meUri)) {
                    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                } else {
                    loginPageRedirector.redirectToLoginPage(request, response);
                }
            } catch (UnresolvedMediaTypeException e) {
                log.error("Couldn't resolve media type: {}", e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't redirect to login", e);
            }
        }
    }

}