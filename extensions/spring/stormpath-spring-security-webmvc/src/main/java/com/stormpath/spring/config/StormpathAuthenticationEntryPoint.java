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
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/714
 *
 * @since 1.0.0
 */
public class StormpathAuthenticationEntryPoint implements AuthenticationEntryPoint {

    protected String loginUri;

    public StormpathAuthenticationEntryPoint(String loginUri) {
        Assert.notNull(loginUri, "loginUri cannot be null");
        this.loginUri = loginUri;
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
            response.sendRedirect(this.loginUri);
            response.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
        }
    }

}