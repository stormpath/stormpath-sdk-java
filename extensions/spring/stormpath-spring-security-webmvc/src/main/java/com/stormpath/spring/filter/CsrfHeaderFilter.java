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
package com.stormpath.spring.filter;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that allows the CSRF token to be placed in the header.
 *
 * https://github.com/stormpath/stormpath-sdk-java/issues/717
 *
 * @since 1.0.0
 */
public class CsrfHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrf != null) {
            // Spring Security will allow the Token to be included in this header name
            response.setHeader("X-CSRF-HEADER", csrf.getHeaderName());

            // Spring Security will allow the token to be included in this parameter name
            response.setHeader("X-CSRF-PARAM", csrf.getParameterName());

            // this is the value of the token to be included as either a header or an HTTP parameter
            response.setHeader("X-CSRF-TOKEN", csrf.getToken());
        }

        filterChain.doFilter(request, response);
    }
}