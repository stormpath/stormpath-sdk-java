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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class ContentNegotiationAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ContentNegotiationAuthenticationFilter.class);

    private boolean postOnly = true;
    private List<MediaType> supportedMediaTypes;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (supportedMediaTypes == null) {
            throw new AuthenticationServiceException("A list of supported media types must be set.");
        }

        MediaType mediaType;
        try {
            mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, response, supportedMediaTypes);
        } catch (UnresolvedMediaTypeException umt) {
            throw new AuthenticationServiceException("Unresolved media type: " + umt.getMessage(), umt);
        }

        log.debug("mediaType: {}", mediaType);
        log.debug("request.getContentType(): {}", request.getContentType());
        // the || is to handle an edge case where an Accept: */* is set but the content-type is application/x-www-form-urlencoded
        // a browser would not do this, but this guards against command line tomfoolery.
        if (!MediaType.APPLICATION_JSON.equals(mediaType) ||
                request.getHeader("accept").contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) ||
                request.getContentType().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return super.attemptAuthentication(request, response);
        }

        UsernamePasswordAuthenticationToken authRequest = getUserNamePasswordAuthenticationToken(request);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @SuppressWarnings("unchecked")
    private UsernamePasswordAuthenticationToken getUserNamePasswordAuthenticationToken(HttpServletRequest request) {
        String body = getRequestBody(request);

        Map<String, String> loginProps;
        try {
            loginProps = new ObjectMapper().readValue(body, HashMap.class);
        } catch(IOException ex) {
            log.error("Couldn't map request body: '{}': {}", body, ex.getMessage(), ex);
            return null;
        }

        return new UsernamePasswordAuthenticationToken(loginProps.get("login"), loginProps.get("password"));
    }

    private String getRequestBody(HttpServletRequest request) {
        BufferedReader bufferedReader = null;
        StringBuffer sb = new StringBuffer();
        try {
            bufferedReader =  request.getReader();
            char[] charBuffer = new char[128];
            int bytesRead;
            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
                sb.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            log.error("Problem reading request body: {}", ex.getMessage(), ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("Problem closing reader: {}", ex.getMessage(), ex);
                }
            }
        }
        return sb.toString();
    }
}
